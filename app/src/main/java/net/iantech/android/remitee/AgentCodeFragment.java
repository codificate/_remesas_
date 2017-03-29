package net.iantech.android.remitee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import net.iantech.android.remitee.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class AgentCodeFragment extends Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private final String LOG_TAG = AgentCodeFragment.class.getSimpleName();
    private RemiteeApp app;
    private ProgressDialog progressDialog;

    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinFifthDigitEditText;
    private EditText mPinHiddenEditText;

    private static final String ISSENDER_PARAM = "isSender";
    private Boolean isSender;

    public AgentCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((RemiteeActivity) getActivity()).app;

        if (getArguments() != null) {
            isSender = getArguments().getBoolean(ISSENDER_PARAM);
        }
    }

    public static AgentCodeFragment newInstance(Boolean _isSender) {
        AgentCodeFragment fragment = new AgentCodeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISSENDER_PARAM, _isSender);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agent_code, container, false);
        if (rootView == null)
            return null;

        mPinFirstDigitEditText = (EditText) rootView.findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) rootView.findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) rootView.findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) rootView.findViewById(R.id.pin_forth_edittext);
        mPinFifthDigitEditText = (EditText) rootView.findViewById(R.id.pin_fifth_edittext);
        mPinHiddenEditText = (EditText) rootView.findViewById(R.id.pin_hidden_edittext);

        Button validateAgentsCodeButton = (Button) rootView.findViewById(R.id.validateAgentsCodeButton);
        if (validateAgentsCodeButton != null) {
            validateAgentsCodeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mPinHiddenEditText.length() == 5)
                        validateCode();
                }
            });
        }

        setPINListeners();

        return rootView;
    }

    private void validateCode() {
        ValidateAgentCodeTask validateAgentCodeTask = new ValidateAgentCodeTask(getActivity());
        validateAgentCodeTask.execute(mPinHiddenEditText.getText().toString());
    }

    public class ValidateAgentCodeTask extends AsyncTask<String, Void, User> {
        private Activity activity;

        public ValidateAgentCodeTask(Activity _activity) {
            activity = _activity;
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Validando Agente...");
            progressDialog.show();
        }

        @Override
        protected User doInBackground(String... params) {

            //Solo voy al server si hay conexion a Internet
            if (Utils.isNetworkAvailable(activity)) {
                Gson gson = new Gson();
                User user = new User(app.accountKitId, app.accountKitPhoneNumber, params[0], true, app.currentTransaction.getAmount());
                String JsonDATA = gson.toJson(user);
                String JsonResponse = null;
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                try {
                    final String BASE_URL = BuildConfig.DEBUG ? app.base_dev_url : app.base_qa_url;

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath("api")
                            .appendPath("user")
                            .appendPath("validateAgentCode")
                            .build();

                    URL url = new URL(builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(app.connectionTimeOut);
                    urlConnection.setReadTimeout(app.readTimeout);
                    // is output buffer writter
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                    Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                    writer.write(JsonDATA);
// json data
                    writer.close();
                    InputStream inputStream = urlConnection.getInputStream();
//input stream
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        buffer.append(inputLine).append("\n");
                    if (buffer.length() == 0) {
                        // Stream was empty. No point in parsing.
                        return null;
                    }
                    JsonResponse = buffer.toString();
//response data
                    Log.i(LOG_TAG, JsonResponse);
                    //send to post execute
                    return gson.fromJson(JsonResponse, User.class);
                } catch (MalformedURLException | ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            //saco el loading
            progressDialog.cancel();

            //Blanqueo los edittexts
            mPinFirstDigitEditText.setText("");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinHiddenEditText.setText("");

            if (user != null) {

                String errorMessage = "";
                app.currentTransaction.setUserId(user.getId());

                if (user.getIsDisabled()) {
                    errorMessage = getString(R.string.agent_code_error1);
                }

                if (isSender) {
                    app.currentTransaction.setSenderAgentId(user.getId());

                    if (!user.getCanCollect())
                    {
                        errorMessage = getString(R.string.agent_code_error2);
                    }

                    if (!user.getHasCredit())
                    {
                        errorMessage = getString(R.string.agent_code_error4);
                    }

                    if (user.getCountryId() != app.currentTransaction.getSourceCountryId())
                    {
                        errorMessage = getString(R.string.agent_code_error6);
                    }
                } else {
                    app.currentTransaction.setRecipientAgentId(user.getId());

                    if (!user.getCanPay())
                    {
                        errorMessage = getString(R.string.agent_code_error5);
                    }

                    if (user.getCountryId() != app.currentTransaction.getTargetCountryId())
                    {
                        errorMessage = getString(R.string.agent_code_error6);
                    }

                    if (errorMessage == "")
                    {
                        if (app.currentTransaction.getStatus() == 6)
                        {
                            errorMessage = getString(R.string.agent_code_error7);
                        }

                        if (app.currentTransaction.getStatus() == 7)
                        {
                            errorMessage = getString(R.string.agent_code_error8);
                        }
                    }
                }

                if (errorMessage.isEmpty()) {
                    GoToNextFrame();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.remitee_message))
                            .setMessage(errorMessage)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }

            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.remitee_message))
                        .setMessage(getString(R.string.agent_code_error3))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setFocus(mPinFirstDigitEditText);
                            }
                        }).show();
            }
        }
    }

    private void GoToNextFrame() {
        if (isSender) {
            //Navego hacia el fragment del tercer paso
            getFragmentManager().beginTransaction().replace(R.id.activity_send, SelectPartyFragment.newInstance(true)).addToBackStack(getString(R.string.agent_code_title)).commit();
        }
        else
        {
            getFragmentManager().beginTransaction().replace(R.id.activity_receive, new CollectCashFragment()).addToBackStack(getString(R.string.agent_code_title)).commit();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_fifth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 5)
                            mPinFifthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 2) {
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 3) {
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 5) {
            mPinFifthDigitEditText.setText(s.charAt(4) + "");

            hideSoftKeyboard(mPinFifthDigitEditText);

            validateCode();
        }
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);
        mPinFifthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinFifthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);

        mPinFirstDigitEditText.requestFocus();
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editText, 0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.agent_code_title));
    }
}
