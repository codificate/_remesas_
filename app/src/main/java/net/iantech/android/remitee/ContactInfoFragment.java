package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hbb20.CountryCodePicker;
import com.orhanobut.logger.Logger;

import net.iantech.android.remitee.enums.ApiUrl;
import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.model.PhoneNumber;
import net.iantech.android.remitee.network.HandlerRequest;
import net.iantech.android.remitee.util.GsonSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by andres on 23/03/17.
 */

public class ContactInfoFragment extends Fragment {

    private final String LOG_TAG = ContactInfoFragment.class.getSimpleName();
    private RemiteeApp app;
    private Party party;

    private Boolean isNewContact = false;
    private Button saveContactButton;
    private Button senderSelectFromContactsButton;
    private CountryCodePicker contact_countryCodePicker;
    private ProgressDialog progressDialog;
    private int defaultPhoneCode;
    private int phoneNumberLength;
    private EditText contact_documentNumber_edittext;
    private EditText contact_firstName_edittext;
    private EditText contact_lastName_edittext;
    private EditText contact_phone_edittext;
    private EditText contact_phone_areacode_edittext;
    private PhoneNumber contactPhoneNumber;

    private static final String ISSENDER_PARAM = "isSender";
    private Boolean isSender;

    public ContactInfoFragment(){}

