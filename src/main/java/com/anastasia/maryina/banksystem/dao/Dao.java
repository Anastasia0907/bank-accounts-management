package com.anastasia.maryina.banksystem.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Dao<K, V> {

    Optional<V> findById(K id);

    List<V> findAll();

    V save(V entity);

    void update(V entity);

    void delete(UUID id);
}
