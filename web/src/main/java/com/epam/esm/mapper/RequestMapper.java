package com.epam.esm.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import java.util.Set;

@Component
public class RequestMapper {

    /**
     * @param sortingRequestParam String containing sorting order and parameter. Request sorting parameter example: asc(email)
     * @return HashMap with key/value pairs
     */
    public static HashMap<String, Boolean> mapSortingParams(Set<String> sortingRequestParam) {
        HashMap<String, Boolean> parametersPairs = new HashMap<>();

        for (String sortingPair : sortingRequestParam) {
            String orderDirString = sortingPair.split("\\(")[0];
            boolean orderDir = orderDirString.equals("asc");
            String orderParam = StringUtils.substringBetween(sortingPair, "(", ")");
            parametersPairs.put(orderParam, orderDir);
        }

        return parametersPairs;

    }
}