    public static ContactInfoFragment newInstance(Boolean _isSender) {
        ContactInfoFragment fragment = new ContactInfoFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISSENDER_PARAM, _isSender);
        args.putBoolean("ContactInfoWasVisible", Boolean.TRUE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

        if (getArguments() != null) {
            isSender = getArguments().getBoolean(ISSENDER_PARAM);

            try {
                //Obtengo desde el bundle el objeto party seleccionado
                party = GsonSingleton.getInstance().getGson().fromJson( getArguments().getString( "partySenderSelected" ), Party.class);
                //party = Party.findById( Party.class, 1 );
            } catch ( Exception e ) {
                party = null;
                e.printStackTrace();
            }
        } else {
            Bundle bundle = savedInstanceState;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contactInfoDialog = inflater.inflate(R.layout.dialog_contact_info, container, false);

        contact_firstName_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_firstName_edittext);
        contact_lastName_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_lastName_edittext);
        contact_countryCodePicker = (CountryCodePicker) contactInfoDialog.findViewById(R.id.contact_countryCodePicker);
        contact_phone_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_phone_edittext);
        contact_phone_areacode_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_phone_areacode_edittext);
        contact_documentNumber_edittext = (EditText) contactInfoDialog.findViewById(R.id.contact_documentNumber_edittext);
        saveContactButton = (Button) contactInfoDialog.findViewById(R.id.saveContactButton);
        senderSelectFromContactsButton = (Button) contactInfoDialog.findViewById(R.id.senderSelectFromContactsButton);

        setFocusOnFirstName();
        setFocusOnLastName();
        setFocusOnCountryCode();
        setFocusOnDocumentNumber();

        if ( senderSelectFromContactsButton != null ) {
            senderSelectFromContactsButton.setOnClickListener(new SelectFromContactsOnClickListener());
        }

        if (saveContactButton != null) {
            saveContactButton.setOnClickListener(new SaveContactOnClickListener());
        }

        return contactInfoDialog;
    }

    private void setFocusOnFirstName(){
        if (contact_firstName_edittext != null) {
            if (!isNewContact && party != null)
                contact_firstName_edittext.setText(party.getFirstName());
            contact_firstName_edittext.setFocusable(true);
            contact_firstName_edittext.setFocusableInTouchMode(true);
            contact_firstName_edittext.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void setFocusOnLastName() {
        if (contact_lastName_edittext != null && party != null && !isNewContact) {
            contact_lastName_edittext.setText(party.getLastName());
        }
    }

    private void setFocusOnCountryCode() {
        if (contact_countryCodePicker != null) {
            if (party != null && party.getPhoneCountryCode() != 0 && !isNewContact) {
                contact_countryCodePicker.setCountryForPhoneCode(party.getPhoneCountryCode());
            } else {
                contact_countryCodePicker.setCountryForPhoneCode(defaultPhoneCode);
            }
        }
    }

    private void setFocusOnDocumentNumber() {
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
    }

    public void onActivityResult(final int reqCode, final int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RemiteeApp.PICK_CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                putDataFromAgenda(data);
            }
        }
    }

    private void putDataFromAgenda( Intent data ){

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

                if (names.length > 0) {
                    contact_firstName_edittext.setText( names[0] );
                    party.setFirstName(names[0]);
                }

                if (names.length > 1) {

                    String lastName = "";

                    for (int i = 1; i < names.length; i++) {
                        if (i > 1)
                            lastName += " ";
                        lastName += names[i];
                    }

                    contact_lastName_edittext.setText( lastName );
                    party.setLastName(lastName);
                }

                //Telefono
                if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);
                    if (phoneCursor != null) {
                        if (phoneCursor.moveToFirst()) {
                            contact_phone_edittext.setText( phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) );
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
                        //party.setEmail(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                    }
                    emailCursor.close();
                }

                c.close();
            }
        }
    }

    private void saveContact() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Guardando...");
        progressDialog.show();

        boolean contactSaved = Boolean.FALSE;

        if ( !validateFirstName() )
            return;
        if ( !validateLastName() )
            return;
        if ( !validateDocumentNumber() )
            return;

        saveAfterValidatePhoneNumber();
    }

    private boolean validateFirstName() {

        if (contact_firstName_edittext.getText().toString().trim().isEmpty()) {
            contact_firstName_edittext.setError(getString(R.string.err_msg_firstName));
            requestFocus(contact_firstName_edittext);
            return false;
        }
        return true;
    }

    private boolean validateLastName() {

        if (contact_lastName_edittext.getText().toString().trim().isEmpty()) {
            contact_lastName_edittext.setError(getString(R.string.err_msg_lastName));
            requestFocus(contact_lastName_edittext);
            return false;
        }
        return true;
    }

    public void saveAfterValidatePhoneNumber() {

        String url = null;
        boolean isvalid = Boolean.FALSE;
        String phone = contact_phone_edittext.getText().toString();
        String area_code = contact_phone_areacode_edittext.getText().toString();
        String country_code = contact_countryCodePicker.getSelectedCountryNameCode();

        //Trimeo los leading ceros del código de area
        if (contact_phone_areacode_edittext != null) {
            if (contact_phone_areacode_edittext.getText().toString().trim().isEmpty()) {
                contact_phone_areacode_edittext.setError(getString(R.string.err_msg_phone_area));
                requestFocus(contact_phone_areacode_edittext);
                return;
            }

            String areaCode = contact_phone_areacode_edittext.getText().toString();
            if (areaCode != "") {
                areaCode = areaCode.replaceFirst("^(0|9)+(?!$)", "");
                contact_phone_areacode_edittext.setText(areaCode);
            }
        }

        ValidatePhoneNumberListener validatePhoneListener = new ValidatePhoneNumberListener();

        url = ApiUrl.VALIDATE_PHONE_NUMBER.url.concat( "?access_key=0079e381ab3f913ac8cd870989eab296"+
                "&number="+String.format("%s%s", area_code, phone)+
                "&country_code="+country_code+
                "&format=1" );

        HandlerRequest handlerRequest = new HandlerRequest( url );
        handlerRequest.stringRequestAttend( ApiUrl.VALIDATE_PHONE_NUMBER.method,
                getActivity().getApplicationContext(),
                validatePhoneListener,
                validatePhoneListener );
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", okListener)
                .create()
                .show();
    }

    private class SaveContactOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            saveContact();
        }
    }

    private class SelectFromContactsOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //Pregunto por los permisos
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_CONTACTS)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    showDialogOK(getString(R.string.remitee_requires_access_contacts),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkAndRequestPermissionsForContacts();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            Snackbar.make(getView(), getString(R.string.access_contacts_denied), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                            break;
                                    }
                                }
                            });

                } else {
                    checkAndRequestPermissionsForContacts();
                }
            } else {
                //isNewContact = false;
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, RemiteeApp.PICK_CONTACT_REQUEST_CODE);
            }
        }
    }

    private void checkAndRequestPermissionsForContacts() {
        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.READ_CONTACTS},
                RemiteeApp.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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

    private class ValidatePhoneNumberListener implements Response.Listener<String>, Response.ErrorListener{

        private PhoneNumber apiResponse;

        @Override
        public void onResponse(String response) {

            try{

                JSONObject _response = new JSONObject(response);

                if ( _response.has("location") ) {

                    try{

                        Logger.json(_response.toString());
                        apiResponse = GsonSingleton.getInstance().getGson().fromJson( response, PhoneNumber.class);
                        this.setApiResponse( apiResponse );

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

                        try {
                            if ( party.save() > 0L ) {
                                getArguments().putString( "partySenderSelected", GsonSingleton.getInstance().getGson().toJson(party) );
                                progressDialog.cancel();
                                getFragmentManager().beginTransaction().replace(R.id.activity_send, new TakePhotoCredentials().newInstance(isSender)).addToBackStack(getString(R.string.take_photo_credentials_fragment)).commit();
                            }
                        }catch ( Exception e ) {
                            e.printStackTrace();
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Logger.json(_response.toString());
                }

            }catch ( Exception e ) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;

            if( response != null && response.data != null ){
                try {
                    JSONObject json = new JSONObject(new String(response.data));
                    if (BuildConfig.DEBUG)
                        Logger.json(json.toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }

        private void setApiResponse(PhoneNumber apiResponse) {
            this.apiResponse = apiResponse;
        }

        public PhoneNumber getApiResponse() {
            return apiResponse;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isSender)
            getActivity().setTitle("Crear contacto enviador");
        else
            getActivity().setTitle("Crear contacto receptor");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ContactInfoWasVisible", Boolean.TRUE);
    }
}