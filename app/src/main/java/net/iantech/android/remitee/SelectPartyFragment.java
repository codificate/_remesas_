package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.orm.query.Condition;
import com.orm.query.Select;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.model.PhoneNumber;
import net.iantech.android.remitee.model.Trx;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;
import net.iantech.android.remitee.util.GsonSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 8/1/2016.
 */
public class SelectPartyFragment extends Fragment {
    private final String LOG_TAG = SelectPartyFragment.class.getSimpleName();
    private RemiteeApp app;
    private PartiesAdapter adapter;
    private Party party;

    private CountryCodePicker contact_countryCodePicker;
    private EditText contact_documentNumber_edittext;
    private EditText contact_firstName_edittext;
    private EditText contact_lastName_edittext;
    private EditText contact_phone_edittext;
    private EditText contact_phone_areacode_edittext;
    private int defaultPhoneCode;
    private PhoneNumber contactPhoneNumber;
    private int phoneNumberLength;

    private static final String ISSENDER_PARAM = "isSender";
    private Boolean isSender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

        if (getArguments() != null) {
            isSender = getArguments().getBoolean(ISSENDER_PARAM);
        }
    }

    public static SelectPartyFragment newInstance(Boolean _isSender) {
        SelectPartyFragment fragment = new SelectPartyFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISSENDER_PARAM, _isSender);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_party, container, false);
        if (rootView == null)
            return null;

        Bundle bundle = getArguments();
        isSender = bundle.getBoolean("isSender");

        adapter = new PartiesAdapter(
                getActivity(),
                new ArrayList<Party>());

        if (isSender) {
            defaultPhoneCode = app.currentSourceCountry.getPhoneCode();
            //TODO: agregar una variable que sea el tamaño de los numeros de teléfono
            phoneNumberLength = 8;
        } else {
            defaultPhoneCode = app.currentTargetCountry.getPhoneCode();
            //TODO: agregar una variable que sea el tamaño de los numeros de teléfono
            phoneNumberLength = 8;
        }

        TextView fragment_select_party_title = (TextView) rootView.findViewById(R.id.fragment_select_party_title);
        if (fragment_select_party_title != null) {
            if (isSender) {
                fragment_select_party_title.setText(R.string.recent_senders);
            } else {
                fragment_select_party_title.setText(R.string.recent_receivers);
            }
        }

        Button senderNewContactButton = (Button) rootView.findViewById(R.id.senderNewContactButton);
        if (senderNewContactButton != null) {
            senderNewContactButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    party = new Party();
                    isNewContact = true;
                    //openContactInfoDialog();
                    getFragmentManager().beginTransaction().replace(R.id.activity_send, new ContactInfoFragment().newInstance(isSender)).addToBackStack(getString(R.string.contact_info_fragment)).commit();
                }
            });
        }

        ListView select_recentparty_listView = (ListView) rootView.findViewById(R.id.select_recentparty_listView);
        if (select_recentparty_listView != null) {
            if (isSender)
                loadSenderParties();
            else
                loadReceiverParties();

            select_recentparty_listView.setAdapter(adapter);
            
            select_recentparty_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    party = (Party) parent.getItemAtPosition(position);

                    savedInstanceState.putString( "partySenderSelected", GsonSingleton.getInstance().getGson().toJson(party) );

                    UpdateTransaction();

                    GoToNextFrame();
                }
            });
        }

        return rootView;
    }

    private void UpdateTransaction()
    {
        if (party != null) {
            if (isSender) {
                app.currentTransaction.setSenderFirstName(party.getFirstName());
                app.currentTransaction.setSenderLastName(party.getLastName());
                app.currentTransaction.setSenderDocumentNumber(party.getDocumentNumber());
                app.currentTransaction.setSenderPhoneCountryCode(party.getPhoneCountryCode());
                app.currentTransaction.setSenderPhoneNumber(party.getPhoneNumber());

                app.currentTransaction.setSenderCarrier(party.getCarrier());
                app.currentTransaction.setSenderLocation(party.getLocation());
                app.currentTransaction.setSenderLineType(party.getLineType());

            } else {

                app.currentTransaction.setRecipientFirstName(party.getFirstName());
                app.currentTransaction.setRecipientLastName(party.getLastName());
                app.currentTransaction.setRecipientDocumentNumber(party.getDocumentNumber());
                app.currentTransaction.setRecipientPhoneCountryCode(party.getPhoneCountryCode());
                app.currentTransaction.setRecipientPhoneNumber(party.getPhoneNumber());

                app.currentTransaction.setRecipientCarrier(party.getCarrier());
                app.currentTransaction.setRecipientLocation(party.getLocation());
                app.currentTransaction.setRecipientLineType(party.getLineType());
            }
        }
    }

    private void GoToNextFrame() {

        String title;

        if (isSender)
        {
            title = getString(R.string.select_party_sender_entered);
        }
        else
        {
            title = getString(R.string.select_party_recipient_entered);
        }

        //Guardo un auditTrail para registrar esta acción
        AuditTrail auditTrail = new AuditTrail(title, app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, "Datos Ingresados");
        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
        saveAuditTrailTask.execute(auditTrail);

        //Voy al proximo paso
        if (!isSender) {
            app.currentTransaction.setRecipientId(party.getId());

            //Si es Colombia le mostramos el Fragment de ingreso de Datos Bancarios
            //TODO: a futuro ver si esto lo hacemos así o el recibidor lo ingresa
            if (app.currentTargetCountry.getCode().equals("AR") || app.currentTargetCountry.getCode().equals("BO") || app.currentTargetCountry.getCode().equals("CO"))
            {
                getFragmentManager().beginTransaction().replace(R.id.activity_send, new CollectBankFragment()).addToBackStack("Ingresar datos de Recibidor").commit();
            }
            else {
                getFragmentManager().beginTransaction().replace(R.id.activity_send, new SendConfirmFragment()).addToBackStack("Ingresar datos de Recibidor").commit();
            }

        } else {
            app.currentTransaction.setSenderId(party.getId());
            getFragmentManager().beginTransaction().replace(R.id.activity_send, SelectPartyFragment.newInstance(false)).addToBackStack("Ingresar datos de Enviador").commit();
        }
    }

    private void loadReceiverParties() {

        //Cargo el ArrayList con los contactos recientes del SQLLite
        List<Party> parties = null;

        try {
            parties = Party.find(Party.class, "country_Code = ?", new String[]{app.currentSourceCountry.getCode()}, null, "local_Created_Date DESC", null);
        }catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            parties = new ArrayList<>();
        }

        adapter.clear();
        adapter.addAll(parties); //Honeycomb or above. Antes no existia el addAll
    }

    private void loadSenderParties() {

        //Cargo el ArrayList con los contactos recientes del SQLLite
        List<Party> parties = null;

        try {
            parties = Party.find(Party.class, "country_Code = ?", new String[]{app.currentSourceCountry.getCode()}, null, "local_Created_Date DESC", null);
        }catch (Exception e) {
            parties = new ArrayList<>();
        }

        adapter.clear();
        adapter.addAll(parties); //Honeycomb or above. Antes no existia el addAll
    }

    private void checkAndRequestPermissionsForContacts() {
        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.READ_CONTACTS},
                RemiteeApp.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", okListener)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(final int reqCode, final int resultCode, Intent data) {
        /*
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RemiteeApp.PICK_CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                party = new Party();

                Uri contactData = data.getData();
                ContentResolver cr = getActivity().getContentResolver();
                Cursor c = cr.query(contactData, null, null, null, null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        party.setContactId(contactId);

                        String displayName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String[] names = displayName.split(" ");

                        if (names.length > 0)
                            party.setFirstName(names[0]);

                        if (names.length > 1) {
                            String lastName = "";
                            for (int i = 1; i < names.length; i++) {
                                if (i > 1)
                                    lastName += " ";
                                lastName += names[i];
                            }

                            party.setLastName(lastName);
                        }

                        //Telefono
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{contactId}, null);
                            if (phoneCursor != null) {
                                if (phoneCursor.moveToFirst()) {
                                    party.setPhoneNumber(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                }

                                phoneCursor.close();
                            }
                        }


                        //EMail
                        Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{contactId}, null);
                        if (emailCursor != null) {
                            if (emailCursor.moveToFirst()) {
                                party.setEmail(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                            }
                            emailCursor.close();
            }

                        c.close();
                    }
                }

                openContactInfoDialog();
            }
        }
        */
    }

    private Dialog contactInfoDialog;
    private Boolean isNewContact = false;

    private void openContactInfoDialog() {
        contactInfoDialog = new Dialog(getActivity());

        contactInfoDialog.setTitle("Ingrese datos de contacto");
        contactInfoDialog.setContentView(R.layout.dialog_contact_info);

        contact_firstName_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_firstName_edittext);
        if (contact_firstName_edittext != null) {
            if (!isNewContact && party != null)
                contact_firstName_edittext.setText(party.getFirstName());
            contact_firstName_edittext.setFocusable(true);
            contact_firstName_edittext.setFocusableInTouchMode(true);
            contact_firstName_edittext.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        contact_lastName_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_lastName_edittext);
        if (contact_lastName_edittext != null && party != null && !isNewContact) {
            contact_lastName_edittext.setText(party.getLastName());
        }

        contact_countryCodePicker = (CountryCodePicker) contactInfoDialog.findViewById(R.id.contact_countryCodePicker);
        if (contact_countryCodePicker != null) {
            if (party != null && party.getPhoneCountryCode() != 0 && !isNewContact) {
                contact_countryCodePicker.setCountryForPhoneCode(party.getPhoneCountryCode());
            } else {
                contact_countryCodePicker.setCountryForPhoneCode(defaultPhoneCode);
            }
        }

        contact_phone_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_phone_edittext);
        contact_phone_areacode_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_phone_areacode_edittext);
        if (party != null && !isNewContact) {
            String phoneNumber = party.getPhoneNumber().replaceAll("[^0-9]","");
            String localPhoneNumber = "";
            String localWithAreaCodePhoneNumber = "";
            String areacode = "";

            //Detecto si los primeros caracteres coinciden con el phoneCode del country
            String countryCode = "";
            if (isSender)
                countryCode = String.valueOf(app.currentSourceCountry.getPhoneCode());
            else
                countryCode = String.valueOf(app.currentTargetCountry.getPhoneCode());

            if (phoneNumber.startsWith(countryCode) && phoneNumber.length() > phoneNumberLength)
                localWithAreaCodePhoneNumber = phoneNumber.substring(countryCode.length(), phoneNumber.length());
            else
                localWithAreaCodePhoneNumber = phoneNumber;

            if (phoneNumber.length() >= phoneNumberLength)
                localPhoneNumber = phoneNumber.substring(phoneNumber.length() - phoneNumberLength, phoneNumber.length());

            if (localWithAreaCodePhoneNumber.length() >= phoneNumberLength)
                areacode = localWithAreaCodePhoneNumber.substring(0, localWithAreaCodePhoneNumber.length() - phoneNumberLength);

            if (contact_phone_edittext != null) {
                contact_phone_edittext.setText(localPhoneNumber);
            }

            if (contact_phone_areacode_edittext != null) {
                contact_phone_areacode_edittext.setText(areacode);
            }
        }

        contact_documentNumber_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_documentNumber_edittext);
        if (contact_documentNumber_edittext != null) {

            if (party != null && !isNewContact)
                contact_documentNumber_edittext.setText(party.getDocumentNumber());

            contact_documentNumber_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (event == null)
                        return false;

                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return false;

                    if (actionId == EditorInfo.IME_ACTION_SEND || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (!v.getText().toString().isEmpty()) {
                            saveContact();
                        }

                        handled = true;
                    }
                    return handled;
                }
            });
        }

        Button saveContactButton = (Button) contactInfoDialog.findViewById(R.id.saveContactButton);
        if (saveContactButton != null) {

            saveContactButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO: agregar logica de Save
                    saveContact();
                }
            });
        }


        contactInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (contactSaved) {
                    GoToNextFrame();
                }
            }
        });

        contactInfoDialog.show();
    }

    Boolean contactSaved = false;

    private void saveContact() {
        if (!validatePhoneNumber()) {
            return;
        }

        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (isSender && !validateDocumentNumber()) {
            return;
        }

        if (isSender)
        {
            party.setCountryCode(app.currentSourceCountry.getCode());
        }
        else
        {
            party.setCountryCode(app.currentTargetCountry.getCode());
            party.setSenderId(app.currentTransaction.getSenderId());
        }

        //Si existe un contacto para el mismo pais con el mismo documento entonces hago un update de sus datos
        party.setDocumentNumber(contact_documentNumber_edittext.getText().toString());

        List<Party> parties = Party.find(Party.class, "country_Code = ? and document_Number = ?", new String[]{isSender ? app.currentSourceCountry.getCode() : app.currentTargetCountry.getCode(), party.getDocumentNumber()}, null, null, null);
        if (parties.size() > 0)
        {
            party = parties.get(0);
        }

        party.setFirstName(contact_firstName_edittext.getText().toString());
        party.setLastName(contact_lastName_edittext.getText().toString());
        party.setPhoneNumber(contactPhoneNumber.getInternational_format());
        party.setPhoneCountryCode(Integer.parseInt(contact_countryCodePicker.getSelectedCountryCode()));
        party.setCarrier(contactPhoneNumber.getCarrier());
        party.setLocation(contactPhoneNumber.getLocation());
        party.setLineType(contactPhoneNumber.getLine_type());

        UpdateTransaction();

        //TODO: Guardo los datos adicionales en la Agenda de Contactos del Teléfono

        party.save();

        contactSaved = true;

        contactInfoDialog.dismiss();
    }

    public class PhoneNumberVerifyTask extends net.iantech.android.remitee.tasks.PhoneNumberVerifyTask {
        public PhoneNumberVerifyTask(Activity _activity) {
            activity = _activity;
            app = (RemiteeApp) _activity.getApplicationContext();
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            //Busco el loading y por ahora lo saco
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Validando Teléfono...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(PhoneNumber phoneNumber) {

            progressDialog.cancel();

            //TODO: hacer algo con el Phone Number
            if (phoneNumber != null) {
                contactPhoneNumber = phoneNumber;
                if (!phoneNumber.getValid()) {
                    contact_phone_edittext.setError(getString(R.string.err_msg_phone_dont_exist));
                    requestFocus(contact_phone_edittext);
                } else {
                    //Vuelvo a intentar guardar

                    saveContact();
                }
            } else {
                contact_phone_edittext.setError(getString(R.string.err_msg_phone_cant_validate));
                requestFocus(contact_phone_edittext);
            }
        }
    }

    private boolean validateFirstName() {
        if (contact_firstName_edittext.getText().toString().trim().isEmpty()) {
            contact_firstName_edittext.setError(getString(R.string.err_msg_firstName));
            requestFocus(contact_firstName_edittext);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private boolean validateLastName() {
        if (contact_lastName_edittext.getText().toString().trim().isEmpty()) {
            contact_lastName_edittext.setError(getString(R.string.err_msg_lastName));
            requestFocus(contact_lastName_edittext);
            return false;
        }

        return true;
    }

    private Boolean validatePhoneNumber() {

        //Trimeo los leading ceros del código de area
        if (contact_phone_areacode_edittext != null) {
            if (contact_phone_areacode_edittext.getText().toString().trim().isEmpty()) {
                contact_phone_areacode_edittext.setError(getString(R.string.err_msg_phone_area));
                requestFocus(contact_phone_areacode_edittext);
                return false;
            }

            String areaCode = contact_phone_areacode_edittext.getText().toString();
            if (areaCode != "") {
                areaCode = areaCode.replaceFirst("^0+(?!$)", "");
                contact_phone_areacode_edittext.setText(areaCode);
            }
        }

        if (contactPhoneNumber != null && contactPhoneNumber.getValid())
            return true;

        /*
        String phoneNumberPattern = "";
        String phoneNumberFormatError = "";
        switch (defaultPhoneCode) {
            case 54:
                phoneNumberPattern = "[0-9]{10,11}";
                phoneNumberFormatError = "Deben ser entre 10 y 11 números";
                break;
            case 51:
                phoneNumberPattern = "[0-9]{8,9}"; //TODO: verificar que en Perú la longitud de los numeros con codigo de area sea entre 8 y 9 digitos
                phoneNumberFormatError = "Deben ser entre 8 y 9 números";
                break;
            case 595:
                phoneNumberPattern = "[0-9]{9}"; //TODO: verificar que en Paraguay la longitud de los numeros con codigo de area sea entre 8 y 9 digitos
                phoneNumberFormatError = "Deben ser 9 números";
                break;
            default:
                phoneNumberPattern = "[0-9]{10}";
                break;
        }


        String phoneNumber = contact_phone_edittext.getText().toString().replaceAll("\\W", "");
        if (!phoneNumber.matches(phoneNumberPattern)) {
            contact_phone_edittext.setError(phoneNumberFormatError);
            requestFocus(contact_phone_edittext);
            return false;
        } else {
*/
        //TODO: Por ahora no llamo a la API para poder hacer tests sin Internet
        contactPhoneNumber = new PhoneNumber();
        contactPhoneNumber.setInternational_format(String.format("%s%s%s", contact_countryCodePicker.getSelectedCountryCodeWithPlus(), contact_phone_areacode_edittext.getText().toString(), contact_phone_edittext.getText().toString()));
        contactPhoneNumber.setCarrier("Movistar Argentina");
        contactPhoneNumber.setLine_type("Mobile");
        contactPhoneNumber.setLocation("Buenos Aires");
        contactPhoneNumber.setValid(true);
        return true;


        //PhoneNumberVerifyTask phoneNumberVerifyTask = new PhoneNumberVerifyTask(getActivity());
        //phoneNumberVerifyTask.execute(contact_countryCodePicker.getSelectedCountryNameCode(), contact_phone_areacode_edittext.getText().toString(), contact_phone_edittext.getText().toString());
        // return false;
        //}


    }

    private Boolean validateDocumentNumber() {

        //TODO: Ver si tenemos que preguntar la Nacionalidad del Party antes del DNI
        String documentNumberPattern = "";
        switch (defaultPhoneCode) {
            case 54:
                documentNumberPattern = "[0-9]{7,8}";
                break;
            default:
                documentNumberPattern = "[0-9]{7,8}";
                break;
        }

        if (!contact_documentNumber_edittext.getText().toString().matches(documentNumberPattern)) {
            contact_documentNumber_edittext.setError(getString(R.string.err_msg_dni));
            requestFocus(contact_documentNumber_edittext);
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isSender)
            getActivity().setTitle("¿Quién Envía?");
        else
            getActivity().setTitle("¿Quién Recibe?");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ISSENDER_PARAM", isSender);
    }
}