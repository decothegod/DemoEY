package com.example.demo.ey.exception;

public class ServiceExceptionBadRequest extends RuntimeException {
    public ServiceExceptionBadRequest(String msg) {
        super(msg);
    }
}
