package com.example.Repository;

import java.util.List;

public interface CrudRepository<T, ID> {
    void save(T entity);

    void delete(ID id);

    T findById(ID id);

    List<T> findAll();
}