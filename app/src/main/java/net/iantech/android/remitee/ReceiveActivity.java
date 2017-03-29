package net.iantech.android.remitee;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;

import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.Trx;

public class ReceiveActivity extends RemiteeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
        }

        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken == null) {
            onLoginPhone(findViewById(R.id.activity_receive));
        }
        else
        {
            getFragmentManager().beginTransaction().replace(R.id.activity_receive, new TransactionCodeFragment()).addToBackStack("Ingresar Código de Trx").commit();
        }
    }

    private void goBack()
    {
        hideSoftKeyboard();

        int count = getFragmentManager().getBackStackEntryCount();

        //Con esto detecto si estoy en el ultimo fragment. En ese caso vuelvo a la home
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(count - 1);
        if (backEntry != null) {
            String tag = backEntry.getName();
            if (tag != null && tag.equals(getString(R.string.collect_cash_verify_title)))
            {
                //Open MainActivity
                Intent intent = new Intent(ReceiveActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        }

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else if (count == 1) {
            //Open MainActivity
            Intent intent = new Intent(ReceiveActivity.this, MainActivity.class);
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
}
