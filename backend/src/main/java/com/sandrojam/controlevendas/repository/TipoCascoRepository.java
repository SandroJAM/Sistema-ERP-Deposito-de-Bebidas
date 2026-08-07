package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.TipoCasco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoCascoRepository extends JpaRepository<TipoCasco, Long> {
    List<TipoCasco> findByAtivoTrue();
}
