package com.example.Repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T save(T entity);

    void update(T entity);

    void delete(ID id);

    Optional<T> findById(ID id);

    List<T> findAll();
}