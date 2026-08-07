package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.MovimentoCasco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentoCascoRepository extends JpaRepository<MovimentoCasco, Long> {

    List<MovimentoCasco> findByCliente_IdOrderByDataDesc(Long clienteId);

    List<MovimentoCasco> findAllByOrderByDataDesc();

    List<MovimentoCasco> findByCliente_IdAndTipoCasco_IdOrderByDataAsc(Long clienteId, Long tipoCascoId);
}
