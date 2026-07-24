package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
