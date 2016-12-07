package com.workshop.advice;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseBody
    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public VndError propertyReferenceHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndError resourceNotFoundHandler(Exception e) {
        return new VndError(e.getClass().getCanonicalName(), e.getLocalizedMessage());
    }
}
