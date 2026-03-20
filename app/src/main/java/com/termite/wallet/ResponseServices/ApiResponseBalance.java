package com.termite.wallet.ResponseServices;

import com.google.gson.annotations.SerializedName;

public class ApiResponseBalance {

    @SerializedName("data")
    private String balance;

    public String getBalance() {
        return balance;
    }

}
