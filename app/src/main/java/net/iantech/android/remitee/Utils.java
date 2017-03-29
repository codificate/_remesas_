package net.iantech.android.remitee;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 8/1/2016.
 */
public class Utils {
    public static String getJsonDate(Calendar calendar) {
        if (calendar != null) {
            return calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
                    + "T" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" + "00";
        }
        return null;
    }

    public static boolean validatePhoneNumber(String phoneNumber)
    {
        String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static int calculateCUITDigit(String cuit){
        int[] mult = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        char[] nums = cuit.toCharArray();
        int total = 0;
        for (int i = 0; i < mult.length; i++)
        {
            total += Character.getNumericValue(nums[i]) * mult[i];
        }
        int resto = total % 11;
        return resto == 0 ? 0 : resto == 1 ? 9 : 11 - resto;
    }

    public static boolean isValidCUIT(String cuit) {

        //Quito los guiones y espacios, el cuit resultante debe tener 11 caracteres.
        cuit = cuit.replaceAll("\\W", "").replaceAll("-", "");

        if (cuit.length() != 11)
        {
            return false;
        }
        else
        {
            int calculado = calculateCUITDigit(cuit);
            int digito = Integer.parseInt(cuit.substring(10));
            return calculado == digito;
        }
    }
}
