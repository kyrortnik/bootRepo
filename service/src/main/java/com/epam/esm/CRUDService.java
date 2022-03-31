package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CRUDService<E> {

    Optional<E> getById(Long id);

//    List<E> getAll(HashMap<String,Boolean> sortParams, int max, int offset);

    Page<E> getAll(Sort sortParams, int max, int offset);

    boolean delete(Long id);

    void update(E element, Long id);

    E create(E element);

}
