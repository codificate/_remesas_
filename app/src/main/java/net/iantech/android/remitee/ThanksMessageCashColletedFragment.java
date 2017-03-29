package net.iantech.android.remitee;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Lucas on 8/1/2016.
 */
public class ThanksMessageCashColletedFragment extends Fragment {
    private final String LOG_TAG = ThanksMessageCashColletedFragment.class.getSimpleName();
    private RemiteeApp app;

    public ThanksMessageCashColletedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity)getActivity()).app;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cashcollected_thanks_message, container, false);
        if (rootView == null)
            return null;

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(getString(R.string.collect_cash_final_title));
    }
}