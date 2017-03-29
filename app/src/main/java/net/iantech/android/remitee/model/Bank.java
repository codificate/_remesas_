package net.iantech.android.remitee.model;

/*
  Created by Lucas on 1/16/2017.
*/
public class Bank {
    private int id;
    private String code;
    private String shortName;

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Bank()
    {}

    public Bank(int _id, String _code, String _shortName)
    {
        id = _id;
        code = _code;
        shortName = _shortName;
    }

    @Override
    public String toString()
    {
        return shortName;
    }
}
