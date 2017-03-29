package net.iantech.android.remitee.tasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import net.iantech.android.remitee.BuildConfig;
import net.iantech.android.remitee.RemiteeApp;
import net.iantech.android.remitee.Utils;
import net.iantech.android.remitee.model.Bank;
import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.PhoneNumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Lucas on 10/19/2016.
 */
public class GetBanksTask extends AsyncTask<Integer, Void, Bank[]> {
    public RemiteeApp app;
    private final String LOG_TAG = GetBanksTask.class.getSimpleName();
    public Activity activity;

    @Override
    protected Bank[] doInBackground(Integer... params) {

        //Solo voy al server si hay conexion a Internet
        if (Utils.isNetworkAvailable(activity)) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            Bank[] banks = null;

            try {
                final String BASE_URL = BuildConfig.DEBUG ? app.base_dev_url : app.base_qa_url;

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("api")
                        .appendPath("bank")
                        .appendPath("banks")
                        .appendPath(params[0].toString())
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
                responseJsonStr = buffer.toString();

                Log.v(LOG_TAG, "JSON String: " + responseJsonStr);

                Gson gson = new Gson();
                banks = gson.fromJson(responseJsonStr, Bank[].class);
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
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

            return banks;
        }
        return null;
    }
}
