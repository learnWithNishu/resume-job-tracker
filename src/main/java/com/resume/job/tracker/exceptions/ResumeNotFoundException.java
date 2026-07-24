package com.resume.job.tracker.exceptions;

public class ResumeNotFoundException extends RuntimeException{

    public ResumeNotFoundException(String message){
        super(message);
    }
}
