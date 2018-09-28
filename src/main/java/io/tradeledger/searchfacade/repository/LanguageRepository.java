package io.tradeledger.searchfacade.repository;

import io.tradeledger.searchfacade.model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LanguageRepository extends MongoRepository<Language, String>, LanguageSearchRepository {

}
