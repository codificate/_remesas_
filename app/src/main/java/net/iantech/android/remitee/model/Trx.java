package net.iantech.android.remitee.model;

import com.orm.dsl.Ignore;

import java.util.Calendar;
import java.util.Date;

/*
  Created by Lucas on 7/29/2016.
  Renombre el objeto a Trx porque Transaction es un nombre reservado de SQLLite
*/
public class Trx extends com.orm.SugarRecord {

    private int remoteId;

    private int sourceCountryId;
    private String sourceCountryCode;
    private String sourceCountryCurrencyCode;

    private int targetCountryId;
    private String targetCountryCode;
    String targetCountryCurrencyCode;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    private long senderId;
    private int senderAgentId;
    private String senderFirstName;
    private String senderLastName;
    private String senderPhoneNumber;
    private int senderPhoneCountryCode;
    private String senderDocumentNumber;

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    private String senderLocation;

    public String getSenderCarrier() {
        return senderCarrier;
    }

    public void setSenderCarrier(String senderCarrier) {
        this.senderCarrier = senderCarrier;
    }

    private String senderCarrier;

    public String getSenderLineType() {
        return senderLineType;
    }

    public void setSenderLineType(String senderLineType) {
        this.senderLineType = senderLineType;
    }

    private String senderLineType;

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    private long recipientId;
    private int recipientAgentId;
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientPhoneNumber;
    private int recipientPhoneCountryCode;
    private String recipientDocumentNumber;

    private int recipientBankId;
    private String recipientBankAccountNumber;
    private int recipientBankAccountType;

    public int getRecipientBankId() {
        return recipientBankId;
    }

    public void setRecipientBankId(int recipientBankId) {
        this.recipientBankId = recipientBankId;
    }

    public String getRecipientBankAccountNumber() {
        return recipientBankAccountNumber;
    }

    public void setRecipientBankAccountNumber(String recipientBankAccountNumber) {
        this.recipientBankAccountNumber = recipientBankAccountNumber;
    }

    public int getRecipientBankAccountType() {
        return recipientBankAccountType;
    }

    public void setRecipientBankAccountType(int recipientBankAccountType) {
        this.recipientBankAccountType = recipientBankAccountType;
    }

    public String getRecipientBankTin() {
        return recipientBankTin;
    }

    public void setRecipientBankTin(String recipientBankTin) {
        this.recipientBankTin = recipientBankTin;
    }

    private String recipientBankTin;

    public String getRecipientLocation() {
        return recipientLocation;
    }

    public void setRecipientLocation(String recipientLocation) {
        this.recipientLocation = recipientLocation;
    }

    private String recipientLocation;

    public String getRecipientCarrier() {
        return recipientCarrier;
    }

    public void setRecipientCarrier(String recipientCarrier) {
        this.recipientCarrier = recipientCarrier;
    }

    private String recipientCarrier;

    public String getRecipientLineType() {
        return recipientLineType;
    }

    public void setRecipientLineType(String recipientLineType) {
        this.recipientLineType = recipientLineType;
    }

    private String recipientLineType;

    private double amount;

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

    public double getSourceTaxRate() {
        return sourceTaxRate;
    }

    public void setSourceTaxRate(double sourceTaxRate) {
        this.sourceTaxRate = sourceTaxRate;
    }

    public double getTargetTaxRate() {
        return targetTaxRate;
    }

    public void setTargetTaxRate(double targetTaxRate) {
        this.targetTaxRate = targetTaxRate;
    }

    private double sourceTransactionFee;
    private double targetTransactionFee;
    private double agentDiscount;
    private double sourceTaxRate;
    private double targetTaxRate;
    private double recipientsAmount;

    public double getExchangeRate() {
        return exchangeRate;
    }

    private double exchangeRate;
    private double exchangeRateSpread;

    public double getExchangeRateToUSD() {
        return exchangeRateToUSD;
    }

    public void setExchangeRateToUSD(double exchangeRateToUSD) {
        this.exchangeRateToUSD = exchangeRateToUSD;
    }

    private double exchangeRateToUSD;
    private boolean confirmed;
    boolean payed;
    private double minAmountInUSD = 15; //TODO: esta propiedad deberia estar asociada al pais?
    private double maxAmountInUSD = 1000; //TODO: esta propiedad deberia estar asociada al pais?

    private int localCreatedDate;

    private String trackingNumber;

    //TODO: tanto el payment como el collection van a depender de los medios de pagos o cobro habilitado para cada país.
    private int paymentMethod = 1; //1 = Cash, 2 = Banco, 3 = Tarjeta

    public int getCollectionMethod() {
        return collectionMethod;
    }

    public void setCollectionMethod(int collectionMethod) {
        this.collectionMethod = collectionMethod;
    }

    private int collectionMethod = 1; //1 = Cash, 2 = Banco, 3 = Tarjeta

    private int userId;
    private String accountId;
    private String accountKitPhoneNumber;

    public Trx() {
        Calendar c = Calendar.getInstance();
        localCreatedDate = c.get(Calendar.SECOND);
    }

    public void setUserId(int _userId) {
        userId = _userId;
    }

    public void setSenderAgentId(int _senderAgentId) {
        senderAgentId = _senderAgentId;
    }

    public void setRecipientAgentId(int _recipientAgentId) {
        recipientAgentId = _recipientAgentId;
    }

    public void setAccountId(String _accountId) {
        accountId = _accountId;
    }

