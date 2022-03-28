package com.epam.esm.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RequestMapper {


    /**
     * @param sortingRequestParam String containing sorting order and parameter. Request sorting parameter example: asc(email)
     * @return HashMap with key/value pairs
     */
    public static LinkedHashMap<String, Boolean> mapSortingParams(List<String> sortingRequestParam) {
        LinkedHashMap<String, Boolean> parametersPairs = new LinkedHashMap<>();

        for (String sortingPair : sortingRequestParam) {
            String orderDirString = sortingPair.split("\\(")[0];
            boolean orderDir = orderDirString.equals("asc");
            String orderParam = StringUtils.substringBetween(sortingPair, "(", ")");
            parametersPairs.put(orderParam, orderDir);
        }

        return parametersPairs;

    }
}
