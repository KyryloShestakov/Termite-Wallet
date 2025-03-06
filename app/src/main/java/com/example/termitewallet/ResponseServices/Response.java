package com.example.termitewallet.ResponseServices;

public class Response {
    public String Message;
    public Boolean IsSuccesfull;

    public String GetMessage(){
        return this.Message;
    }

    public Boolean GetStatus(){
        return this.IsSuccesfull;
    }
}
