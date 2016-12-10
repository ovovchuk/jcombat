package com.workshop.advice;

import com.workshop.exception.AccountNotFoundException;
import com.workshop.exception.SessionNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public VndError propertyReferenceHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public VndError constraintValidationHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndError accountNotFoundHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndError sessionNotFoundHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public VndError dataIntegrityViolationHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public VndError illegalArgumentHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }
}
