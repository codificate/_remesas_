package net.iantech.android.remitee.model;

/*
  Created by Lucas on 7/29/2016.
*/
public class Corridor {

    private int id;
    public double sourceCountryTaxRate;
    public double targetCountryTaxRate;
    public int sourceCountryId;
    public int targetCountryId;
    public double exchangeRateSpread;
    public double exchangeRate;
    public double sourceTransactionFee;
    public double targetTransactionFee;
    public double agentDiscount;
    public double exchangeRateUSD;
    public double referenceAmount;

    public double getExchangeRateUSD() {
        return exchangeRateUSD;
    }

    public void setExchangeRateUSD(double exchangeRateUSD) {
        this.exchangeRateUSD = exchangeRateUSD;
    }

    public double getReferenceAmount() {
        return referenceAmount;
    }

    public void setReferenceAmount(double referenceAmount) {
        this.referenceAmount = referenceAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSourceCountryTaxRate() {
        return sourceCountryTaxRate;
    }

    public void setSourceCountryTaxRate(double sourceCountryTaxRate) {
        this.sourceCountryTaxRate = sourceCountryTaxRate;
    }

    public double getTargetCountryTaxRate() {
        return targetCountryTaxRate;
    }

    public void setTargetCountryTaxRate(double targetCountryTaxRate) {
        this.targetCountryTaxRate = targetCountryTaxRate;
    }

    public int getSourceCountryId() {
        return sourceCountryId;
    }

    public void setSourceCountryId(int sourceCountryId) {
        this.sourceCountryId = sourceCountryId;
    }

    public int getTargetCountryId() {
        return targetCountryId;
    }

    public void setTargetCountryId(int targetCountryId) {
        this.targetCountryId = targetCountryId;
    }

    public double getExchangeRateSpread() {
        return exchangeRateSpread;
    }

    public void setExchangeRateSpread(double exchangeRateSpread) {
        this.exchangeRateSpread = exchangeRateSpread;
    }

    public double getSourceTransactionFee() {
        return sourceTransactionFee;
    }

    public void setSourceTransactionFee(double sourceTransactionFee) {
        this.sourceTransactionFee = sourceTransactionFee;
    }

    public double getTargetTransactionFee() {
        return targetTransactionFee;
    }

    public void setTargetTransactionFee(double targetTransactionFee) {
        this.targetTransactionFee = targetTransactionFee;
    }

    public double getAgentDiscount() {
        return agentDiscount;
    }

    public void setAgentDiscount(double agentDiscount) {
        this.agentDiscount = agentDiscount;
    }

    public Corridor() {

    }


}
