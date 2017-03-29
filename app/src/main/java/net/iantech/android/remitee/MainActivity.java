package net.iantech.android.remitee;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.*;
import android.util.Log;
import net.iantech.android.remitee.helpers.NetworkStateReceiver;
import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.model.PartyPaymentMethod;
import net.iantech.android.remitee.model.Trx;
import net.iantech.android.remitee.notifications.RegistrationIntentService;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

import java.util.List;


public class MainActivity extends RemiteeActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkStateReceiver.NetworkStateReceiverListener {

    public static final String PREFS_NAME = "RemiteePrefsFile";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;

    //For Google Play Services (Notifications)
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private GoogleCloudMessaging gcm;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static String PACKAGE_NAME;

    private DrawerLayout drawer;
    private Button receiveButton;
    private Button quoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CoordinatorLayout main_coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinatorLayout);
        if (main_coordinatorLayout != null) {

            AnimationDrawable transition = (AnimationDrawable) main_coordinatorLayout.getBackground();
            transition.setEnterFadeDuration(500);
            transition.setExitFadeDuration(1000);
            transition.start();
        }

/*
        List<Trx> transactions = Trx.find(Trx.class, null, null);
        for (Trx trx : transactions) {
            trx.delete();
        }

        List<Party> parties = Party.find(Party.class, null, null);
        for (Party party : parties) {
            party.delete();
        }

        List<PartyPaymentMethod> partyPaymentMethods = PartyPaymentMethod.find(PartyPaymentMethod.class, null, null);
        for (PartyPaymentMethod partyPaymentMethod : partyPaymentMethods) {
            partyPaymentMethod.delete();
        }

*/


        //Notifications
        /*
        mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, AzureNotificationsHandler.class);
        registerWithNotificationHubs();
        */

        PACKAGE_NAME = getApplicationContext().getPackageName();

        //En el primer frame si el usuario no está logueado no muestro el Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        //Chequeo si hay conexion a Internet
        quoteButton = (Button) findViewById(R.id.quoteButton);
        receiveButton = (Button) findViewById(R.id.receiveButton);

        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            changeButtonStatus(false);
            Snackbar.make(drawer, getString(R.string.no_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        //AccountKit
        if (app.accountKitId == null || app.accountKitId.isEmpty()) {
            //TODO: tira un error cuando no hay conexion a internet?
            this.getCurrentAccount();
        }

        if (quoteButton != null) {

            quoteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    GoToQuote();
                }
            });

        }


        if (receiveButton != null) {

            receiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Guardo un auditTrail para registrar esta acción
                    AuditTrail auditTrail = new AuditTrail("Inicio de Recepción", app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, "success");
                    SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(MainActivity.this);
                    saveAuditTrailTask.execute(auditTrail);

                    //Open ReceiveActivity
                    Intent intent = new Intent(MainActivity.this, ReceiveActivity.class);
                    startActivity(intent);
                }
            });

        }

        //Seteo el DeviceId y el devicePhoneNumber
        app.deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        TelephonyManager t = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            app.devicePhoneNumber = t.getLine1Number();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    private void GoToQuote()
    {
        //Guardo un auditTrail para registrar esta acción
        AuditTrail auditTrail = new AuditTrail("Inicio de Cotizacion", app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, "success");
        SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(MainActivity.this);
        saveAuditTrailTask.execute(auditTrail);


        //Open SendActivity
        Intent intent = new Intent(MainActivity.this, SendActivity.class);
        startActivity(intent);
    }

    private void changeButtonStatus(boolean enable) {
        if (quoteButton != null)
            quoteButton.setEnabled(enable);

        if (receiveButton != null)
            receiveButton.setEnabled(enable);
    }

    @Override
    public void networkAvailable() {
        changeButtonStatus(true);
    }

    @Override
    public void networkUnavailable() {
        changeButtonStatus(false);
        Snackbar.make(drawer, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_quote) {
            GoToQuote();
        }
        /*
        else if (id == R.id.nav_enterCode) {
            getFragmentManager().beginTransaction().replace(R.id.content_home, new EnterCodeFragment()).addToBackStack("Ingresar Codigo").commit();

        } else if (id == R.id.nav_matches) {
            getFragmentManager().beginTransaction().replace(R.id.content_home, new MatchesFragment()).addToBackStack("Partidos").commit();
        } else if (id == R.id.nav_home)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        */
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, getString(R.string.google_play_services_incompatible));
                ToastNotify(getString(R.string.google_play_services_incompatible));
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs() {
        Log.i(LOG_TAG, " Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

        changeButtonStatus(Utils.isNetworkAvailable(MainActivity.this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = findViewById(R.id.main_coordinatorLayout);
                if (view != null)
                    Snackbar.make(view, notificationMessage, Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });
    }
}
