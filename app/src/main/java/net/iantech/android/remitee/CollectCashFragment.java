package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.google.gson.Gson;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.User;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

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
import java.util.Locale;

/**
 * Created by Lucas on 8/1/2016.
 */
public class CollectCashFragment extends Fragment {
    private final String LOG_TAG = CollectCashFragment.class.getSimpleName();
    private RemiteeApp app;

    public CollectCashFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collect_cash, container, false);
        if (rootView == null)
            return null;

        Resources resources = getResources();
        String[] country_currencySymbols = resources.getStringArray(R.array.country_currencySymbols);
        String[] country_flags = resources.getStringArray(R.array.country_flags);
        String[] country_currencyNames = resources.getStringArray(R.array.country_currencyNames);
        int[] country_ids = resources.getIntArray(R.array.country_ids);

        //TODO: las propiedades de los países las debería traer del server cuando se inicia la aplicación
        int sourceCountryIndex = 0;
        int targetCountryIndex = 0;
        for (int i = 0; i < country_ids.length; i++) {
            if (country_ids[i] == app.currentTransaction.getSourceCountryId())
                sourceCountryIndex = i;

            if (country_ids[i] == app.currentTransaction.getTargetCountryId())
                targetCountryIndex = i;
        }

        //Datos Enviador
        TextView collect_summary_from_textview = (TextView) rootView.findViewById(R.id.collect_summary_from_textview);
        if (collect_summary_from_textview != null) {
            collect_summary_from_textview.setText(String.format("%s %s", app.currentTransaction.getSenderFirstName(), app.currentTransaction.getSenderLastName()));
        }

        ImageView collect_summary_from_imageview = (ImageView) rootView.findViewById(R.id.collect_summary_from_imageview);
        if (collect_summary_from_imageview != null) {
            String uri = "@drawable/" + country_flags[sourceCountryIndex];

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            collect_summary_from_imageview.setImageDrawable(res);
        }

        TextView collect_summary_from_amount = (TextView) rootView.findViewById(R.id.collect_summary_from_amount);
        if (collect_summary_from_amount != null) {

            String amount = country_currencySymbols[sourceCountryIndex] + String.format(Locale.US, " %1$,.0f ", app.currentTransaction.getAmount()) + String.format("(%s)", country_currencyNames[sourceCountryIndex]);
            collect_summary_from_amount.setText(amount);
        }

        TextView collect_summary_from_phoneNumber = (TextView) rootView.findViewById(R.id.collect_summary_from_phoneNumber);
        if (collect_summary_from_phoneNumber != null) {
            collect_summary_from_phoneNumber.setText(app.currentTransaction.getSenderPhoneNumber());
        }

        //Datos Recibidor
        TextView collect_summary_to_textview = (TextView) rootView.findViewById(R.id.collect_summary_to_textview);
        if (collect_summary_to_textview != null) {
            collect_summary_to_textview.setText(String.format("%s %s", app.currentTransaction.getRecipientFirstName(), app.currentTransaction.getRecipientLastName()));
        }

        ImageView collect_summary_to_imageview = (ImageView) rootView.findViewById(R.id.collect_summary_to_imageview);
        if (collect_summary_to_imageview != null) {
            String uri = "@drawable/" + country_flags[targetCountryIndex];

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            collect_summary_to_imageview.setImageDrawable(res);
        }

        TextView collect_summary_to_amount = (TextView) rootView.findViewById(R.id.collect_summary_to_amount);
        if (collect_summary_to_amount != null) {

            String amount = country_currencySymbols[targetCountryIndex] + String.format(Locale.US, " %1$,.0f ", app.currentTransaction.getRecipientsAmount()) + String.format("(%s)", country_currencyNames[targetCountryIndex]);
            collect_summary_to_amount.setText(amount);
        }


        TextView collect_summary_to_phoneNumber = (TextView) rootView.findViewById(R.id.collect_summary_to_phoneNumber);
        if (collect_summary_to_phoneNumber != null) {
            collect_summary_to_phoneNumber.setText(app.currentTransaction.getRecipientPhoneNumber());
        }

        TextView collect_summary_to_documentNumber = (TextView) rootView.findViewById(R.id.collect_summary_to_documentNumber);
        if (collect_summary_to_documentNumber != null) {
            collect_summary_to_documentNumber.setText(app.currentTransaction.getRecipientDocumentNumber());
        }

        Button collect_summary_confirmbutton = (Button) rootView.findViewById(R.id.collect_summary_confirmbutton);
        if (collect_summary_confirmbutton != null) {

            collect_summary_confirmbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    TransactionCollectedTask transactionCollectedTask = new TransactionCollectedTask(getActivity());
                    transactionCollectedTask.execute();

                }
            });
        }

        return rootView;
    }

    private void GoToNextFrame() {
        getFragmentManager().beginTransaction().replace(R.id.activity_receive, new ThanksMessageCashColletedFragment()).addToBackStack(getString(R.string.collect_cash_verify_title)).commit();
    }

    public class TransactionCollectedTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private ProgressDialog progressDialog;

        public TransactionCollectedTask(Activity _activity) {
            activity = _activity;
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Actualizando Transacción...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Solo voy al server si hay conexion a Internet
            if (Utils.isNetworkAvailable(activity)) {
                Gson gson = new Gson();
                String JsonDATA = gson.toJson(app.currentTransaction);
                String JsonResponse = null;
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                try {
                    final String BASE_URL = BuildConfig.DEBUG ? app.base_dev_url : app.base_qa_url;

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath("api")
                            .appendPath("transaction")
                            .appendPath("collected")
                            .build();

                    URL url = new URL(builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(app.connectionTimeOut);
                    urlConnection.setReadTimeout(app.readTimeout);
                    // is output buffer writter
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                    Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                    writer.write(JsonDATA);
// json data
                    writer.close();
                    InputStream inputStream = urlConnection.getInputStream();
//input stream
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        buffer.append(inputLine).append("\n");
                    if (buffer.length() == 0) {
                        // Stream was empty. No point in parsing.
                        return null;
                    }
                    JsonResponse = buffer.toString();
//response data
                    Log.i(LOG_TAG, JsonResponse);
                    //send to post execute

                } catch (MalformedURLException | ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
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
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            //saco el loading
            progressDialog.cancel();

            GoToNextFrame();

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.collect_cash_verify_title));
    }
}