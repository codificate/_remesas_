package net.iantech.android.remitee;

import android.app.Fragment;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Lucas on 8/1/2016.
 */
public class ThanksMessageFragment extends Fragment {
    private final String LOG_TAG = ThanksMessageFragment.class.getSimpleName();
    private RemiteeApp app;

    private static final String TRACKINGNUMBER_PARAM = "trackingNumber";
    private String trackingNumber;

    public ThanksMessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity)getActivity()).app;

        if (getArguments() != null) {
            trackingNumber = getArguments().getString(TRACKINGNUMBER_PARAM);
        }
    }

    public static ThanksMessageFragment newInstance(String _trackingNumber) {
        ThanksMessageFragment fragment = new ThanksMessageFragment();
        Bundle args = new Bundle();
        args.putString(TRACKINGNUMBER_PARAM, _trackingNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thanks_message, container, false);
        if (rootView == null)
            return null;

        //Si el pago es en efectivo a través de un Agente, no se muestra el codigo de operación.
        if (app.currentTransaction.getPaymentMethod() == 1) {
            LinearLayout thanksMessageFragment_code_layout = (LinearLayout) rootView.findViewById(R.id.thanksMessageFragment_code_layout);
            if (thanksMessageFragment_code_layout != null) {
                thanksMessageFragment_code_layout.setVisibility(View.GONE);
            }
        }

        TextView your_tracking_number_textView = (TextView) rootView.findViewById(R.id.your_tracking_number_textView);
        if (your_tracking_number_textView != null) {
            your_tracking_number_textView.setText(trackingNumber);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Muchas Gracias!");
    }
}