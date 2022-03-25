package com.epam.esm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CRUDService<E> {

    Optional<E> getById(Long id);

    List<E> getAll(HashMap<String,Boolean> sortParams, int max, int offset);

    boolean delete(Long id);

    boolean update(E element, Long id);

    Optional<E> create(E element);

}
