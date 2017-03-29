package net.iantech.android.remitee.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.iantech.android.remitee.enums.TagsEnum;
import net.iantech.android.remitee.util.JsonObjectRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andres on 27/03/17.
 */

public class HandlerRequest {

    private static JsonObjectRequest jsonObjectRequest = null;
    private static StringRequest stringRequest = null;
    private static Gson gson = null;
    private static String url = null;
    private static String TAG = TagsEnum.LOGINTAG.toString();
    private final String LANG = "lang";
    private final String USER_ID = "user_id";
    private final String TOKEN_DEVICE= "token_device";

    JSONObject jsonObject = null;

    public HandlerRequest(String _url){

        this.url = _url;
        this.gson = new GsonBuilder().create();
    }

    public void
    requestAttend(int _method, Context _context, Object _obj, Response.Listener<JSONObject> _listener, Response.ErrorListener _errorListener) {

        this.setJsonObject(_obj);

        String urlCreated = UrlGlobal.createUrl(url, _context);
        jsonObjectRequest = new JsonObjectRequestUtil(_method, urlCreated, this.getJsonObject(), _listener, _errorListener);
        jsonObjectRequest.setTag(TAG);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(_context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void
    requestLogout(int _method, Context _context, String lang, String userId, String tokenDevice, String authorization, Response.Listener<JSONObject> _listener, Response.ErrorListener _errorListener) {

        JSONObject mUserJsonObject = null;
        try {
            mUserJsonObject = new JSONObject();
            mUserJsonObject.put(LANG, lang);
            mUserJsonObject.put(USER_ID, userId);
            mUserJsonObject.put(TOKEN_DEVICE, tokenDevice);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String urlCreated = UrlGlobal.createUrl(url, _context);
        jsonObjectRequest = new JsonObjectRequestUtil(_method, urlCreated, mUserJsonObject, _listener, _errorListener, authorization);
        jsonObjectRequest.setTag(TAG);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(_context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void
    requestAttenWithHeaders(int _method, Context _context, Object _obj,
                            Response.Listener<JSONObject> _listener, Response.ErrorListener _errorListener, final Map<String, String> headers){

        this.setJsonObject(_obj);

        String urlCreated = UrlGlobal.createUrl(url, _context);
        jsonObjectRequest = new JsonObjectRequestUtil(_method, urlCreated, this.getJsonObject(), _listener, _errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //loop a Map
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    params.put( entry.getKey(),  entry.getValue() );
                }

                return params;
            }
        };
        jsonObjectRequest.setTag(TAG);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(_context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }



    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(Object _obj) {

        try {
            this.jsonObject = new JSONObject( gson.toJson( _obj ).toString() );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void
    stringRequestAttenWithHeaders(int _method, Context _context,
                                  Response.Listener<String> _listener, Response.ErrorListener _errorListener, final Map<String, String> headers){

        String urlCreated = UrlGlobal.createUrl(url, _context);
        stringRequest = new StringRequest(_method, urlCreated, _listener, _errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //loop a Map
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    params.put( entry.getKey(),  entry.getValue() );
                }

                return params;
            }
        };
        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(_context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void stringRequestAttend( int _method, Context _context,
                                     Response.Listener<String> _listener, Response.ErrorListener _errorListener ){
        String urlCreated = url;
        stringRequest = new StringRequest( _method, urlCreated, _listener, _errorListener );
        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(_context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

}