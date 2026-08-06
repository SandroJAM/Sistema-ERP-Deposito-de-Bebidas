package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
