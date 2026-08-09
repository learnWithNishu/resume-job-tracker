package com.resume.job.tracker.exceptions;

public class UnauthorizedAccessException  extends  RuntimeException {

    public UnauthorizedAccessException(String message){
        super(message);
    }
}
