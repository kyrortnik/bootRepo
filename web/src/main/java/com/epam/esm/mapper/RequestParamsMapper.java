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

    private static final String  SPLIT = "\\.";


    public static Sort mapParams(List<String> sortingParams) {
        try{
            String parameter;
            Direction direction;
            List<Order> orders = new ArrayList<>();
            if (sortingParams.size() == 1) {
                parameter = sortingParams.get(0).split(SPLIT)[0];
                direction = getSortDirection(sortingParams.get(0).split(SPLIT)[1]);
                orders.add(new Order(direction, parameter));
            } else {
                for (String param : sortingParams) {
                    parameter = param.split(SPLIT)[0];
                    direction = getSortDirection(param.split(SPLIT)[1]);
                    orders.add(new Order(direction, parameter));
                }
            }
            return Sort.by(orders);
        }catch (ArrayIndexOutOfBoundsException e){
            throw  new ArrayIndexOutOfBoundsException(" Incorrect sort_by pattern. Exmaple : sort_by=id.desc");
        }


    }

    private static Direction getSortDirection(String direction) {

        return direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}
