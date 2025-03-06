package com.example.termitewallet.ResponseServices;

public class ApiResponseAddress {
    private String status;
    private String message;
    private Data data;
    private String errorCode;

    public ApiResponseAddress(String status, String message, Data data, String errorCode){
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public Data getData(){
        return this.data;
    }
}
