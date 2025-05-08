package com.example.digiwallet.mapper;

public interface AbstractMapper <D, E> {

    D toDto(E entity);

    E toEntity(D dto);
}
