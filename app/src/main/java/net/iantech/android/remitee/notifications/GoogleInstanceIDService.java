package net.iantech.android.remitee.notifications;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
//import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Lucas on 6/2/2016.
*/
public class GoogleInstanceIDService extends InstanceIDListenerService {
    private static final String LOG_TAG = "GoogleInstanceIDService";

    @Override
    public void onTokenRefresh() {

        Log.i(LOG_TAG, "Refreshing GCM Registration Token");

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}