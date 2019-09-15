package com.app.customexception;

public class InValidPrice extends Exception{
    public String message;

    public InValidPrice(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage() {
        return message;
    }
    }