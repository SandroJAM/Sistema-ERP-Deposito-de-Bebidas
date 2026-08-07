package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.TipoCascoDTO;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.TipoCasco;
import com.sandrojam.controlevendas.repository.TipoCascoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TipoCascoService {

    private final TipoCascoRepository tipoCascoRepository;

    public TipoCascoService(TipoCascoRepository tipoCascoRepository) {
        this.tipoCascoRepository = tipoCascoRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoCascoDTO> listarTodos() {
        return tipoCascoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<TipoCascoDTO> listarAtivos() {
        return tipoCascoRepository.findByAtivoTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public TipoCascoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public TipoCascoDTO criar(TipoCascoDTO dto) {
        TipoCasco tipoCasco = new TipoCasco();
        aplicarDTO(tipoCasco, dto);
        return toDTO(tipoCascoRepository.save(tipoCasco));
    }

    public TipoCascoDTO atualizar(Long id, TipoCascoDTO dto) {
        TipoCasco tipoCasco = buscarEntidade(id);
        aplicarDTO(tipoCasco, dto);
        return toDTO(tipoCascoRepository.save(tipoCasco));
    }

    public void excluir(Long id) {
        TipoCasco tipoCasco = buscarEntidade(id);
        tipoCascoRepository.delete(tipoCasco);
    }

    private void aplicarDTO(TipoCasco tipoCasco, TipoCascoDTO dto) {
        tipoCasco.setNome(dto.getNome());
        tipoCasco.setValorReposicao(dto.getValorReposicao() != null ? dto.getValorReposicao() : BigDecimal.ZERO);
        tipoCasco.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
    }

    private TipoCasco buscarEntidade(Long id) {
        return tipoCascoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de casco não encontrado: " + id));
    }

    private TipoCascoDTO toDTO(TipoCasco tipoCasco) {
        TipoCascoDTO dto = new TipoCascoDTO();
        dto.setId(tipoCasco.getId());
        dto.setNome(tipoCasco.getNome());
        dto.setValorReposicao(tipoCasco.getValorReposicao());
        dto.setAtivo(tipoCasco.getAtivo());
        return dto;
    }
}
