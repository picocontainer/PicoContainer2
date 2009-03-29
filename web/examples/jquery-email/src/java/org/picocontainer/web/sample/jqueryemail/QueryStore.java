package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.Query;
import java.util.Map;
import java.util.HashMap;

public class QueryStore {

    private Map<String,Query> queries = new HashMap<String,Query>();

    public Query get(String key) {
        return queries.get(key);
    }

    public void put(String key, Query query) {
        queries.put(key, query);
    }
}
