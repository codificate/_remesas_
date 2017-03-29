package net.iantech.android.remitee.model;

import java.util.Calendar;

/*
  Created by Lucas on 7/29/2016.
*/
public class Country {
    private int id;
    private String code;
    private String name;
    private String currencySymbol;
    private String currencyCode;
    private String currencyName;
    private String flag;
    private int phoneCode;
    private double exchangeRateSpread;

    public Country(int _id, String _name, String _code, String _currencySymbol, String _currencyCode, String _currencyName, String _flag, int _phoneCode, double _exchangeRateSpread) {
        id = _id;
        code = _code;
        name = _name;
        currencySymbol = _currencySymbol;
        currencyCode = _currencyCode;
        currencyName = _currencyName;
        flag = _flag;
        phoneCode = _phoneCode;
        exchangeRateSpread = _exchangeRateSpread;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getFlag() {
        return flag;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public int getPhoneCode() {
        return phoneCode;
    }

    public double getExchangeRateSpread() {
        return exchangeRateSpread;
    }
}
