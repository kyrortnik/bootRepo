package com.epam.esm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class BaseRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRepository.class);

    public String addParamsToQuery(HashMap<String, Boolean> sortingParams, String query, String tableAlias) {
        LOGGER.info("Entering BaseRepository.addParamsToQuery()");

        Set<Map.Entry<String, Boolean>> paramsPairs = sortingParams.entrySet();
        StringBuilder originalQuery = new StringBuilder(query);
        String comma = ", ";
        for (Map.Entry<String, Boolean> paramPair : paramsPairs) {
            originalQuery.append(tableAlias);
            originalQuery.append(paramPair.getKey());
            originalQuery.append(paramPair.getValue() ? " ASC" : " DESC");
            originalQuery.append(comma);
        }
        LOGGER.info("Exiting BaseRepository.addParamsToQuery()");
        return originalQuery.substring(0, originalQuery.length() - 2);
    }
}
