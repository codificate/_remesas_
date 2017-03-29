package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Corridor;
import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.ExchangeRate;
import net.iantech.android.remitee.model.Trx;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Lucas on 8/1/2016.
 */
public class QuoteAmountFragment extends Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private final String LOG_TAG = QuoteAmountFragment.class.getSimpleName();
    private ProgressDialog progressDialog;
    private EditText quote_amount_source_edittext;
    private EditText quote_amount_target_edittext;
    private double minAmountInLocalCurrency = 0;

    private TextView quote_amount_local_reference_textview;
    private TextView quote_amount_usd_reference_textview;
    private TextView quote_agent_discount_reference_textview;

    private RemiteeApp app;
    private Locale localeSpanish = new Locale("es", "AR");

    public QuoteAmountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

        GetCorridorTask getCorridorTask = new GetCorridorTask(getActivity());
        getCorridorTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quote_amount, container, false);
        if (rootView == null || app.currentTargetCountry == null)
            return null;

        Button quoteAmmountNextButton = (Button) rootView.findViewById(R.id.quoteAmmountNextButton);
        if (quoteAmmountNextButton != null) {
            quoteAmmountNextButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    UpdateAmount();
                }
            });
        }

        //Si la trx es local oculto la tabla
        LinearLayout quote_amount_source_linearlayout = (LinearLayout) rootView.findViewById(R.id.quote_amount_source_linearlayout);
        LinearLayout quote_amount_target_linearlayout = (LinearLayout) rootView.findViewById(R.id.quote_amount_target_linearlayout);

        if (quote_amount_source_linearlayout != null && quote_amount_target_linearlayout != null) {

            //Símbolo de moneda del país recibidor
            TextView quote_amount_target_symbol = (TextView) rootView.findViewById(R.id.quote_amount_target_symbol);
            if (quote_amount_target_symbol != null) {
                quote_amount_target_symbol.setText(app.currentTargetCountry.getCurrencySymbol());
            }

            //Bandera del país recibidor
            ImageView quote_amount_target_flag_imageview = (ImageView) rootView.findViewById(R.id.quote_amount_target_flag_imageview);
            if (quote_amount_target_flag_imageview != null) {
                String uri = "@drawable/" + app.currentTargetCountry.getFlag();
                int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
                Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);
                quote_amount_target_flag_imageview.setImageDrawable(res);
            }

            //Símbolo de moneda del país de origen
            TextView quote_amount_source_symbol = (TextView) rootView.findViewById(R.id.quote_amount_source_symbol);
            if (quote_amount_source_symbol != null) {
                quote_amount_source_symbol.setText(app.currentSourceCountry.getCurrencySymbol());
            }

            //Bandera del país de origen
            ImageView quote_amount_source_flag_imageview = (ImageView) rootView.findViewById(R.id.quote_amount_source_flag_imageview);
            if (quote_amount_source_flag_imageview != null) {
                String uri = "@drawable/" + app.currentSourceCountry.getFlag();
                int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
                Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);
                quote_amount_source_flag_imageview.setImageDrawable(res);
            }

        }

        //Referencias
        quote_amount_local_reference_textview = (TextView) rootView.findViewById(R.id.quote_amount_local_reference_textview);
        quote_amount_usd_reference_textview = (TextView) rootView.findViewById(R.id.quote_amount_usd_reference_textview);
        quote_agent_discount_reference_textview = (TextView) rootView.findViewById(R.id.quote_agent_discount_reference_textview);

        //Input para ingresar monto en moneda de origen
        quote_amount_source_edittext = (EditText) rootView.findViewById(R.id.quote_amount_source_edittext);
        quote_amount_target_edittext = (EditText) rootView.findViewById(R.id.quote_amount_target_edittext);

        quote_amount_source_edittext.addTextChangedListener(this);
        quote_amount_source_edittext.setOnFocusChangeListener(this);
        quote_amount_source_edittext.setOnKeyListener(this);
        quote_amount_source_edittext.requestFocus();

        quote_amount_target_edittext.addTextChangedListener(this);
        quote_amount_target_edittext.setOnFocusChangeListener(this);
        quote_amount_target_edittext.setOnKeyListener(this);


        return rootView;
    }

    private void UpdateAmount() {
        if (quote_amount_source_edittext != null) {

            double amount = 0;
            double recipientsAmount = 0;
            try {
                amount = Double.parseDouble(quote_amount_source_edittext.getText().toString());
                recipientsAmount = Double.parseDouble(quote_amount_target_edittext.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //TODO: Hay un maximo?
            if (amount < minAmountInLocalCurrency) {
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, String.format(localeSpanish, getString(R.string.quote_amount_min_validation), app.currentSourceCountry.getCurrencySymbol(), minAmountInLocalCurrency), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } else {

                hideSoftKeyboard(quote_amount_source_edittext);

                if (app.currentTransaction.getAmount() != amount) {

                    app.currentTransaction.setAmount(amount);
                    app.currentTransaction.setRecipientsAmount(recipientsAmount);

                    //Guardo un auditTrail para registrar esta acción
                    AuditTrail auditTrail = new AuditTrail(getString(R.string.quote_amount_title), app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, Double.toString(amount));
                    SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
                    saveAuditTrailTask.execute(auditTrail);

                    GoToNextFrame();
                }
            }
        }
    }

    private void GoToNextFrame() {
        //Navego hacia el fragment del tercer paso
        getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuoteSummaryFragment()).addToBackStack(getString(R.string.quote_amount_title)).commit();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            double amount = 0;
            double recipientsAmount = 0;
            switch (editTextBeingChangedId) {
                case R.id.quote_amount_source_edittext:
                    try {
                        amount = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    recipientsAmount = app.currentTransaction.calculateRecipientsAmount(amount, 0, false);

                    quote_amount_target_edittext.removeTextChangedListener(this);
                    quote_amount_target_edittext.setText(String.valueOf(String.format(localeSpanish, "%.0f ", recipientsAmount)));
                    quote_amount_target_edittext.addTextChangedListener(this);

                    break;
                case R.id.quote_amount_target_edittext:
                    try {
                        recipientsAmount = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    amount = app.currentTransaction.calculateAmount(recipientsAmount, 0);

                    quote_amount_source_edittext.removeTextChangedListener(this);
                    quote_amount_source_edittext.setText(String.format(localeSpanish, "%.0f ", amount));
                    quote_amount_source_edittext.addTextChangedListener(this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    int editTextBeingChangedId;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        editTextBeingChangedId = v.getId();
        switch (editTextBeingChangedId) {
            case R.id.quote_amount_source_edittext:
                if (hasFocus) {
                    setFocus(quote_amount_source_edittext);
                    showSoftKeyboard(quote_amount_source_edittext);
                }
                break;

            case R.id.quote_amount_target_edittext:
                if (hasFocus) {
                    setFocus(quote_amount_target_edittext);
                    showSoftKeyboard(quote_amount_target_edittext);
                }
                break;

            default:
                break;
        }
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editText, 0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {

            final int id = v.getId();
            switch (id) {
                case R.id.quote_amount_source_edittext:
                    hideSoftKeyboard(quote_amount_source_edittext);
                    break;

                case R.id.quote_amount_target_edittext:
                    hideSoftKeyboard(quote_amount_target_edittext);
                    break;
                default:
                    break;
            }

            UpdateAmount();
            return true;
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public class GetCorridorTask extends AsyncTask<Void, Void, Corridor> {
        private Activity activity;

        public GetCorridorTask(Activity _activity) {
            activity = _activity;
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null) {
                progressDialog.setMessage("Cargando...");
                progressDialog.show();
            }
        }

        @Override
        protected Corridor doInBackground(Void... params) {

            //Solo voy al server si hay conexion a Internet
            Corridor corridor = null;

            if (Utils.isNetworkAvailable(activity)) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String jsonStr = null;
                String format = "json";

                try {
                    final String BASE_URL = BuildConfig.DEBUG ? app.base_dev_url : app.base_qa_url;

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath("api")
                            .appendPath("corridor")
                            .appendPath("corridor")
                            .appendPath(app.currentSourceCountry.getCode())
                            .appendPath(app.currentTargetCountry.getCode())
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    // Create the request, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(app.connectionTimeOut);
                    urlConnection.setReadTimeout(app.readTimeout);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line).append("\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    jsonStr = buffer.toString();

                    Log.v(LOG_TAG, "JSON String: " + jsonStr);

                    Gson gson = new Gson();
                    corridor = gson.fromJson(jsonStr, Corridor.class);

                } catch (MalformedURLException | ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);

                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            }
            return corridor;
        }

        @Override
        protected void onPostExecute(Corridor corridor) {
            //saco el loading
            progressDialog.cancel();

            if (corridor != null && corridor.exchangeRate != 0) {

                app.currentTransaction.setExchangeRate(corridor.getExchangeRate());
                app.currentTransaction.setExchangeRateToUSD(corridor.getExchangeRateUSD());
                app.currentTransaction.setAgentDiscount(corridor.getAgentDiscount());
                app.currentTransaction.setSourceTaxRate(corridor.getSourceCountryTaxRate());
                app.currentTransaction.setTargetTaxRate(corridor.getTargetCountryTaxRate());
                app.currentTransaction.setSourceTransactionFee(corridor.getSourceTransactionFee());
                app.currentTransaction.setTargetTransactionFee(corridor.getTargetTransactionFee());
                app.currentTransaction.setExchangeRateSpread(corridor.getExchangeRateSpread());

                if (corridor.getExchangeRateUSD() != 0)
                    minAmountInLocalCurrency = app.currentTransaction.getMinAmountInUSD() / corridor.getExchangeRateUSD();

                //Actualizo referencias
                if (quote_amount_local_reference_textview != null) {
                    double reference1Value = app.currentTransaction.calculateRecipientsAmount(corridor.getReferenceAmount(), corridor.getExchangeRate(), false);
                    String reference1 = String.format(localeSpanish, getString(R.string.quote_amount_legend1), app.currentSourceCountry.getCurrencySymbol(), corridor.getReferenceAmount(), app.currentSourceCountry.getCurrencyName(), app.currentTargetCountry.getCurrencySymbol(), reference1Value, app.currentTargetCountry.getCurrencyName());
                    quote_amount_local_reference_textview.setText(reference1);
                }

                if (quote_amount_usd_reference_textview != null) {
                    double reference2Value = app.currentTransaction.calculateAmount(100, corridor.getExchangeRateUSD());
                    String reference2 = String.format(localeSpanish, getString(R.string.quote_amount_legend2), app.currentSourceCountry.getCurrencySymbol(), reference2Value, app.currentSourceCountry.getCurrencyName());
                    quote_amount_usd_reference_textview.setText(reference2);
                }

                if (quote_agent_discount_reference_textview != null) {
                    String reference3 = String.format(localeSpanish, getString(R.string.quote_amount_legend3), corridor.getAgentDiscount() * 100);
                    quote_agent_discount_reference_textview.setText(reference3);
                }

            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.remitee_message))
                        .setMessage(getString(R.string.quote_amount_corridor_error))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ((RemiteeActivity)getActivity()).hideSoftKeyboard();
                                getFragmentManager().popBackStack();
                            }
                        }).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.quote_amount_title));
    }
}