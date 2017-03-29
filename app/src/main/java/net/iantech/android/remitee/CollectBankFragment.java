package net.iantech.android.remitee;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Bank;
import net.iantech.android.remitee.model.PartyPaymentMethod;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 8/1/2016.
 */
public class CollectBankFragment extends Fragment {
    private final String LOG_TAG = CollectBankFragment.class.getSimpleName();
    private RemiteeApp app;
    private EditText collect_bank_account_number_edittext;
    private EditText collect_bank_account_tin_edittext;

    private AppCompatSpinner collect_bank_select_bank_spinner = null;

    private PartyPaymentMethod partyPaymentMethod;
    List<PartyPaymentMethod> partyPaymentMethods;

    private Dialog bankAccountInfoDialog;

    public CollectBankFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_collect_bank, container, false);
        if (rootView == null)
            return null;

        //Listview de Cuentas Guardadas
        ListView select_partyBankAccount_listView = (ListView) rootView.findViewById(R.id.select_partyBankAccount_listView);
        if (select_partyBankAccount_listView != null) {

            partyPaymentMethods = PartyPaymentMethod.find(PartyPaymentMethod.class, "party_Id = ? and payment_Method = 2", new String[]{String.valueOf(app.currentTransaction.getRecipientId())}, null, "local_Created_Date DESC", null);

            if (partyPaymentMethods.size() > 0) {
                ArrayList<PartyPaymentMethod> partyPaymentMethodsArray = new ArrayList<>();
                partyPaymentMethodsArray.addAll(partyPaymentMethods);
                select_partyBankAccount_listView.setAdapter(new BankAccountsAdapter(getActivity(), partyPaymentMethodsArray));

                select_partyBankAccount_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        partyPaymentMethod = (PartyPaymentMethod) parent.getItemAtPosition(position);

                        updateTransaction();

                        GoToNextFrame();
                    }
                });
            }
        }

        //Boton para crear nueva cuenta
        Button collect_bank_add_button = (Button) rootView.findViewById(R.id.collect_bank_add_button);
        if (collect_bank_add_button != null) {
            collect_bank_add_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    openBankAccountInfoDialog();
                }
            });
        }

        //Boton para continuar sin seleccionar cuenta bancaria
        Button collect_bank_continue_button = (Button) rootView.findViewById(R.id.collect_bank_continue_button);
        if (collect_bank_continue_button != null) {
            collect_bank_continue_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    GoToNextFrame();
                }
            });
        }

        return rootView;
    }

    private void updateTransaction() {
        app.currentTransaction.setRecipientBankId(partyPaymentMethod.getBankId());
        app.currentTransaction.setRecipientBankAccountNumber(partyPaymentMethod.getBankAccountNumber());
        app.currentTransaction.setRecipientBankTin(partyPaymentMethod.getBankTIN());
        app.currentTransaction.setCollectionMethod(partyPaymentMethod.getPaymentMethod());
        app.currentTransaction.setRecipientBankAccountType(partyPaymentMethod.getBankAccountType());
    }

    private boolean isValid() {
        if (partyPaymentMethod.getBankId() == 0) {

            final View view = bankAccountInfoDialog.findViewById(R.id.bank_account_info_layout);
            if (view != null) {
                Snackbar.make(view, getString(R.string.err_msg_select_bank), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            requestFocus(collect_bank_select_bank_spinner);
            return false;
        }

        if (collect_bank_account_number_edittext.getText().toString().isEmpty()) {
            if (app.currentTargetCountry.getCode().equals("AR")) {
                collect_bank_account_number_edittext.setError(getString(R.string.err_msg_invalid_cbu));
            } else
                collect_bank_account_number_edittext.setError(getString(R.string.err_msg_invalid_account_number));
            requestFocus(collect_bank_account_number_edittext);
            return false;
        } else {
            if (app.currentTargetCountry.getCode().equals("AR")) {
                String cbuPattern = "[0-9]{22}";
                String accountNumber = collect_bank_account_number_edittext.getText().toString().replaceAll("\\W", "");
                if (!accountNumber.matches(cbuPattern)) {
                    collect_bank_account_number_edittext.setError(getString(R.string.err_msg_invalid_cbu));
                    requestFocus(collect_bank_account_number_edittext);
                    return false;
                }
            }
        }

        if (collect_bank_account_tin_edittext.getText().toString().isEmpty()) {
            collect_bank_account_tin_edittext.setError(getString(R.string.err_msg_invalid_tin_number));
            return false;
        } else {
            if (app.currentTargetCountry.getCode().equals("AR")) {
                if (!Utils.isValidCUIT(collect_bank_account_tin_edittext.getText().toString())) {
                    collect_bank_account_tin_edittext.setError(getString(R.string.err_msg_invalid_cuit));
                    requestFocus(collect_bank_account_tin_edittext);
                    return false;
                }
            }
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void save() {

        if (partyPaymentMethod.getPartyId() == 0) {
            partyPaymentMethod.setPartyId(app.currentTransaction.getRecipientId());
            partyPaymentMethod.setBankAccountNumber(collect_bank_account_number_edittext.getText().toString());
            partyPaymentMethod.setBankTIN(collect_bank_account_tin_edittext.getText().toString());
            partyPaymentMethod.save();
        }
    }

    private void openBankAccountInfoDialog() {
        //Cargo los Bancos
        GetBanksTask getBanksTask = new GetBanksTask(getActivity());
        getBanksTask.execute(app.currentTargetCountry.getId());

        partyPaymentMethod = new PartyPaymentMethod();

        bankAccountInfoDialog = new Dialog(getActivity());

        bankAccountInfoDialog.setTitle(getString(R.string.collect_bank_new_title));
        bankAccountInfoDialog.setContentView(R.layout.dialog_bank_account_info);

        TextView collect_bank_account_number_label = (TextView) bankAccountInfoDialog.findViewById(R.id.collect_bank_account_number_label);
        if (collect_bank_account_number_label != null) {
            if (app.currentTargetCountry.getCode().equals("AR")) {
                collect_bank_account_number_label.setText(getString(R.string.collect_bank_label_cbu));
            } else {
                collect_bank_account_number_label.setText(getString(R.string.collect_bank_label_account));
            }
        }

        TextView collect_bank_document_label = (TextView) bankAccountInfoDialog.findViewById(R.id.collect_bank_document_label);
        if (collect_bank_document_label != null) {
            if (app.currentTargetCountry.getCode().equals("AR")) {
                collect_bank_document_label.setText(getString(R.string.collect_bank_label_cuit));
            } else {
                collect_bank_document_label.setText(getString(R.string.collect_bank_label_document));
            }
        }

        collect_bank_account_number_edittext = (EditText) bankAccountInfoDialog.findViewById(R.id.collect_bank_account_number_edittext);
        collect_bank_select_bank_spinner = (AppCompatSpinner) bankAccountInfoDialog.findViewById(R.id.collect_bank_select_bank_spinner);
        collect_bank_account_tin_edittext = (EditText) bankAccountInfoDialog.findViewById(R.id.collect_bank_account_tin_edittext);
        Switch collect_bank_account_type_switch = (Switch) bankAccountInfoDialog.findViewById(R.id.collect_bank_account_type_switch);
        Button saveBankAccountButton = (Button) bankAccountInfoDialog.findViewById(R.id.saveBankAccountButton);
        Button cancelBankAccountButton = (Button) bankAccountInfoDialog.findViewById(R.id.cancelBankAccountButton);

        if (collect_bank_select_bank_spinner != null) {
            collect_bank_select_bank_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Bank bank = (Bank) parent.getItemAtPosition(position);
                    if (bank != null) {
                        partyPaymentMethod.setBankId(bank.getId());
                        partyPaymentMethod.setBankName(bank.getShortName());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    partyPaymentMethod.setBankId(0);
                    partyPaymentMethod.setBankName("");
                }
            });
        }

        if (collect_bank_account_tin_edittext != null) {
            collect_bank_account_tin_edittext.setText(app.currentTransaction.getRecipientDocumentNumber());
        }

        if (collect_bank_account_type_switch != null) {
            collect_bank_account_type_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        partyPaymentMethod.setBankAccountType(2);
                    } else {
                        partyPaymentMethod.setBankAccountType(1);
                    }
                }
            });

        }

        if (saveBankAccountButton != null) {

            saveBankAccountButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValid()) {
                        save();

                        updateTransaction();

                        GoToNextFrame();

                        bankAccountInfoDialog.dismiss();
                    }
                }
            });
        }

        if (cancelBankAccountButton != null) {

            cancelBankAccountButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    bankAccountInfoDialog.dismiss();
                }
            });
        }

        bankAccountInfoDialog.show();
    }

    private void GoToNextFrame() {

        //Guardo un auditTrail para registrar esta acción
        AuditTrail auditTrail = new AuditTrail(getString(R.string.collect_bank_title), app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, "Datos Bancarios Ingresados");
        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
        saveAuditTrailTask.execute(auditTrail);

        //Voy al proximo paso
        getFragmentManager().beginTransaction().replace(R.id.activity_send, new SendConfirmFragment()).addToBackStack(getString(R.string.collect_bank_title)).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.collect_bank_title));
    }

    public class GetBanksTask extends net.iantech.android.remitee.tasks.GetBanksTask {
        public GetBanksTask(Activity _activity) {
            activity = _activity;
            app = (RemiteeApp) _activity.getApplicationContext();
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            //Busco el loading y por ahora lo saco
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Bank[] banks) {

            progressDialog.cancel();

            ArrayList<Bank> banksArray = new ArrayList<>();
            banksArray.add(new Bank(0, "0", getString(R.string.collect_bank_select_bank)));

            for (int i = 0; i < banks.length; i++) {
                banksArray.add(banks[i]);
            }

            ArrayAdapter<Bank> adapter = new ArrayAdapter<>(activity
                    , android.R.layout.simple_spinner_item
                    , banksArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if (collect_bank_select_bank_spinner != null)
                collect_bank_select_bank_spinner.setAdapter(adapter);
        }
    }
}