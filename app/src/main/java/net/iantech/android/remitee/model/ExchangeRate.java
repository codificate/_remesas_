package net.iantech.android.remitee.model;

import java.util.HashMap;
import java.util.Map;

/*
  Created by Lucas on 7/29/2016.
*/
public class ExchangeRate {
    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;

    private int timestamp;

    private String source;

    private Map<String,Double> quotes;

    public ExchangeRate() {
        quotes =  new HashMap<>();
    }

    public Map<String,Double> getQuotes() {
        return quotes;
    }

    public boolean getSuccess(){ return success; }

    public void addQuote(String key, Double value)
    {
        quotes.put(key, value);
    }
}
