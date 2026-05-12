package com.SneakySolo.SecureWatch.Exception;

public class EntityAlreadyBlockedException extends  RuntimeException{
    public EntityAlreadyBlockedException(String message) {
        super(message);
    }
}
