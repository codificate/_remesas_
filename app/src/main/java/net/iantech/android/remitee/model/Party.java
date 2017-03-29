package net.iantech.android.remitee.model;

import java.io.Serializable;
import java.util.Calendar;

/*
  Created by Lucas on 7/29/2016.
*/
public class Party extends com.orm.SugarRecord {

    private String contactId; //El Id de la agenda de contactos
    private String firstName;

    private String lastName;
    private String email;
    private String phoneNumber;

    private String location;
    private String carrier;
    private String lineType;

    private String countryCode;

    private String sender_Document_Number;
    private int documentTypeId;
    private long senderId;

    private int localCreatedDate;

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setPhoneCountryCode(int phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    private int phoneCountryCode;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    private String documentNumber;

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSender_Document_Number() {
        return sender_Document_Number;
    }

    public void setSender_Document_Number(String sender_Document_Number) {
        this.sender_Document_Number = sender_Document_Number;
    }

    public Party() {
        Calendar c = Calendar.getInstance();
        localCreatedDate = c.get(Calendar.SECOND);
    }

    public Party(String _contactId, String _firstName, String _lastName, String _phoneNumber, int _phoneCountryCode, String _countryCode, String _documentNumber) {
        contactId = _contactId;
        firstName = _firstName;
        lastName = _lastName;
        phoneNumber = _phoneNumber;
        phoneCountryCode = _phoneCountryCode;
        countryCode = _countryCode;
        documentNumber = _documentNumber;

        Calendar c = Calendar.getInstance();
        localCreatedDate = c.get(Calendar.SECOND);
    }

    public void setContactId(String _contactId) {
        contactId = _contactId;
    }

    public void setFirstName(String _firstName) {
        firstName = _firstName;
    }

    public void setEmail(String _email) {
        contactId = _email;
    }

    public void setPhoneNumber(String _phoneNumber) {
        phoneNumber = _phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public String getContactId() {
        return contactId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public String getDocumentNumber() {
        if (documentNumber == null)
            return "";

        return documentNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
