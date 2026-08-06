package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.EmpresaDTO;
import com.sandrojam.controlevendas.exception.RegraNegocioException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Empresa;
import com.sandrojam.controlevendas.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD do cadastro da empresa. Na prática só existe um registro — nome/endereço/telefone
 * usados no topo das telas e no cabeçalho do histórico/PDF de vendas — então criar() bloqueia
 * um segundo cadastro enquanto já existir um.
 */
@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarTodas() {
        return empresaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /** Usado pelo cabeçalho das telas e pelo extrato/PDF — o primeiro (e normalmente único) cadastro. */
    @Transactional(readOnly = true)
    public Optional<EmpresaDTO> buscarAtual() {
        return empresaRepository.findAll().stream().findFirst().map(this::toDTO);
    }

    public EmpresaDTO criar(EmpresaDTO dto) {
        if (empresaRepository.count() > 0) {
            throw new RegraNegocioException(
                    "Já existe uma empresa cadastrada. Edite o cadastro existente em vez de criar um novo.");
        }

        Empresa empresa = new Empresa();
        empresa.setNome(dto.getNome());
        empresa.setEndereco(dto.getEndereco());
        empresa.setTelefone(dto.getTelefone());
        return toDTO(empresaRepository.save(empresa));
    }

    public EmpresaDTO atualizar(Long id, EmpresaDTO dto) {
        Empresa empresa = buscarEntidade(id);
        empresa.setNome(dto.getNome());
        empresa.setEndereco(dto.getEndereco());
        empresa.setTelefone(dto.getTelefone());
        return toDTO(empresaRepository.save(empresa));
    }

    public void excluir(Long id) {
        Empresa empresa = buscarEntidade(id);
        empresaRepository.delete(empresa);
    }

    private Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada: " + id));
    }

    private EmpresaDTO toDTO(Empresa empresa) {
        return new EmpresaDTO(empresa.getId(), empresa.getNome(), empresa.getEndereco(), empresa.getTelefone());
    }
}
