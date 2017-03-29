package net.iantech.android.remitee;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.ExchangeRate;
import net.iantech.android.remitee.model.Trx;
import net.iantech.android.remitee.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class TransactionCodeFragment extends Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private final String LOG_TAG = TransactionCodeFragment.class.getSimpleName();
    private RemiteeApp app;
    private ProgressDialog progressDialog;

    private EditText mTrxCodeFirstDigitEditText;
    private EditText mTrxCodeSecondDigitEditText;
    private EditText mTrxCodeThirdDigitEditText;
    private EditText mTrxCodeForthDigitEditText;
    private EditText mTrxCodeFifthDigitEditText;
    private EditText mTrxCodeSixthDigitEditText;
    private EditText mTrxCodeHiddenEditText;

    public TransactionCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((RemiteeActivity) getActivity()).app;

    }

    @Override
    public void onStop()
    {
        hideSoftKeyboard(mTrxCodeFirstDigitEditText);

        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transaction_code, container, false);
        if (rootView == null)
            return null;

        mTrxCodeFirstDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_first_edittext);
        mTrxCodeSecondDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_second_edittext);
        mTrxCodeThirdDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_third_edittext);
        mTrxCodeForthDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_forth_edittext);
        mTrxCodeFifthDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_fifth_edittext);
        mTrxCodeSixthDigitEditText = (EditText) rootView.findViewById(R.id.trxcode_sixth_edittext);
        mTrxCodeHiddenEditText = (EditText) rootView.findViewById(R.id.trxcode_hidden_edittext);

        Button validateAgentsCodeButton = (Button) rootView.findViewById(R.id.validateTransactionCodeButton);
        if (validateAgentsCodeButton != null) {
            validateAgentsCodeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTrxCodeHiddenEditText.length() == 6)
                        validateCode();
                }
            });
        }

        setTrxCodeListeners();

        return rootView;
    }

    private void validateCode() {
        //TODO: validar el codigo de la transacción con el server
        GetTransactionByTrackingNumberTask getTransactionByTrackingNumberTask = new GetTransactionByTrackingNumberTask(getActivity());
        getTransactionByTrackingNumberTask.execute(mTrxCodeHiddenEditText.getText().toString());
    }

    public class GetTransactionByTrackingNumberTask extends AsyncTask<String, Void, Trx> {
        private Activity activity;

        public GetTransactionByTrackingNumberTask(Activity _activity) {
            activity = _activity;
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Validando...");
            progressDialog.show();
        }

        @Override
        protected Trx doInBackground(String... params) {
            //Solo voy al server si hay conexion a Internet
            Trx trx = null;

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
                            .appendPath("transaction")
                            .appendPath("getTransactionByTrackingNumber")
                            .appendQueryParameter("trackingNumber", params[0])
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
                    trx = gson.fromJson(jsonStr, Trx.class);

                } catch (MalformedURLException | ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemTrxCodeg
                    // to parse it.
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
            return trx;
        }

        @Override
        protected void onPostExecute(Trx trx) {
            //saco el loading
            progressDialog.cancel();

            //Blanqueo los edittexts
            mTrxCodeFirstDigitEditText.setText("");
            mTrxCodeSecondDigitEditText.setText("");
            mTrxCodeThirdDigitEditText.setText("");
            mTrxCodeForthDigitEditText.setText("");
            mTrxCodeFifthDigitEditText.setText("");
            mTrxCodeSixthDigitEditText.setText("");
            mTrxCodeHiddenEditText.setText("");

            if (trx != null) {

                app.currentTransaction = trx;

                GoToNextFrame();

            } else {
                setFocus(mTrxCodeFirstDigitEditText);
                Snackbar.make(getView(), "El código es incorrecto.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    private void GoToNextFrame() {
        //Navego hacia el fragment del tercer paso
        getFragmentManager().beginTransaction().replace(R.id.activity_receive, new CollectPaymentMethodFragment()).addToBackStack("Ingresar Código de Trx").commit();
    }

    @Override
    public void afterTextChanged(Editable s) {
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.trxcode_first_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;

            case R.id.trxcode_second_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;

            case R.id.trxcode_third_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;

            case R.id.trxcode_forth_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;

            case R.id.trxcode_fifth_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;
            case R.id.trxcode_sixth_edittext:
                if (hasFocus) {
                    setFocus(mTrxCodeHiddenEditText);
                    showSoftKeyboard(mTrxCodeHiddenEditText);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.trxcode_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mTrxCodeHiddenEditText.getText().length() == 6)
                            mTrxCodeSixthDigitEditText.setText("");
                        if (mTrxCodeHiddenEditText.getText().length() == 5)
                            mTrxCodeFifthDigitEditText.setText("");
                        else if (mTrxCodeHiddenEditText.getText().length() == 4)
                            mTrxCodeForthDigitEditText.setText("");
                        else if (mTrxCodeHiddenEditText.getText().length() == 3)
                            mTrxCodeThirdDigitEditText.setText("");
                        else if (mTrxCodeHiddenEditText.getText().length() == 2)
                            mTrxCodeSecondDigitEditText.setText("");
                        else if (mTrxCodeHiddenEditText.getText().length() == 1)
                            mTrxCodeFirstDigitEditText.setText("");

                        if (mTrxCodeHiddenEditText.length() > 0)
                            mTrxCodeHiddenEditText.setText(mTrxCodeHiddenEditText.getText().subSequence(0, mTrxCodeHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mTrxCodeFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mTrxCodeFirstDigitEditText.setText(s.charAt(0) + "");
            mTrxCodeSecondDigitEditText.setText("");
            mTrxCodeThirdDigitEditText.setText("");
            mTrxCodeForthDigitEditText.setText("");
            mTrxCodeFifthDigitEditText.setText("");
            mTrxCodeSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            mTrxCodeSecondDigitEditText.setText(s.charAt(1) + "");
            mTrxCodeThirdDigitEditText.setText("");
            mTrxCodeForthDigitEditText.setText("");
            mTrxCodeFifthDigitEditText.setText("");
            mTrxCodeSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
            mTrxCodeThirdDigitEditText.setText(s.charAt(2) + "");
            mTrxCodeForthDigitEditText.setText("");
            mTrxCodeFifthDigitEditText.setText("");
            mTrxCodeSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mTrxCodeForthDigitEditText.setText(s.charAt(3) + "");
            mTrxCodeFifthDigitEditText.setText("");
            mTrxCodeSixthDigitEditText.setText("");
        } else if (s.length() == 5) {
            mTrxCodeFifthDigitEditText.setText(s.charAt(4) + "");
            mTrxCodeSixthDigitEditText.setText("");
        } else if (s.length() == 6) {
            mTrxCodeSixthDigitEditText.setText(s.charAt(5) + "");

            hideSoftKeyboard(mTrxCodeSixthDigitEditText);

            validateCode();
        }
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

    /**
     * Sets listeners for EditText fields.
     */
    private void setTrxCodeListeners() {
        mTrxCodeHiddenEditText.addTextChangedListener(this);

        mTrxCodeFirstDigitEditText.setOnFocusChangeListener(this);
        mTrxCodeSecondDigitEditText.setOnFocusChangeListener(this);
        mTrxCodeThirdDigitEditText.setOnFocusChangeListener(this);
        mTrxCodeForthDigitEditText.setOnFocusChangeListener(this);
        mTrxCodeFifthDigitEditText.setOnFocusChangeListener(this);
        mTrxCodeSixthDigitEditText.setOnFocusChangeListener(this);

        mTrxCodeFirstDigitEditText.setOnKeyListener(this);
        mTrxCodeSecondDigitEditText.setOnKeyListener(this);
        mTrxCodeThirdDigitEditText.setOnKeyListener(this);
        mTrxCodeForthDigitEditText.setOnKeyListener(this);
        mTrxCodeFifthDigitEditText.setOnKeyListener(this);
        mTrxCodeSixthDigitEditText.setOnKeyListener(this);
        mTrxCodeHiddenEditText.setOnKeyListener(this);

        mTrxCodeFirstDigitEditText.requestFocus();
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editText, 0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Ingresar Código de Envio");
    }
}
