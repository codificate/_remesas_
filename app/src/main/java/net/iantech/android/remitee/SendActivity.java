package net.iantech.android.remitee;

import android.*;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.Trx;
import net.iantech.android.remitee.util.CaptureAndCropImage;

public class SendActivity extends RemiteeActivity {

    private final String LOG_TAG = SendActivity.class.getSimpleName();
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;
    public static final String CONTACT_INFO_FRAGMENT = "CONTACT_INFO_FRAGMENT";
    private static final int REQUEST_CAMERA = 0;
    CaptureAndCropImage mCaptureAndCropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Genero una nueva transaccion en memoria
        app.currentTransaction = new Trx();
        app.currentTransaction.setAccountId(app.accountKitId);
        app.currentTransaction.setAccountKitPhoneNumber(app.accountKitPhoneNumber);

        //TODO: Por ahora seteo Argentina como el pais de Origen
        Country country = new Country(1, "Argentina", "AR", "$", "ARS", "Pesos Argentinos", "flag_ar", 54, 0);
        app.currentSourceCountry = country;
        app.currentTransaction.setSourceCountryId(country.getId());
        app.currentTransaction.setSourceCountryCode(country.getCode());

        setContentView(R.layout.activity_send);

        //ActionBar actionBar = getSupportActionBar();

        //if (actionBar != null) {
        //    actionBar.setDisplayHomeAsUpEnabled(true); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
        //}

        Bundle bundle = getIntent().getExtras();

        if( bundle != null && bundle.getBoolean("ContactInfoWasVisible")  )
            getFragmentManager().beginTransaction().replace(R.id.activity_send, new ContactInfoFragment().newInstance( savedInstanceState.getBoolean("ISSENDER_PARAM") )).addToBackStack("Crear contacto enviador").commit();
        else
            getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuoteSelectTargetCountryFragment(), "QuoteSelectTargetCountryFragment").addToBackStack("Seleccionar Pais").commit();
    }



    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            mCaptureAndCropImage = new CaptureAndCropImage(this);
            mCaptureAndCropImage.createChooser();
        }
    }

    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }

    private void goBack()
    {
        hideSoftKeyboard();

        int count = getFragmentManager().getBackStackEntryCount();

        //Con esto detecto si estoy en el ultimo fragment. En ese caso vuelvo a la home
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(count - 1);
        if (backEntry != null) {
            String tag = backEntry.getName();
            if (tag != null && tag.equals(getString(R.string.send_confirm)))
            {
                //Open MainActivity
                Intent intent = new Intent(SendActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        }

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else if (count == 1) {
            //Open MainActivity
            Intent intent = new Intent(SendActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                goBack();
                return true;
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {

        goBack();
    }

    @Override
    protected void onStart() { super.onStart(); }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
