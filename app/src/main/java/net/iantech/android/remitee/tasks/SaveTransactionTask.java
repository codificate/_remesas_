package net.iantech.android.remitee.tasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import net.iantech.android.remitee.BuildConfig;
import net.iantech.android.remitee.RemiteeApp;
import net.iantech.android.remitee.Utils;
import net.iantech.android.remitee.model.Trx;

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

/**
 * Created by Lucas on 8/4/2016.
 */
public class SaveTransactionTask extends AsyncTask<Trx, Void, Trx> {
    public RemiteeApp app;
    private final String LOG_TAG = SaveTransactionTask.class.getSimpleName();
    public Activity activity;

    public SaveTransactionTask() {
    }

    @Override
    protected Trx doInBackground(Trx... params) {

        //Solo voy al server si hay conexion a Internet
        if (Utils.isNetworkAvailable(activity)) {
            Gson gson = new Gson();
            String JsonDATA = gson.toJson(params[0]);
            String JsonResponse = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                final String BASE_URL = BuildConfig.DEBUG ? app.base_dev_url : app.base_qa_url;

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("api")
                        .appendPath("transaction")
                        .appendPath("save")
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
                Trx transaction = gson.fromJson(JsonResponse, Trx.class);

                //TODO: analizar si este if está bien. Por ahora lo hago asi para permitir continuar con el proceso mas alla de que se haya guardado o no bien la trx
                if (transaction != null)
                    app.currentTransaction = transaction;

                return transaction;

            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } // Exception thrown when network timeout occurs
            // Exception thrown when general network I/O error occurs
            catch (IOException e) {
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
}