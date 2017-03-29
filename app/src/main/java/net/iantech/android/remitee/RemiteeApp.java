package net.iantech.android.remitee;

import android.app.Service;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.facebook.accountkit.AccountKit;

import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.Trx;

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Lucas on 7/29/2016.
 */
public class RemiteeApp extends com.orm.SugarApp {

    private final String LOG_TAG = RemiteeApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        //Facebook Account Kit initialize
        AccountKit.initialize(getApplicationContext());

        // Initialize the SDK before executing any other operations,
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

        /*
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(LOG_TAG + Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });
        */
    }

    //Facebook Acccount Kit
    public String accountKitId;
    public String accountKitPhoneNumber;
    public String accountKitEmail;

    public final String base_dev_url = "http://remitee.azurewebsites.net/";
    //"http://10.0.2.2:11684";
    //public final String base_dev_url = "http://remitee.azurewebsites.net/";
    public final String base_qa_url = "http://remitee.azurewebsites.net/";
    public final String base_prod_url = "http://remitee.com/";
    public final int connectionTimeOut = 8000;
    public final int readTimeout = 15000;

    public Country currentSourceCountry;

    public Country currentTargetCountry;

    public Trx currentTransaction;

    public static final int FACEBOOK_APP_REQUEST_CODE = 99;
    public static final int PICK_CONTACT_REQUEST_CODE = 98;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 97;

    public String deviceId;
    public String devicePhoneNumber;
}
