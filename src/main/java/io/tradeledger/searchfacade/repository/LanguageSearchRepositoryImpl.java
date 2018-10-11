package io.tradeledger.searchfacade.repository;

import io.tradeledger.searchfacade.filter.Filter;
import io.tradeledger.searchfacade.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.List;

public class LanguageSearchRepositoryImpl implements LanguageSearchRepository {

    private MongoOperations operations;

    public LanguageSearchRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public List<Language> search(Filter[] filters) {
        return operations.find(getQuery(filters), Language.class);
    }

}
