package com.tutorial.ecommerce.exception;

public class InvalidCredentialsException extends RuntimeException {

        public InvalidCredentialsException(String message) {
            super(message);
        }
}
