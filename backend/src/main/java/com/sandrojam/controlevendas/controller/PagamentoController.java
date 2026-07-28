package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.PagamentoDTO;
import com.sandrojam.controlevendas.model.StatusPagamento;
import com.sandrojam.controlevendas.service.PagamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    /**
     * Lista pagamentos. Todos os filtros são opcionais — sem eles, retorna todos os pagamentos.
     * notaEntradaId tem prioridade (usado pela tela de Notas de Entrada); os demais filtros
     * (fornecedor, status, faixa de vencimento) são combináveis entre si.
     */
    @GetMapping
    public List<PagamentoDTO> listar(
            @RequestParam(required = false) Long notaEntradaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) StatusPagamento status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoDe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoAte) {
        if (notaEntradaId != null) {
            return pagamentoService.listarPorNota(notaEntradaId);
        }
        return pagamentoService.listarComFiltros(fornecedorId, status, vencimentoDe, vencimentoAte);
    }

    @GetMapping("/{id}")
    public PagamentoDTO buscarPorId(@PathVariable Long id) {
        return pagamentoService.buscarPorId(id);
    }

    @PostMapping("/{id}/marcar-pago")
    public PagamentoDTO marcarComoPago(@PathVariable Long id) {
        return pagamentoService.marcarComoPago(id);
    }
}
