package com.app.customexception;

public class FuelTypeNotFound extends Exception{
    public String message;

    public FuelTypeNotFound(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }
}
