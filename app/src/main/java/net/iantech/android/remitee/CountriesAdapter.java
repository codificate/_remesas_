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

import net.iantech.android.remitee.model.Country;
import java.util.ArrayList;

/**
 * Created by Lucas on 5/27/2016.
 */
public class CountriesAdapter extends ArrayAdapter<Country> {
    private final String LOG_TAG = CountriesAdapter.class.getSimpleName();

    public CountriesAdapter(Context context, ArrayList<Country> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Country country = getItem(position);
        if (country == null)
            return null;

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_listitem, parent, false);

            /*
            if (!country.getCode().equals("AR"))
            {
                convertView.setAlpha(0.5f);
            }
            */

            ImageView country_flag = (ImageView) convertView.findViewById(R.id.country_flag);
            if (country_flag != null)
            {
                String uri = "@drawable/" + country.getFlag();

                int imageResource = parent.getResources().getIdentifier(uri, null, MainActivity.PACKAGE_NAME);
                Drawable res = ResourcesCompat.getDrawable(parent.getResources(), imageResource, null);

                country_flag.setImageDrawable(res);
            }


            TextView country_name = (TextView) convertView.findViewById(R.id.country_name);
            if (country_name != null) {
                country_name.setText(country.getName());
            }
        }


        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        /*
        if(position != 0){
            return false;
        }
        */
        return true;
    }

}
