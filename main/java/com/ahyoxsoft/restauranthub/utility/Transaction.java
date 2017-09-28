package com.ahyoxsoft.restauranthub.utility;

import java.util.List;

/**
 * Created by dejiogunnubi on 1/19/16.
 */
public class Transaction {
    private int merchantID;
    private int userID;
    private String merchant;
    private String foodName;
    private double foodPrice;
    private String foodDecription;
    private String transactionID;
    private int status;
    private List<Transaction> transactions;

    public int getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(int merchantID) {
        this.merchantID = merchantID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }


    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDecription() {
        return foodDecription;
    }

    public void setFoodDecription(String foodDecription) {
        this.foodDecription = foodDecription;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
