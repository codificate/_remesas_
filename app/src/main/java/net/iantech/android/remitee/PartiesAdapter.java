package net.iantech.android.remitee;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.model.Party;

import java.util.ArrayList;

/**
 * Created by Lucas on 5/27/2016.
 */
public class PartiesAdapter extends ArrayAdapter<Party> {
    private final String LOG_TAG = PartiesAdapter.class.getSimpleName();

    public PartiesAdapter(Context context, ArrayList<Party> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final Party party = getItem(position);
        if (party == null)
            return null;

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.party_listitem, parent, false);

            ImageView country_flag = (ImageView) convertView.findViewById(R.id.listitem_party_country_flag);
            if (country_flag != null) {
                String uri = "@drawable/flag_" + party.getCountryCode().toLowerCase();

                int imageResource = parent.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
                if (imageResource != 0) {
                    Drawable res = ResourcesCompat.getDrawable(parent.getResources(), imageResource, null);
                    country_flag.setImageDrawable(res);
                }
            }


            TextView listitem_party_name = (TextView) convertView.findViewById(R.id.listitem_party_name);
            if (listitem_party_name != null) {
                listitem_party_name.setText(party.getName());
            }
        }


        return convertView;
    }

}
