package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.FornecedorDTO;
import com.sandrojam.controlevendas.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<FornecedorDTO> listar(@RequestParam(required = false) String nome) {
        return fornecedorService.buscar(nome);
    }

    @GetMapping("/{id}")
    public FornecedorDTO buscarPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorDTO criar(@Valid @RequestBody FornecedorDTO dto) {
        return fornecedorService.criar(dto);
    }

    @PutMapping("/{id}")
    public FornecedorDTO atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorDTO dto) {
        return fornecedorService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        fornecedorService.excluir(id);
    }
}
