package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.ContasAPagarResumoDTO;
import com.sandrojam.controlevendas.dto.ContasAReceberResumoDTO;
import com.sandrojam.controlevendas.dto.DashboardVendasDTO;
import com.sandrojam.controlevendas.dto.FluxoCaixaDTO;
import com.sandrojam.controlevendas.dto.RelatorioFinanceiroDTO;
import com.sandrojam.controlevendas.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Relatórios agregados: dashboard de vendas e financeiro (contas a pagar/receber + fluxo de
 * caixa). inicio/fim são opcionais em todos os endpoints — sem eles, considera os últimos 30 dias.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas")
    public DashboardVendasDTO dashboardVendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarDashboardVendas(inicio, fim);
    }

    /** Os três blocos financeiros de uma vez — usado pela tela de Financeiro. */
    @GetMapping("/financeiro")
    public RelatorioFinanceiroDTO financeiro(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarRelatorioFinanceiro(inicio, fim);
    }

    @GetMapping("/financeiro/contas-a-pagar")
    public ContasAPagarResumoDTO contasAPagar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarContasAPagar(inicio, fim);
    }

    @GetMapping("/financeiro/contas-a-receber")
    public ContasAReceberResumoDTO contasAReceber(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarContasAReceber(inicio, fim);
    }

    @GetMapping("/financeiro/fluxo-de-caixa")
    public FluxoCaixaDTO fluxoDeCaixa(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarFluxoCaixa(inicio, fim);
    }
}
