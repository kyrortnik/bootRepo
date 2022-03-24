package com.epam.esm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseRepository {

    public String addParamsToQuery(HashMap<String, Boolean> sortingParams, String query, String tableAlias) {
        Set<Map.Entry<String, Boolean>> paramsPairs = sortingParams.entrySet();
        StringBuilder originalQuery = new StringBuilder(query);
        String comma = ", ";

        for (Map.Entry<String, Boolean> paramPair : paramsPairs) {
            originalQuery.append(tableAlias);
            originalQuery.append(paramPair.getKey());
            originalQuery.append(paramPair.getValue() ? " ASC" : " DESC");
            originalQuery.append(comma);
        }
        return originalQuery.substring(0, originalQuery.length() - 2);

    }
}
