package com.farmaline.farmaline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Registro;

public interface RegistroRepository extends JpaRepository<Registro, Integer> {
}