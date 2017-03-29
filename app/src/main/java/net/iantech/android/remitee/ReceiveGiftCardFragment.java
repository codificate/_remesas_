package net.iantech.android.remitee;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by Lucas on 8/1/2016.
 */
public class ReceiveGiftCardFragment extends Fragment {
    private final String LOG_TAG = ReceiveGiftCardFragment.class.getSimpleName();
    private ProgressDialog progressDialog;

    private RemiteeApp app;

    public ReceiveGiftCardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gift_card, container, false);
        if (rootView == null)
            return null;

        TextView gift_card_amount_textView = (TextView)rootView.findViewById(R.id.gift_card_amount_textView);
        if (gift_card_amount_textView != null)
        {
            String amount = app.currentTargetCountry.getCurrencySymbol() + " " + String.format(Locale.US, "%1$,.0f", app.currentTransaction.getRecipientsAmount());
            gift_card_amount_textView.setText(amount);
        }


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Pague su Supermercado");
    }
}