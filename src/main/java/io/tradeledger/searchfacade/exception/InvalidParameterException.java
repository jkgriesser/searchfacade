package io.tradeledger.searchfacade.exception;

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String params) {
        super("Cannot parse parameters: " + params);
    }

}
