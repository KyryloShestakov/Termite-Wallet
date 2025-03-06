package com.example.termitewallet.ResponseServices;

public class ApiResponseTransaction {
    private String status;
    private String message;
    private String errorCode;

    public ApiResponseTransaction(String status, String message, String errorCode){
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
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

}
