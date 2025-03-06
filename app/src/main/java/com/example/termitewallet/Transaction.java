package com.example.termitewallet;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName("Id")
    private String id;

    @SerializedName("Sender")
    private String sender;

    @SerializedName("Receiver")
    private String receiver;

    @SerializedName("Amount")
    private double amount;

    @SerializedName("Timestamp")
    private String timestamp;

    @SerializedName("Fee")
    private double fee;

    @SerializedName("Signature")
    private String signature;

    @SerializedName("BlockId")
    private String blockId;

    @SerializedName("Data")
    private String data;

    @SerializedName("Contract")
    private String contract;

    @SerializedName("PublicKey")
    private String publicKey;

    public Transaction(String id, String sender, String receiver, double amount, String timestamp, double fee, String signature, String publicKey) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = timestamp;
        this.fee = fee;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

