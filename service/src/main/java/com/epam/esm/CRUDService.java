package com.epam.esm;

import java.util.List;
import java.util.Optional;

public interface CRUDService<E> {

    Optional<E> getById(Long id);

    List<E> getAll(String order, int max);

    boolean delete(Long id);

    boolean update(E element, Long id);

    Optional<E> create(E element);

}
