package com.incidentplatform.common.Exception;

public class ApiException extends RuntimeException{
    public ApiException(String message){
        super(message);
    }
}
