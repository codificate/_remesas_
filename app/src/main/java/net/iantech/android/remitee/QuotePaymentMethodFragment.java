package net.iantech.android.remitee;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

/**
 * Created by Lucas on 8/1/2016.
 */
public class QuotePaymentMethodFragment extends Fragment {
    private final String LOG_TAG = QuotePaymentMethodFragment.class.getSimpleName();

    private RemiteeApp app;

    public QuotePaymentMethodFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_quote_payment_method, container, false);
        if (rootView == null)
            return null;

        Button quote_payment_method_cash_button = (Button) rootView.findViewById(R.id.quote_payment_method_cash_button);
        if (quote_payment_method_cash_button != null) {
            quote_payment_method_cash_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Pago Cash", "Cash");

                    app.currentTransaction.setPaymentMethod(1);

                    getFragmentManager().beginTransaction().replace(R.id.activity_send, AgentCodeFragment.newInstance(true)).addToBackStack("Pagar").commit();
                }
            });
        }

        Button quote_payment_method_bank_button = (Button) rootView.findViewById(R.id.quote_payment_method_bank_button);
        if (quote_payment_method_bank_button != null) {
            quote_payment_method_bank_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Pago Banco", "Banco");

                    app.currentTransaction.setPaymentMethod(2);

                    Snackbar.make(getView(), "MEDIO DE PAGO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        Button quote_payment_method_creditcard_button = (Button) rootView.findViewById(R.id.quote_payment_method_creditcard_button);
        if (quote_payment_method_creditcard_button != null) {
            quote_payment_method_creditcard_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Pago Tarjeta", "Tarjeta");

                    app.currentTransaction.setPaymentMethod(3);

                    Snackbar.make(getView(), "MEDIO DE PAGO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        Button quote_payment_method_billers_button = (Button) rootView.findViewById(R.id.quote_payment_method_billers_button);
        if (quote_payment_method_billers_button != null) {
            quote_payment_method_billers_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Pago - Pago de Servicios", "Pago de Servicios");

                    app.currentTransaction.setPaymentMethod(5);

                    Snackbar.make(getView(), "MEDIO DE PAGO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        return rootView;
    }

    private void saveAuditTrail(String action, String status)
    {
        AuditTrail auditTrail = new AuditTrail(action, app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, status);
        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
        saveAuditTrailTask.execute(auditTrail);
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Pagar");
    }
}