package io.tradeledger.searchfacade.repository;

import io.tradeledger.searchfacade.exception.InvalidParameterException;
import io.tradeledger.searchfacade.filter.Filter;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Allows searching the repository via filter request parameters.
  *
 * <p>The following format must be obeyed:
 * GET /resource/search?filter=filter1&filter=filter2&filter=filter3
 *
 * <p>The filters themselves are JSON objects.
 *
 * <p>Sample implementation:
 * {@link LanguageSearchRepositoryImpl}
 *
 */
public interface SearchRepository<T> {

    List<T> search(Filter[] filters);

    default Query getQuery(Filter[] filters) {
        Query query = new Query();

        for (Filter filter : filters) {
            try {
                query.addCriteria(filter.getCriteria());
            } catch (Exception ex) {
                // Duplicate attributes are not allowed
                throw new InvalidParameterException(ex.getMessage());
            }
        }

        return query;
    }

}
