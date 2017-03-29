package net.iantech.android.remitee.model;

import java.io.Serializable;

/*
  Created by Lucas on 7/29/2016.
*/
public class AuditTrail {
    String action; //El Id de la agenda de contactos
    String deviceId;
    String deviceModel;
    String deviceName;
    String devicePhoneNumber;
    String accountId;
    String userPhoneNumber;
    String status;

    public AuditTrail(String _action, String _deviceId, String _deviceModel, String _deviceName, String _devicePhoneNumber, String _accountId, String _userPhoneNumber, String _status) {
        action = _action;
        deviceId = _deviceId;
        deviceModel = _deviceModel;
        deviceName = _deviceName;
        devicePhoneNumber = _devicePhoneNumber;
        accountId = _accountId;
        userPhoneNumber = _userPhoneNumber;
        status = _status;
    }
}
