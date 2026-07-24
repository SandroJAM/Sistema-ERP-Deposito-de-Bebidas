package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepository extends JpaRepository<Venda, Long> {
}
