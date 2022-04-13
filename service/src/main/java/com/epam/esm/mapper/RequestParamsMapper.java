package com.epam.esm.mapper;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestParamsMapper {

    private static final String SPLIT = "\\.";
    private static final String DESC = "desc";


    public Sort mapParams(List<String> sortingParams) throws ArrayIndexOutOfBoundsException {
        try {
            String parameter;
            Direction direction;
            List<Order> orders = new ArrayList<>();
            for (String param : sortingParams) {
                parameter = param.split(SPLIT)[0];
                direction = getSortDirection(param.split(SPLIT)[1]);
                orders.add(new Order(direction, parameter));
            }
            return Sort.by(orders);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(" Incorrect sort_by pattern. Example : sort_by=id.desc");
        }
    }

    private Direction getSortDirection(String direction) {

        return direction.equals(DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}
