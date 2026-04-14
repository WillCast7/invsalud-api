package com.aurealab.util.exceptions;

import com.aurealab.util.constants;

public class DataPersistenceException extends BaseException {
    public DataPersistenceException(String message) {
        super(message, constants.errors.dataPersistenceError);
    }
}