    public void setAccountKitPhoneNumber(String _accountKitPhoneNumber) {
        accountKitPhoneNumber = _accountKitPhoneNumber;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int _paymentMethod) {
        paymentMethod = _paymentMethod;
    }

    public void setTargetCountryId(int _targetCountryId) {
        targetCountryId = _targetCountryId;
    }

    public void setAmount(double _amount) {
        amount = _amount;
    }

    public double getAmount() {
        return amount;
    }

    public double getRecipientsAmount() {
        return recipientsAmount;
    }

    public double calculateAmount(double _recipientsAmount, double _exchangeRate)
    {
        //TODO: en este calculo faltan los impuestos o fees del lado recibidor?
        if (_exchangeRate == 0)
            _exchangeRate = exchangeRate;

        double netFee = sourceTransactionFee; // * (1 - agentDiscount); //el descuento se lo hacemos despues
        double netInReceivingCountry = 1 / (1 + (netFee * (1 + sourceTaxRate)));

        return _recipientsAmount / netInReceivingCountry / (_exchangeRate * (1 - exchangeRateSpread));
    }

    public double calculateRecipientsAmount(double _amount, double _exchangeRate, boolean updateRecipientsAmount)
    {
        //TODO: en este calculo faltan los impuestos o fees del lado recibidor?
        if (_amount == 0)
            _amount = amount;

        if (_exchangeRate == 0)
            _exchangeRate = exchangeRate;

        double netFee = sourceTransactionFee; // * (1 - agentDiscount); //el descuento se lo hacemos despues.
        double netInReceivingCountry = 1 / (1 + (netFee * (1 + sourceTaxRate)));
        double feeToCustomer = 1 - netInReceivingCountry;
        double feeToRemitee =  feeToCustomer / (1 + sourceTaxRate);
        double revenue = _amount * feeToRemitee;
        double tax = revenue * sourceTaxRate;


        double _recipientsAmount = (_amount - revenue - tax) * (_exchangeRate * (1 - exchangeRateSpread));
        if (updateRecipientsAmount)
        {
            recipientsAmount = _recipientsAmount;
        }
        return _recipientsAmount;
    }

    public void setRemoteId(int _remoteId) {
        remoteId = _remoteId;
    }

    public void setConfirmed(boolean _confirmed) {
        confirmed = _confirmed;
    }

    public void setExchangeRate(double _exchangeRate) {
        exchangeRate = _exchangeRate;
    }

    public double getExchangeRateSpread() {
        return exchangeRateSpread;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public double getMinAmountInUSD() {
        return minAmountInUSD;
    }

    public void setSourceCountryId(int _sourceCountryId) {
        sourceCountryId = _sourceCountryId;
    }

    public int getSourceCountryId() {
        return sourceCountryId;
    }

    public int getTargetCountryId() {
        return targetCountryId;
    }

    public void setSourceCountryCode(String _sourceCountryCode) {
        sourceCountryCode = _sourceCountryCode;
    }

    public void setTargetCountryCode(String _targetCountryCode) {
        targetCountryCode = _targetCountryCode;
    }

    public void setExchangeRateSpread(double _exchangeRateSpread) {
        exchangeRateSpread = _exchangeRateSpread;
    }

    public void setRecipientsAmount(double _recipientsAmount) {
        recipientsAmount = _recipientsAmount;
    }

    public String getSenderName() {
        return senderFirstName + " " + senderLastName;
    }

    public String getRecipientName() {
        return recipientFirstName + " " + recipientLastName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public int getRecipientPhoneCountryCode() {
        return recipientPhoneCountryCode;
    }

    public String getRecipientDocumentNumber() {
        return recipientDocumentNumber;
    }

    public String getRecipientFirstName() {
        return recipientFirstName;
    }

    public String getRecipientLastName() {
        return recipientLastName;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public int getSenderPhoneCountryCode() {
        return senderPhoneCountryCode;
    }

    public String getSenderDocumentNumber() {
        return senderDocumentNumber;
    }


    public String getSourceCountryCode() {
        return sourceCountryCode;
    }

    public String getTargetCountryCode() {
        return targetCountryCode;
    }

    public void setTrackingNumber(String _trackingNumber) {
        trackingNumber = _trackingNumber;
    }

    public void setSenderFirstName(String _senderFirstName) {
        senderFirstName = _senderFirstName;
    }

    public void setSenderLastName(String _senderLastName) {
        senderLastName = _senderLastName;
    }

    public void setSenderPhoneNumber(String _senderPhoneNumber) {
        senderPhoneNumber = _senderPhoneNumber;
    }

    public void setSenderPhoneCountryCode(int _senderPhoneCountryCode) {
        senderPhoneCountryCode = _senderPhoneCountryCode;
    }

    public void setSenderDocumentNumber(String _senderDocumentNumber) {
        senderDocumentNumber = _senderDocumentNumber;
    }


    public void setRecipientFirstName(String _recipientFirstName) {
        recipientFirstName = _recipientFirstName;
    }

    public void setRecipientLastName(String _recipientLastName) {
        recipientLastName = _recipientLastName;
    }

    public void setRecipientPhoneNumber(String _recipientPhoneNumber) {
        recipientPhoneNumber = _recipientPhoneNumber;
    }

    public void setRecipientPhoneCountryCode(int _recipientPhoneCountryCode) {
        recipientPhoneCountryCode = _recipientPhoneCountryCode;
    }

    public void setRecipientDocumentNumber(String _recipientDocumentNumber) {
        recipientDocumentNumber = _recipientDocumentNumber;
    }
}




