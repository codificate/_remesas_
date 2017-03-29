package net.iantech.android.remitee.model;

import net.iantech.android.remitee.R;

import java.util.Calendar;

/*
  Created by Lucas on 7/29/2016.
*/
public class PartyPaymentMethod extends com.orm.SugarRecord {

    private long partyId;
    private int bankId;
    private String bankName = "";
    private String bankAccountNumber = "";
    private int bankAccountType = 1; //Por default Caja de Ahorro
    private int paymentMethod = 2; //Por default es banco
    private String bankTIN = "";
    private int localCreatedDate;

    public int getLocalCreatedDate() {
        return localCreatedDate;
    }

    public void setLocalCreatedDate(int localCreatedDate) {
        this.localCreatedDate = localCreatedDate;
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public int getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(int bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBankTIN() {
        return bankTIN;
    }

    public void setBankTIN(String bankTIN) {
        this.bankTIN = bankTIN;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public PartyPaymentMethod() {
        Calendar c = Calendar.getInstance();
        localCreatedDate = c.get(Calendar.SECOND);
    }

    public PartyPaymentMethod(long _partyId) {
        localCreatedDate = 0;

        partyId = _partyId;
    }

    public String getBankAccountNumberWithX() {
        if (bankAccountNumber.isEmpty() || bankAccountNumber.length() < 3 || bankName.isEmpty())
            return "";

        String accountNumberx = "";
        for (int i = 0; i < bankAccountNumber.length() - 3; i++)
            accountNumberx += "X";

        return String.format("%s%s", accountNumberx, bankAccountNumber.substring(bankAccountNumber.length() - 3, bankAccountNumber.length()));
    }
}
