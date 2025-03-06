package com.example.termitewallet.ResponseServices;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class ApiResponseBalance {

    @SerializedName("data")
    private String balance;

    public String getBalance() {
        return balance;
    }

}
