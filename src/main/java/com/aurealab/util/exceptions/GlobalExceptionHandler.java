package com.aurealab.util.exceptions;

import com.aurealab.util.constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Custom exception handling
     * @param ex with all params
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now().toString(),
                ex.toString()
        );
        return new ResponseEntity<>(response, ex.getHttpStatus()    );
    }

    /**
     * Standar exception handling
     * @param ex with all params
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                false,
                constants.errors.handlerException,
                constants.errors.internalServerError,
                LocalDateTime.now().toString(),
                ex.toString()
        );
        log.error(constants.errors.unespectedError, ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Routes not found
     * @param ex NoHandlerFound exception
     * @return response error details
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                false,
                constants.errors.routeNotFound,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now().toString(),
                ex.toString()
        );
        log.error(constants.errors.routeNotFound, ex);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Response constructor class
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private boolean state;
        private String message;
        private String error;
        private String timestamp;
        private String trace;
    }
}
