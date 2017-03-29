package net.iantech.android.remitee;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iantech.android.remitee.model.Trx;

import java.util.Locale;

public class SendConfirmFragment extends Fragment {

    private RemiteeApp app;

    public SendConfirmFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((RemiteeActivity)getActivity()).app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_confirm, container, false);
        if (rootView == null)
            return null;

        TextView quote_confirm_from_textview = (TextView) rootView.findViewById(R.id.quote_confirm_from_textview);
        if (quote_confirm_from_textview != null) {
            quote_confirm_from_textview.setText(app.currentTransaction.getSenderName());
        }

        ImageView quote_confirm_from_imageview = (ImageView) rootView.findViewById(R.id.quote_confirm_from_imageview);
        if (quote_confirm_from_imageview != null) {
            String uri = "@drawable/" + app.currentSourceCountry.getFlag();

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            quote_confirm_from_imageview.setImageDrawable(res);
        }


        TextView quote_confirm_from_amount = (TextView) rootView.findViewById(R.id.quote_confirm_from_amount);
        if (quote_confirm_from_amount != null) {
            quote_confirm_from_amount.setText(String.format(Locale.US, "%1$,.2f", app.currentTransaction.getAmount()));
        }

        TextView quote_confirm_from_currency = (TextView) rootView.findViewById(R.id.quote_confirm_from_currency);
        if (quote_confirm_from_currency != null) {
            quote_confirm_from_currency.setText(app.currentSourceCountry.getCurrencySymbol());
        }

        TextView quote_confirm_to_textview = (TextView) rootView.findViewById(R.id.quote_confirm_to_textview);
        if (quote_confirm_to_textview != null) {
            quote_confirm_to_textview.setText(app.currentTransaction.getRecipientName());
        }

        ImageView quote_confirm_to_imageview = (ImageView) rootView.findViewById(R.id.quote_confirm_to_imageview);
        if (quote_confirm_to_imageview != null) {
            String uri = "@drawable/" + app.currentTargetCountry.getFlag();

            int imageResource = rootView.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
            Drawable res = ResourcesCompat.getDrawable(rootView.getResources(), imageResource, null);

            quote_confirm_to_imageview.setImageDrawable(res);
        }

        TextView quote_confirm_to_amount = (TextView) rootView.findViewById(R.id.quote_confirm_to_amount);
        if (quote_confirm_to_amount != null) {
            quote_confirm_to_amount.setText(String.format(Locale.US, "%1$,.2f", app.currentTransaction.getRecipientsAmount()));
        }

        TextView quote_confirm_to_currency = (TextView) rootView.findViewById(R.id.quote_confirm_to_currency);
        if (quote_confirm_to_currency != null) {
            quote_confirm_to_currency.setText(app.currentTargetCountry.getCurrencySymbol());
        }

        Button send_confirm_confirmbutton = (Button) rootView.findViewById(R.id.send_confirm_confirmbutton);
        if (send_confirm_confirmbutton != null) {
            send_confirm_confirmbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    app.currentTransaction.setConfirmed(true);

                    if (app.currentTransaction.getTrackingNumber() == null || app.currentTransaction.getTrackingNumber().isEmpty())
                    {
                        //Guardo la transacción
                        SaveTransactionTask saveTransactionTask = new SaveTransactionTask(getActivity());
                        saveTransactionTask.execute(app.currentTransaction);

                    } else {
                        GoToNextFrame();
                    }
                }
            });
        }

        return rootView;
    }

    public class SaveTransactionTask extends net.iantech.android.remitee.tasks.SaveTransactionTask {
        public SaveTransactionTask(Activity _activity) {
            activity = _activity;
            app = (RemiteeApp) _activity.getApplicationContext();
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            //Busco el loading y por ahora lo saco
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Confirmando Envio...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Trx transaction) {

            progressDialog.cancel();

            if (transaction != null && !transaction.getTrackingNumber().isEmpty())
            {
                //Guardo la transaccion localmente
                //app.currentTransaction.setId(transaction.save());

                app.currentTransaction.setTrackingNumber(transaction.getTrackingNumber());

                GoToNextFrame();
            }
            else
            {
                Snackbar.make(getView(), "Ocurrió un error al procesar el envío.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    private void GoToNextFrame()
    {
        //Voy al thank you y le paso el tracking number
        getFragmentManager().beginTransaction().replace(R.id.activity_send, ThanksMessageFragment.newInstance(app.currentTransaction.getTrackingNumber())).addToBackStack(getString(R.string.send_confirm)).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Confirma el envío");
    }
}
