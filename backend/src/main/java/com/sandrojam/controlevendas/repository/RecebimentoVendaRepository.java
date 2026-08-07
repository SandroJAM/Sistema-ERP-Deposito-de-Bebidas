package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.RecebimentoVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecebimentoVendaRepository extends JpaRepository<RecebimentoVenda, Long> {

    List<RecebimentoVenda> findByVendaIdOrderByDataRecebimentoAsc(Long vendaId);

    List<RecebimentoVenda> findByVenda_Cliente_IdOrderByDataRecebimentoAsc(Long clienteId);

    /** Recebimentos de fiado dentro do período — uma das origens de entrada do fluxo de caixa. */
    List<RecebimentoVenda> findByDataRecebimentoBetween(LocalDate inicio, LocalDate fim);
}
