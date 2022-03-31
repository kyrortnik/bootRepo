package com.epam.esm.mapper;

import com.epam.esm.controller.GiftCertificateController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
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

        LinkedHashMap<String, Boolean> parametersPairs = new LinkedHashMap<>();

        for (String sortingPair : sortingRequestParam) {
            String orderDirString = sortingPair.split("\\(")[0];
            boolean orderDir = orderDirString.equals("asc");
            String orderParam = StringUtils.substringBetween(sortingPair, "(", ")");
            parametersPairs.put(orderParam, orderDir);
        }
        LOGGER.debug("Exiting RequestMapper.mapSortingParams()");
        return parametersPairs;
    }

    public static Sort mapParams(List<String> sortingParams) {
        String parameter;
        Direction direction;
        List<Order> orders = new ArrayList<>();
        if (sortingParams.size() == 1) {
            parameter = sortingParams.get(0).split("\\.")[0];
            direction = getSortDirection(sortingParams.get(0).split("\\.")[1]);
            orders.add(new Order(direction, parameter));
        } else {
            for (String param : sortingParams) {
                parameter = param.split("\\.")[0];
                direction = getSortDirection(param.split("\\.")[1]);
                orders.add(new Order(direction, parameter));
            }
        }

        return Sort.by(orders);

    }

    private static Direction getSortDirection(String direction) {

        return direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    }
}
