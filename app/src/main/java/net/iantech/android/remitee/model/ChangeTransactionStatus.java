package net.iantech.android.remitee.model;


/*
  Created by Lucas on 7/29/2016.
*/
public class ChangeTransactionStatus {
    private int transactionId;
    private int status;

    public ChangeTransactionStatus(int _transactionId, int _status) {
        transactionId = _transactionId;
        status = _status;
    }
}




