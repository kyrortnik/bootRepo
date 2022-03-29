package com.epam.esm.mapper;

import com.epam.esm.controller.GiftCertificateController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RequestParamsMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateController.class);

    //TODO -- check on null throw here
    /**
     * @param sortingRequestParam String containing sorting order and parameter. Request sorting parameter example: asc(email)
     * @return HashMap with key/value pairs
     */
    public static LinkedHashMap<String, Boolean> mapSortingParams(List<String> sortingRequestParam) {
        LOGGER.info("Entering RequestMapper.mapSortingParams()");
        LinkedHashMap<String, Boolean> parametersPairs = new LinkedHashMap<>();

        for (String sortingPair : sortingRequestParam) {
            String orderDirString = sortingPair.split("\\(")[0];
            boolean orderDir = orderDirString.equals("asc");
            String orderParam = StringUtils.substringBetween(sortingPair, "(", ")");
            parametersPairs.put(orderParam, orderDir);
        }
        LOGGER.info("Exiting RequestMapper.mapSortingParams()");
        return parametersPairs;
    }
}
