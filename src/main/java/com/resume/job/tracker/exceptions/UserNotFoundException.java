package com.resume.job.tracker.exceptions;

public class UserNotFoundException extends  RuntimeException {

    public UserNotFoundException(String message){
        super(message);
    }
}
