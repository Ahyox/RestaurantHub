package com.ahyoxsoft.restauranthub.utility;

/**
 * Created by dejiogunnubi on 4/28/16.
 */
public class ServerResponse {
    private boolean response;
    private String responseMessage;
    private int merchantTransactionID;
    private int userTransactionID;

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public int getMerchantTransactionID() {
        return merchantTransactionID;
    }

    public void setMerchantTransactionID(int merchantTransactionID) {
        this.merchantTransactionID = merchantTransactionID;
    }

    public int getUserTransactionID() {
        return userTransactionID;
    }

    public void setUserTransactionID(int userTransactionID) {
        this.userTransactionID = userTransactionID;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
