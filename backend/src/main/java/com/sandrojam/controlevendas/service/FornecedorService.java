package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.FornecedorDTO;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Fornecedor;
import com.sandrojam.controlevendas.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    /**
     * Busca fornecedores pelo nome (contendo, case-insensitive). Se o termo vier vazio/nulo,
     * retorna todos — assim o mesmo endpoint serve tanto pra listagem quanto pra busca.
     */
    @Transactional(readOnly = true)
    public List<FornecedorDTO> buscar(String nome) {
        List<Fornecedor> fornecedores = (nome == null || nome.isBlank())
                ? fornecedorRepository.findAll()
                : fornecedorRepository.findByNomeContainingIgnoreCase(nome.trim());

        return fornecedores.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public FornecedorDTO criar(FornecedorDTO dto) {
        Fornecedor fornecedor = new Fornecedor();
        aplicarDTO(fornecedor, dto);
        return toDTO(fornecedorRepository.save(fornecedor));
    }

    public FornecedorDTO atualizar(Long id, FornecedorDTO dto) {
        Fornecedor fornecedor = buscarEntidade(id);
        aplicarDTO(fornecedor, dto);
        return toDTO(fornecedorRepository.save(fornecedor));
    }

    public void excluir(Long id) {
        Fornecedor fornecedor = buscarEntidade(id);
        fornecedorRepository.delete(fornecedor);
    }

    Fornecedor buscarEntidade(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + id));
    }

    private void aplicarDTO(Fornecedor fornecedor, FornecedorDTO dto) {
        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpjCpf(dto.getCnpjCpf());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
    }

    private FornecedorDTO toDTO(Fornecedor fornecedor) {
        return new FornecedorDTO(
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getCnpjCpf(),
                fornecedor.getTelefone(),
                fornecedor.getEmail(),
                fornecedor.getAtivo()
        );
    }
}
