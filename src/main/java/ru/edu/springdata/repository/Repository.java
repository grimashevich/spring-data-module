package ru.edu.springdata.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    List<T> findAll();

    Optional<T> findById(K id);

    T save(T t);

    void update(T t);

    void delete(K id);

    long getRecordCount();
}
