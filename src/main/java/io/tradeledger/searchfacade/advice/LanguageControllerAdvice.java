package io.tradeledger.searchfacade.advice;

import io.tradeledger.searchfacade.exception.InvalidParameterException;
import io.tradeledger.searchfacade.exception.LanguageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LanguageControllerAdvice {

    @ExceptionHandler(LanguageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String languageNotFoundHandler(LanguageNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidParameterHandler(InvalidParameterException ex) {
        return ex.getMessage();
    }

}
