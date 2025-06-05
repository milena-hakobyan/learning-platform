package com.example.Repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    void save(T entity);

    void delete(ID id);

    Optional<T> findById(ID id);

    List<T> findAll();
}