package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.google.gson.Gson;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Trx;
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
public class QuoteSummaryFragment extends Fragment {
    private final String LOG_TAG = QuoteSummaryFragment.class.getSimpleName();

    private RemiteeApp app;
    private TextView quote_summary_to_amount;
    private Button quote_summary_confirmbutton;

    public QuoteSummaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

        //GetRecipientsAmountTask getRecipientsAmountTask = new GetRecipientsAmountTask(getActivity());
        //getRecipientsAmountTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quote_summary, container, false);
        if (rootView == null)
            return null;

        TextView quote_summary_from_textview = (TextView) rootView.findViewById(R.id.quote_summary_from_textview);
        if (quote_summary_from_textview != null) {
            quote_summary_from_textview.setText(String.format("Desde %s", app.currentSourceCountry.getName()));
        }

        ImageView quote_summary_from_imageview = (ImageView) rootView.findViewById(R.id.quote_summary_from_imageview);
        if (quote_summary_from_imageview != null) {
            String uri = "@drawable/" + app.currentSourceCountry.getFlag();

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            quote_summary_from_imageview.setImageDrawable(res);
        }


        TextView quote_summary_from_amount = (TextView) rootView.findViewById(R.id.quote_summary_from_amount);
        if (quote_summary_from_amount != null) {
            quote_summary_from_amount.setText(String.format(Locale.US, "%1$,.2f", app.currentTransaction.getAmount()));
        }

        TextView quote_summary_from_currency = (TextView) rootView.findViewById(R.id.quote_summary_from_currency);
        if (quote_summary_from_currency != null) {
            quote_summary_from_currency.setText(app.currentSourceCountry.getCurrencySymbol());
        }

        TextView quote_summary_to_textview = (TextView) rootView.findViewById(R.id.quote_summary_to_textview);
        if (quote_summary_to_textview != null) {
            quote_summary_to_textview.setText(String.format("En %s", app.currentTargetCountry.getName()));
        }

        ImageView quote_summary_to_imageview = (ImageView) rootView.findViewById(R.id.quote_summary_to_imageview);
        if (quote_summary_to_imageview != null) {
            String uri = "@drawable/" + app.currentTargetCountry.getFlag();

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            quote_summary_to_imageview.setImageDrawable(res);
        }

        quote_summary_to_amount = (TextView) rootView.findViewById(R.id.quote_summary_to_amount);
        if (quote_summary_to_amount != null) {
            quote_summary_to_amount.setText(String.format(Locale.US, "%1$,.2f", app.currentTransaction.getRecipientsAmount()));
        }

        TextView quote_summary_to_currency = (TextView) rootView.findViewById(R.id.quote_summary_to_currency);
        if (quote_summary_to_currency != null) {
            quote_summary_to_currency.setText(app.currentTargetCountry.getCurrencySymbol());
        }

        quote_summary_confirmbutton = (Button) rootView.findViewById(R.id.quote_summary_confirmbutton);
        if (quote_summary_confirmbutton != null) {

            if (app.currentTransaction.getRecipientsAmount() != 0) {
                quote_summary_confirmbutton.setEnabled(true);
            }

            quote_summary_confirmbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!app.currentTransaction.getConfirmed()) {


                        //Guardo un auditTrail para registrar esta acción
                        AuditTrail auditTrail = new AuditTrail(getString(R.string.quote_summary_confirm), app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, "Confirma");
                        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
                        saveAuditTrailTask.execute(auditTrail);

                        //Por ahora solo dejamos continuar si el país de destino es Argentina, Bolivia o Colombia
                        if (app.currentTargetCountry.getCode().equals("AR") || app.currentTargetCountry.getCode().equals("BO") || app.currentTargetCountry.getCode().equals("CO")) {
                            app.currentTransaction.setConfirmed(true);
                            GoToNextFrame();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.quote_summary_country_not_available_title))
                                    .setMessage(app.currentTargetCountry.getName() + " " + getString(R.string.quote_summary_country_not_available))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuoteSelectTargetCountryFragment()).addToBackStack("Resumen").commit();
                                        }
                                    }).show();
                        }
                    } else {
                        GoToNextFrame();
                    }
                }
            });
        }

        return rootView;
    }

    private void GoToNextFrame() {

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null) {
            //Handle Returning User

            //Voy al primer paso de una cotizacion (seleccionar el pais receptor
            getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuotePaymentMethodFragment()).addToBackStack("Resumen").commit();

        } else {
            //Handle new or logged out user
            RemiteeActivity remiteeActivity = (RemiteeActivity) getActivity();
            if (remiteeActivity != null) {
                remiteeActivity.onLoginPhone(getView());
            }
        }
    }

    @Override
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != RemiteeApp.FACEBOOK_APP_REQUEST_CODE) {
            return;
        }

        final String toastMessage;
        final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
        Boolean showError = false;
        if (loginResult == null || loginResult.wasCancelled()) {
            toastMessage = "Login Cancelled";
        } else if (loginResult.getError() != null) {

            toastMessage = loginResult.getError().getErrorType().getMessage();

            showError = true;

        } else {
            final AccessToken accessToken = loginResult.getAccessToken();
            final long tokenRefreshIntervalInSeconds =
                    loginResult.getTokenRefreshIntervalInSeconds();
            if (accessToken != null) {

                app.accountKitId = accessToken.getAccountId();

                //Guardo en los SharedPreferences el accountId
                toastMessage = "Success:" + accessToken.getAccountId()
                        + tokenRefreshIntervalInSeconds;

                ((RemiteeActivity) getActivity()).getCurrentAccount();

                //Voy al primer paso de una cotizacion (seleccionar el pais receptor
                getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuotePaymentMethodFragment()).addToBackStack("Resumen").commit();

            } else {
                toastMessage = "Error en la respuesta. Intente nuevamente";

                showError = true;
            }
        }

        if (showError)
            Snackbar.make(getView(), toastMessage, Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Resumen");
    }
}