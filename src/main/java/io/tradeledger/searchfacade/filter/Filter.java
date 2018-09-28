package io.tradeledger.searchfacade.filter;

import org.springframework.data.mongodb.core.query.Criteria;

public interface Filter {

    Criteria getCriteria();

}
