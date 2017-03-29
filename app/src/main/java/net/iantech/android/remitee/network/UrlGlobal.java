package net.iantech.android.remitee.network;

import android.content.Context;

import net.iantech.android.remitee.enums.ConfigEnum;
import net.iantech.android.remitee.util.ConfigSingleton;

/**
 * Created by andres on 27/03/17.
 */

public class UrlGlobal {

    public static String createUrl(String url, Context mContext){
        StringBuilder builder = new StringBuilder();
        builder.append(ConfigSingleton.getInstance().getConfValue(ConfigEnum.URL));
        builder.append(url);
        return builder.toString();
    }

}