package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ItemVendaDTO;
import com.sandrojam.controlevendas.dto.VendaDTO;
import com.sandrojam.controlevendas.exception.EstoqueInsuficienteException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.*;
import com.sandrojam.controlevendas.repository.ClienteRepository;
import com.sandrojam.controlevendas.repository.ProdutoRepository;
import com.sandrojam.controlevendas.repository.UsuarioRepository;
import com.sandrojam.controlevendas.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, UsuarioRepository usuarioRepository,
                         ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<VendaDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /**
     * Cria a venda, calcula os subtotais/total com base no preço ATUAL do produto
     * (nunca confia em preço vindo do cliente) e abate o estoque de cada item.
     * Se algum produto não tiver estoque suficiente, a transação inteira é revertida.
     */
    public VendaDTO criar(VendaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));

        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + dto.getClienteId()));
        }

        Venda venda = new Venda();
        venda.setUsuario(usuario);
        venda.setCliente(cliente);
        venda.setStatus(StatusVenda.FINALIZADA);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemVendaDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.getProdutoId()));

            if (produto.getEstoqueAtual() < itemDto.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para \"" + produto.getNome() + "\". Disponível: "
                                + produto.getEstoqueAtual() + ", solicitado: " + itemDto.getQuantidade());
            }

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.calcularSubtotal();

            venda.adicionarItem(item);
            valorTotal = valorTotal.add(item.getSubtotal());

            // Dirty checking do Hibernate salva essa alteração junto, sem precisar chamar save() aqui.
            produto.setEstoqueAtual(produto.getEstoqueAtual() - itemDto.getQuantidade());
        }

        venda.setValorTotal(valorTotal);

        return toDTO(vendaRepository.save(venda));
    }

    /**
     * Cancela a venda e devolve a quantidade de cada item ao estoque do produto.
     */
    public VendaDTO cancelar(Long id) {
        Venda venda = buscarEntidade(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            return toDTO(venda);
        }

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
        }

        venda.setStatus(StatusVenda.CANCELADA);

        return toDTO(vendaRepository.save(venda));
    }

    private Venda buscarEntidade(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada: " + id));
    }

    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setClienteId(venda.getCliente() != null ? venda.getCliente().getId() : null);
        dto.setDataVenda(venda.getDataVenda());
        dto.setStatus(venda.getStatus().name());
        dto.setValorTotal(venda.getValorTotal());
        dto.setItens(venda.getItens().stream().map(this::toItemDTO).toList());
        return dto;
    }

    private ItemVendaDTO toItemDTO(ItemVenda item) {
        ItemVendaDTO dto = new ItemVendaDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
