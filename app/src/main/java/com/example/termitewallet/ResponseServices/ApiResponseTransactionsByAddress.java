package com.example.termitewallet.ResponseServices;

import com.example.termitewallet.CryptoUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponseTransactionsByAddress {

    @SerializedName("data")
    private List<CryptoUtils.TransactionModel> transactionModels;
    private String message;
    // Геттер для получения списка транзакций
    public List<CryptoUtils.TransactionModel> getTransactions() {
        return transactionModels;
    }

    // Сеттер для установки списка транзакций
    public void setTransactions(List<CryptoUtils.TransactionModel> transactionModels) {
        this.transactionModels = transactionModels;
    }

    // Метод для сериализации в JSON
    public String toJson() {
        // Используем Gson для сериализации объекта
        return new com.google.gson.Gson().toJson(this);
    }

    // Метод для десериализации из JSON
    public static ApiResponseTransactionsByAddress fromJson(String json) {
        // Используем Gson для десериализации JSON в объект
        return new com.google.gson.Gson().fromJson(json, ApiResponseTransactionsByAddress.class);
    }

    public String getMessage(){
        return this.message;
    }
}
