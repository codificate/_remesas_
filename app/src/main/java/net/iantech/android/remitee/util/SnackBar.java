package net.iantech.android.remitee.util;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.iantech.android.remitee.R;

/**
 * Created by andres on 28/03/17.
 */

public class SnackBar {

    public static void makeMsg(String msg, View v) {
        try{
            Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
            View sbView = snackBar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorDarkGray));

            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorAccentDark));
            snackBar.show();
        } catch(RuntimeException e){
            e.printStackTrace();
            Toast.makeText(v.getContext(), "Favor intenta mas tarde", Toast.LENGTH_LONG).show();
        }
    }
}
