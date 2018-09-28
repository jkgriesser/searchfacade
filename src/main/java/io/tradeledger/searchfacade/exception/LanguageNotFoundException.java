package io.tradeledger.searchfacade.exception;

public class LanguageNotFoundException extends RuntimeException {

    public LanguageNotFoundException(String id) {
        super("Could not find language with ID #" + id);
    }

}
