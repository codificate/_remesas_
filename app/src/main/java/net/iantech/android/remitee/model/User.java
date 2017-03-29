package net.iantech.android.remitee.model;

import java.util.Calendar;
import java.util.UUID;

/*
  Created by Lucas on 7/29/2016.
*/
public class User {
    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /* pais de origen del usuario */
    private int countryId;

    /* es el numero de telefono completo, con codigo de pais, ciudad, etc. (solo numeros) */
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    /* Datos de direccion */
    private String address;
    private String city;
    private String zipcode;
    private float latitude;
    private float longitude;

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    /* indica si fue deshabilitado por un administrador */
    private Boolean isDisabled;
    private Boolean isAgent;
    private String agentCode;

    public void setCanCollect(Boolean canCollect) {
        this.canCollect = canCollect;
    }

    /* indica si el usuario validó su número de teléfono */
    private Boolean canCollect;

    public void setCanPay(Boolean canPay) {
        this.canPay = canPay;
    }

    private Boolean canPay;

    /* url de la foto del usuario */
    private String pictureUrl;

    private Calendar birthDate;

    private int documentTypeId;
    private String documentNumber;
    /* url de la foto del DNI o documento que lo identifica */
    private String documentUrl;

    /* Datos relacionados al device del User */
    private String deviceId;
    private String deviceModel;
    private String deviceName;
    private String devicePhoneNumber;

    /* Datos de Facebook si hacemos Facebook Login */
    private String facebook_id;
    private String facebook_email;
    private String facebook_fullname;

    /* Datos de Facebook Account Kit */
    private String accountId;
    private String accountKitPhoneNumber;

    public boolean getHasCredit() {
        return hasCredit;
    }

    private boolean hasCredit;
    private double amount;

    public User() {

    }

    public User(String _accountId, String _accountKitPhoneNumber, String _agentCode, Boolean _isAgent, double _amount) {
        accountId = _accountId;
        accountKitPhoneNumber = _accountKitPhoneNumber;
        agentCode = _agentCode;
        isAgent = _isAgent;
        amount = _amount;
    }

    public User(String _deviceId, String _deviceModel, String _deviceName, String _devicePhoneNumber) {
        deviceId = _deviceId;
        deviceModel = _deviceModel;
        deviceName = _deviceName;
        devicePhoneNumber = _devicePhoneNumber;
    }

    public int getId() {
        return id;
    }

    public Boolean getIsDisabled()
    {
        return  isDisabled;
    }

    public Boolean getCanCollect()
    {
        return  canCollect;
    }

    public Boolean getCanPay()
    {
        return  canPay;
    }

    public String getAccountId()
    {
        return  accountId;
    }

    public String getAccountKitPhoneNumber()
    {
        return  accountKitPhoneNumber;
    }
}
