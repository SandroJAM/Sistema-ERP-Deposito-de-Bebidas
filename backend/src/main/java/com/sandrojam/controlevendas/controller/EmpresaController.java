package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.EmpresaDTO;
import com.sandrojam.controlevendas.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public List<EmpresaDTO> listar() {
        return empresaService.listarTodas();
    }

    /** Usado no cabeçalho de todas as telas — o cadastro atual da empresa (se existir). */
    @GetMapping("/atual")
    public ResponseEntity<EmpresaDTO> buscarAtual() {
        return empresaService.buscarAtual()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public EmpresaDTO buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaDTO criar(@Valid @RequestBody EmpresaDTO dto) {
        return empresaService.criar(dto);
    }

    @PutMapping("/{id}")
    public EmpresaDTO atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO dto) {
        return empresaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        empresaService.excluir(id);
    }
}
