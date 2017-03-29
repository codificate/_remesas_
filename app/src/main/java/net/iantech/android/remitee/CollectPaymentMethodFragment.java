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
public class CollectPaymentMethodFragment extends Fragment {
    private final String LOG_TAG = CollectPaymentMethodFragment.class.getSimpleName();

    private RemiteeApp app;

    public CollectPaymentMethodFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_collect_payment_method, container, false);
        if (rootView == null)
            return null;

        Button collect_payment_method_cash_button = (Button) rootView.findViewById(R.id.collect_payment_method_cash_button);
        if (collect_payment_method_cash_button != null) {
            collect_payment_method_cash_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail(getString(R.string.collect_payment_method_cash_audit), "Cash");

                    app.currentTransaction.setCollectionMethod(1);

                    getFragmentManager().beginTransaction().replace(R.id.activity_receive, AgentCodeFragment.newInstance(false)).addToBackStack(getString(R.string.collect_payment_method_title)).commit();
                }
            });
        }

        Button collect_payment_method_bank_button = (Button) rootView.findViewById(R.id.collect_payment_method_bank_button);
        if (collect_payment_method_bank_button != null) {
            collect_payment_method_bank_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Cobro Banco", "Banco");

                    app.currentTransaction.setCollectionMethod(2);

                    Snackbar.make(getView(), "MEDIO DE COBRO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        Button collect_payment_method_billers_button = (Button) rootView.findViewById(R.id.collect_payment_method_billers_button);
        if (collect_payment_method_billers_button != null) {
            collect_payment_method_billers_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail("Medio de Cobro - Pago de Servicios", "Pago de Servicios");

                    app.currentTransaction.setCollectionMethod(5);

                    Snackbar.make(getView(), "MEDIO DE COBRO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        /*
        Button collect_payment_method_wong_button = (Button) rootView.findViewById(R.id.collect_payment_method_wong_button);
        if (collect_payment_method_wong_button != null) {
            collect_payment_method_wong_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail(getString(R.string.collect_payment_method_wong_audit), "Wong");

                    app.currentTransaction.setCollectionMethod(2);

                    getFragmentManager().beginTransaction().replace(R.id.activity_receive, new ReceiveGiftCardFragment()).addToBackStack(getString(R.string.collect_payment_method_title)).commit();

                    //Snackbar.make(getView(), "MEDIO DE COBRO NO DISPONIBLE AÚN", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }

        Button collect_payment_method_inkafarma_button = (Button) rootView.findViewById(R.id.collect_payment_method_inkafarma_button);
        if (collect_payment_method_inkafarma_button != null) {
            collect_payment_method_inkafarma_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    saveAuditTrail(getString(R.string.collect_payment_method_inkafarma_audit), "Inkafarma");

                    app.currentTransaction.setCollectionMethod(3);

                    Snackbar.make(getView(), getString(R.string.collect_payment_method_not_available), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            });
        }
        */

        return rootView;
    }

    private void saveAuditTrail(String action, String status) {
        AuditTrail auditTrail = new AuditTrail(action, app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, status);
        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
        saveAuditTrailTask.execute(auditTrail);
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.collect_payment_method_title));
    }
}