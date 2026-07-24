package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.VendaDTO;
import com.sandrojam.controlevendas.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<VendaDTO> listar() {
        return vendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public VendaDTO buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id);
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
}
