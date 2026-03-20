package com.termite.wallet.ResponseServices;

import com.termite.wallet.CryptoUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponseTransactionsByAddress {

    @SerializedName("data")
    private List<CryptoUtils.TransactionModel> transactionModels;
    private String message;
    public List<CryptoUtils.TransactionModel> getTransactions() {
        return transactionModels;
    }
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }
    public String getMessage(){
        return this.message;
    }
}
