package com.aurealab.util.exceptions;

import com.aurealab.util.constants;

public class TokenException extends BaseException {
    public TokenException(String message, Throwable cause) {
        super(message, constants.errors.tokenCreationError, cause);
    }
}