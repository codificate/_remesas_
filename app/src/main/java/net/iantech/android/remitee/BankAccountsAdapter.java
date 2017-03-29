package net.iantech.android.remitee;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.model.PartyPaymentMethod;

import java.util.ArrayList;

/**
 * Created by Lucas on 5/27/2016.
 */
public class BankAccountsAdapter extends ArrayAdapter<PartyPaymentMethod> {
    private final String LOG_TAG = BankAccountsAdapter.class.getSimpleName();

    public BankAccountsAdapter(Context context, ArrayList<PartyPaymentMethod> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final PartyPaymentMethod partyPaymentMethod = getItem(position);
        if (partyPaymentMethod == null)
            return null;

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_account_listitem, parent, false);

            TextView listitem_bank_name = (TextView) convertView.findViewById(R.id.listitem_bank_name);
            if (listitem_bank_name != null) {
                listitem_bank_name.setText(partyPaymentMethod.getBankName());
            }

            TextView listitem_bank_account = (TextView) convertView.findViewById(R.id.listitem_bank_account);
            if (listitem_bank_account != null) {
                listitem_bank_account.setText(partyPaymentMethod.getBankAccountNumberWithX());
            }
        }


        return convertView;
    }

}
