// User must not choose their own role, it should be set by the system
package com.example.welog.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
