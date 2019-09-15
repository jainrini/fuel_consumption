package com.app.customexception;

public class RecordNotFound extends Exception {
    public String message;

    public RecordNotFound(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }
}
