package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}
