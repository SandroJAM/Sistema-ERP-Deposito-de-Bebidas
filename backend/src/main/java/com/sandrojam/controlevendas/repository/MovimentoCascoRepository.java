package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.MovimentoCasco;
import com.sandrojam.controlevendas.model.TipoMovimentoCasco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovimentoCascoRepository extends JpaRepository<MovimentoCasco, Long> {

    List<MovimentoCasco> findByCliente_IdOrderByDataDesc(Long clienteId);

    List<MovimentoCasco> findAllByOrderByDataDesc();

    List<MovimentoCasco> findByCliente_IdAndTipoCasco_IdOrderByDataAsc(Long clienteId, Long tipoCascoId);

    /** Reposições de casco pagas (cliente optou por pagar em vez de devolver) dentro do período — entrada extra do fluxo de caixa. */
    List<MovimentoCasco> findByTipoMovimentoAndDataBetween(TipoMovimentoCasco tipoMovimento, LocalDate inicio, LocalDate fim);
}
