package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.NotaEntradaDTO;
import com.sandrojam.controlevendas.service.NotaEntradaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas-entrada")
public class NotaEntradaController {

    private final NotaEntradaService notaEntradaService;

    public NotaEntradaController(NotaEntradaService notaEntradaService) {
        this.notaEntradaService = notaEntradaService;
    }

    @GetMapping
    public List<NotaEntradaDTO> listar() {
        return notaEntradaService.listarTodas();
    }

    @GetMapping("/{id}")
    public NotaEntradaDTO buscarPorId(@PathVariable Long id) {
        return notaEntradaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaEntradaDTO criar(@Valid @RequestBody NotaEntradaDTO dto) {
        return notaEntradaService.criar(dto);
    }

    @PostMapping("/{id}/cancelar")
    public NotaEntradaDTO cancelar(@PathVariable Long id) {
        return notaEntradaService.cancelar(id);
    }
}
