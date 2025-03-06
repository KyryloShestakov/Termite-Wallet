package com.example.termitewallet;

import android.os.Build;
import android.util.Log;

import java.math.BigDecimal;
import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CryptoUtils {

    public static RSAPrivateKey getPrivateKeyFromString(String privateKeyBase64) throws Exception {
        byte[] encoded = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(privateKeyBase64);
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    // Метод для подписания транзакции
    public static String signTransaction(TransactionModel transaction, String privateKey) throws Exception {
        if (transaction == null || privateKey == null) {
            throw new IllegalArgumentException("Transaction or Private Key is null");
        }

        // Преобразуем транзакцию в строку JSON
        Gson gson = new Gson();
        String transactionData = gson.toJson(new TransactionData(
                transaction.getId(),
                transaction.getSender(),
                transaction.getReceiver()
//                transaction.getAmount(),
//                transaction.getTimestamp(),
//                transaction.getFee(),
//                transaction.getData(),
//                transaction.getContract(),
//                transaction.getBlockId(),
//                transaction.getPublicKey()
        ));

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] transactionHash = digest.digest(transactionData.getBytes("UTF-8"));
        Log.d("CryptoUtils", "Transaction hash:" + bytesToHex(transactionHash));
        // Получаем RSA приватный ключ
        RSAPrivateKey rsaPrivateKey = getPrivateKeyFromString(privateKey);

        // Подписываем хэш с использованием RSA и SHA-256
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(rsaPrivateKey);
        signature.update(transactionHash);
        byte[] signedData = signature.sign();

        // Возвращаем подпись в формате Base64
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(signedData);
        }
        return transactionData;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase(); // Возвращаем строку в верхнем регистре
    }

    public static class TransactionData {
        private String id;
        private String sender;
        private String receiver;
        private BigDecimal amount;
        private String timestamp;
        private BigDecimal fee;
        private String data;
        private String contract;
        private String publicKey;
        private String signature;
        private String blockId;


        public TransactionData(String id,String sender,String receiver, BigDecimal amount, String timestamp, BigDecimal fee, String data,String contract, String blockId, String publicKey) {
            this.id = id;
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
            this.timestamp = timestamp;
            this.fee = fee;
            this.data = data;
            this.contract = contract;
            this.blockId = blockId;
            this.publicKey = publicKey;
        }

       public TransactionData(String id, String sender, String receiver){
            this.id = id;
            this.sender = sender;
           this.receiver = receiver;
       }
       public TransactionData(){}
    }

    public static class TransactionModel {
        @SerializedName("Id")

        private String Id;
        @SerializedName("Sender")
        private String sender;
        @SerializedName("Receiver")
        private String receiver;
        @SerializedName("Amount")
        private BigDecimal amount;
        @SerializedName("Timestamp")
        private String timestamp;
        @SerializedName("Fee")
        private BigDecimal fee;
        @SerializedName("Data")
        private String data;
        @SerializedName("Contract")
        private String contract;
        @SerializedName("Signature")
        private String signature;
        @SerializedName("PublicKey")
        private String publicKey;
        @SerializedName("BlockId")
        private String blockId;

        @Override
        public String toString() {
            return "TransactionModel{" +
                    "id='" + Id + '\'' +
                    ", sender='" + sender + '\'' +
                    ", receiver='" + receiver + '\'' +
                    ", amount=" + amount +
                    ", timestamp='" + timestamp + '\'' +
                    ", fee=" + fee +
                    ", publicKey='" + publicKey + '\'' +
                    ", signature='" + signature + '\'' +
                    '}';
        }
        public String getSender() {
            return sender;
        }

        public String getBlockId() {
            return blockId;
        }

        public void setBlockId(String blockId) {
            this.blockId = blockId;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String  getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(BigDecimal fee) {
            this.fee = fee;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setPublicKey(String publicKey){
            this.publicKey = publicKey;
        }

        public String getPublicKey(){
            return publicKey;
        }

        public void setId(String id){
            this.Id = id;
        }

        public String getId(){
            return this.Id;
        }
    }
}

