package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.ClienteDevedorDTO;
import com.sandrojam.controlevendas.dto.ExtratoClienteDTO;
import com.sandrojam.controlevendas.dto.RecebimentoVendaDTO;
import com.sandrojam.controlevendas.dto.VendaDTO;
import com.sandrojam.controlevendas.model.StatusPagamentoVenda;
import com.sandrojam.controlevendas.service.ExtratoPdfService;
import com.sandrojam.controlevendas.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final ExtratoPdfService extratoPdfService;

    public VendaController(VendaService vendaService, ExtratoPdfService extratoPdfService) {
        this.vendaService = vendaService;
        this.extratoPdfService = extratoPdfService;
    }

    @GetMapping
    public List<VendaDTO> listar() {
        return vendaService.listarTodas();
    }

    /** Um registro por cliente com o total que ele ainda deve — base da Consulta de Vendas. */
    @GetMapping("/clientes/devedores")
    public List<ClienteDevedorDTO> listarDevedores() {
        return vendaService.listarResumoDevedores();
    }

    /**
     * Extrato ("bobina") do cliente. Com somenteDevido=true, ignora o período e traz só o que
     * ainda está em aberto; caso contrário, filtra por período (inicio/fim opcionais).
     * statusPagamento (opcional) filtra ainda por PENDENTE/PARCIAL/PAGO.
     */
    @GetMapping("/clientes/{clienteId}/extrato")
    public ExtratoClienteDTO buscarExtratoCliente(
            @PathVariable Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "false") boolean somenteDevido,
            @RequestParam(required = false) StatusPagamentoVenda statusPagamento) {
        return vendaService.buscarExtratoCliente(clienteId, inicio, fim, somenteDevido, statusPagamento);
    }

    @GetMapping("/{id}")
    public VendaDTO buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id);
    }

    /** Baixa o extrato ("bobina") do cliente em PDF, pronto para anexar numa conversa de WhatsApp. */
    @GetMapping("/clientes/{clienteId}/extrato/pdf")
    public ResponseEntity<byte[]> baixarExtratoPdf(
            @PathVariable Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "false") boolean somenteDevido,
            @RequestParam(required = false) StatusPagamentoVenda statusPagamento) {
        ExtratoClienteDTO extrato = vendaService.buscarExtratoCliente(clienteId, inicio, fim, somenteDevido, statusPagamento);
        byte[] pdf = extratoPdfService.gerar(extrato, somenteDevido);

        String nomeArquivo = "extrato-" + slugify(extrato.getClienteNome()) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .body(pdf);
    }

    @GetMapping("/{id}/recebimentos")
    public List<RecebimentoVendaDTO> listarRecebimentos(@PathVariable Long id) {
        return vendaService.listarRecebimentos(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendaDTO criar(@Valid @RequestBody VendaDTO dto) {
        return vendaService.criar(dto);
    }

    @PostMapping("/{id}/cancelar")
    public VendaDTO cancelar(@PathVariable Long id) {
        return vendaService.cancelar(id);
    }

    /** Registra uma baixa (total ou parcial) na venda. */
    @PostMapping("/{id}/recebimentos")
    @ResponseStatus(HttpStatus.CREATED)
    public VendaDTO registrarRecebimento(@PathVariable Long id, @Valid @RequestBody RecebimentoVendaDTO dto) {
        return vendaService.registrarRecebimento(id, dto);
    }

    /** Remove acentos/espaços do nome do cliente para usar como nome de arquivo. */
    private String slugify(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
