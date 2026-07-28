package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.UsuarioDTO;
import com.sandrojam.controlevendas.exception.RegraNegocioException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.PerfilUsuario;
import com.sandrojam.controlevendas.model.Usuario;
import com.sandrojam.controlevendas.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public UsuarioDTO criar(UsuarioDTO dto) {
        if (!StringUtils.hasText(dto.getSenha())) {
            throw new RegraNegocioException("A senha é obrigatória ao criar um usuário.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(interpretarPerfil(dto.getPerfil()));
        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarEntidade(id);
        PerfilUsuario perfilAnterior = usuario.getPerfil();
        PerfilUsuario novoPerfil = interpretarPerfil(dto.getPerfil());

        // Rebaixar o último administrador deixaria o sistema sem ninguém para gerenciar usuários.
        boolean estaSendoRebaixado = perfilAnterior == PerfilUsuario.ADMIN && novoPerfil != PerfilUsuario.ADMIN;
        if (estaSendoRebaixado && usuarioRepository.countByPerfil(PerfilUsuario.ADMIN) <= 1) {
            throw new RegraNegocioException("Não é possível rebaixar o último usuário administrador do sistema.");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setPerfil(novoPerfil);

        // Senha só é alterada se o admin informar uma nova — em branco mantém a atual.
        if (StringUtils.hasText(dto.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toDTO(usuarioRepository.save(usuario));
    }

    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);

        if (usuario.getEmail().equalsIgnoreCase(emailAutenticado())) {
            throw new RegraNegocioException("Você não pode excluir o seu próprio usuário.");
        }

        if (usuario.getPerfil() == PerfilUsuario.ADMIN && usuarioRepository.countByPerfil(PerfilUsuario.ADMIN) <= 1) {
            throw new RegraNegocioException("Não é possível excluir o último usuário administrador do sistema.");
        }

        usuarioRepository.delete(usuario);
    }

    Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private PerfilUsuario interpretarPerfil(String perfil) {
        if (!StringUtils.hasText(perfil)) {
            return PerfilUsuario.VENDEDOR;
        }
        try {
            return PerfilUsuario.valueOf(perfil.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException("Perfil inválido: " + perfil + ". Use ADMIN ou VENDEDOR.");
        }
    }

    private String emailAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil().name());
        // senha nunca volta na resposta
        return dto;
    }
}
