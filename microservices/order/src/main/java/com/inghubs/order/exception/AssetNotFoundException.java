package com.inghubs.order.exception;

public class AssetNotFoundException extends RuntimeException{
    public AssetNotFoundException(String message) {
        super(message);
    }
}
