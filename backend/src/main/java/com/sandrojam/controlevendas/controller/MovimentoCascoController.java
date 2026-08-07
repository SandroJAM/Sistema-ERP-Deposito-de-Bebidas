package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.MovimentoCascoDTO;
import com.sandrojam.controlevendas.dto.SaldoCascoDTO;
import com.sandrojam.controlevendas.service.MovimentoCascoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentos-casco")
public class MovimentoCascoController {

    private final MovimentoCascoService movimentoCascoService;

    public MovimentoCascoController(MovimentoCascoService movimentoCascoService) {
        this.movimentoCascoService = movimentoCascoService;
    }

    /** Lista movimentos. Informe clienteId para filtrar o histórico de um cliente específico. */
    @GetMapping
    public List<MovimentoCascoDTO> listar(@RequestParam(required = false) Long clienteId) {
        return clienteId != null
                ? movimentoCascoService.listarPorCliente(clienteId)
                : movimentoCascoService.listarTodos();
    }

    /** Um registro por cliente + tipo de casco com saldo em aberto — "quem está com casco". */
    @GetMapping("/saldos")
    public List<SaldoCascoDTO> listarSaldos() {
        return movimentoCascoService.listarSaldosEmAberto();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentoCascoDTO registrar(@Valid @RequestBody MovimentoCascoDTO dto) {
        return movimentoCascoService.registrar(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        movimentoCascoService.excluir(id);
    }
}
