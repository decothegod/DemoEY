package com.example.demo.ey.exception;

public class ServiceExceptionUnauthorized extends RuntimeException {
    public ServiceExceptionUnauthorized(String msg) {
        super(msg);
    }
}
