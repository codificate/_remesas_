package net.iantech.android.remitee;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

/**
 * Created by Lucas on 10/18/2016.
 */
public class RemiteeActivity extends AppCompatActivity {
    public RemiteeApp app;
    private final String LOG_TAG = RemiteeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (RemiteeApp)getApplicationContext();
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) this.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText()) {
                inputMethodManager.hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception e)
        {

        }
    }

    public void getCurrentAccount()
    {
        //TODO: esto lo agregue porque tira un error si no hay conexion a Internet
        //Esto es un error del Facebook Account Kit que supuestamente van a corregir con la nueva version del SDK
        if (Utils.isNetworkAvailable(RemiteeActivity.this)) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // Get Account Kit ID
                    app.accountKitId = account.getId();

                    // Get phone number
                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    app.accountKitPhoneNumber = phoneNumber.toString();

                    // Get email
                    app.accountKitEmail = account.getEmail();
                }

                @Override
                public void onError(final AccountKitError error) {
                    //TODO: hacer algo aca
                    Log.e(LOG_TAG, error.toString());
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //getCurrentAccount();
    }

    public void onLoginPhone(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN

        configurationBuilder.setDefaultCountryCode(app.currentSourceCountry.getCode());

        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, RemiteeApp.FACEBOOK_APP_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RemiteeApp.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //Abro la Agenda
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, RemiteeApp.PICK_CONTACT_REQUEST_CODE);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
