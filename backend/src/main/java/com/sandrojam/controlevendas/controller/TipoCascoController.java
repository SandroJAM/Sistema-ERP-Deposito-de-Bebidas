package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.TipoCascoDTO;
import com.sandrojam.controlevendas.service.TipoCascoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-casco")
public class TipoCascoController {

    private final TipoCascoService tipoCascoService;

    public TipoCascoController(TipoCascoService tipoCascoService) {
        this.tipoCascoService = tipoCascoService;
    }

    @GetMapping
    public List<TipoCascoDTO> listar(@RequestParam(required = false, defaultValue = "false") boolean somenteAtivos) {
        return somenteAtivos ? tipoCascoService.listarAtivos() : tipoCascoService.listarTodos();
    }

    @GetMapping("/{id}")
    public TipoCascoDTO buscarPorId(@PathVariable Long id) {
        return tipoCascoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoCascoDTO criar(@Valid @RequestBody TipoCascoDTO dto) {
        return tipoCascoService.criar(dto);
    }

    @PutMapping("/{id}")
    public TipoCascoDTO atualizar(@PathVariable Long id, @Valid @RequestBody TipoCascoDTO dto) {
        return tipoCascoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        tipoCascoService.excluir(id);
    }
}
