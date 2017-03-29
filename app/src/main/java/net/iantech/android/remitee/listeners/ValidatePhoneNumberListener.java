package net.iantech.android.remitee.listeners;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;

import net.iantech.android.remitee.BuildConfig;
import net.iantech.android.remitee.RemiteeApp;
import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.model.PhoneNumber;
import net.iantech.android.remitee.util.GsonSingleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andres on 27/03/17.
 */

public class ValidatePhoneNumberListener implements Response.Listener<String>, Response.ErrorListener{

    private PhoneNumber apiResponse;
    private Party party;
    private Boolean isSender;
    private RemiteeApp app;

    public ValidatePhoneNumberListener ( Party _party, Boolean _isSender, RemiteeApp _app ) {
        this.party = _party;
        this.isSender = _isSender;
        this.app= _app;
    }

    @Override
    public void onResponse(String response) {

        try{

            JSONObject _response = new JSONObject(response);

            if ( _response.has("location") ) {

                try{

                    Logger.json(_response.toString());
                    apiResponse = GsonSingleton.getInstance().getGson().fromJson( response, PhoneNumber.class);
                    this.setApiResponse( apiResponse );

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
