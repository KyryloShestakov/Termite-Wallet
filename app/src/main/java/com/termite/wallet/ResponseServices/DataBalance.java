package com.termite.wallet.ResponseServices;

import com.google.gson.annotations.SerializedName;

public class DataBalance {
    @SerializedName("data")
    private String balance;

    public String getBalance() {
        return balance;
    }
}
