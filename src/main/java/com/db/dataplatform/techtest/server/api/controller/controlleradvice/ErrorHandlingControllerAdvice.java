package com.db.dataplatform.techtest.server.api.controller.controlleradvice;

import com.db.dataplatform.techtest.server.api.model.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.db.dataplatform.techtest.server.Constants.INVALID_BLOCK_TYPE;
import static com.db.dataplatform.techtest.server.Constants.VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConversionFailedException.class)
    public String handleConflict(RuntimeException ex) {
        return INVALID_BLOCK_TYPE;
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private ErrorResponse processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST.value(), VALIDATION_ERROR);
        for (org.springframework.validation.FieldError fieldError: fieldErrors) {
            errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errorResponse;
    }

}