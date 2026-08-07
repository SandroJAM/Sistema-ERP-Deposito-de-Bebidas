package com.sandrojam.controlevendas.dto;

/** Agrega os três blocos do relatório financeiro num único payload, pra a tela montar tudo com uma chamada. */
public class RelatorioFinanceiroDTO {

    private ContasAPagarResumoDTO contasAPagar;
    private ContasAReceberResumoDTO contasAReceber;
    private FluxoCaixaDTO fluxoCaixa;

    public ContasAPagarResumoDTO getContasAPagar() {
        return contasAPagar;
    }

    public void setContasAPagar(ContasAPagarResumoDTO contasAPagar) {
        this.contasAPagar = contasAPagar;
    }

    public ContasAReceberResumoDTO getContasAReceber() {
        return contasAReceber;
    }

    public void setContasAReceber(ContasAReceberResumoDTO contasAReceber) {
        this.contasAReceber = contasAReceber;
    }

    public FluxoCaixaDTO getFluxoCaixa() {
        return fluxoCaixa;
    }

    public void setFluxoCaixa(FluxoCaixaDTO fluxoCaixa) {
        this.fluxoCaixa = fluxoCaixa;
    }
}
