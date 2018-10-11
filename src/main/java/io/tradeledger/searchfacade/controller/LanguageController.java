package io.tradeledger.searchfacade.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tradeledger.searchfacade.exception.InvalidParameterException;
import io.tradeledger.searchfacade.exception.LanguageNotFoundException;
import io.tradeledger.searchfacade.model.Language;
import io.tradeledger.searchfacade.filter.LanguageFilter;
import io.tradeledger.searchfacade.repository.LanguageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class LanguageController {

    private final LanguageRepository repository;

    public LanguageController(LanguageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String getGreeting() {
        return "Hello World! Welcome to the Search Facade REST API.";
    }

    @GetMapping("/languages/{id}")
    public Language getLanguage(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new LanguageNotFoundException(id));
    }

    @GetMapping("/languages/search")
    public List<Language> searchLanguages(@RequestParam(value="filter") String filters) {
        try {
            // TODO Replace the conversion logic with a custom type converter
            //  Note: The deserialisation of multiple JSON filter parameters doesn't work without enclosing brackets.
            //  Hence they are added here.
            //  I was also unable to correctly coerce a single filter into an array or list using @RequestParam.
            //  Multiple filters work as expected, but a single parameter is still split up for some reason.
            filters = "[" + filters + "]";
            LanguageFilter languageFilters[] = new ObjectMapper()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .readValue(filters, LanguageFilter[].class);

            return repository.search(languageFilters);
        } catch (IOException e) {
            throw new InvalidParameterException(filters);
        }
    }

}
