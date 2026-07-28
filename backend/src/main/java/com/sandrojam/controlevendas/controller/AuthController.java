package com.sandrojam.controlevendas.controller;

import com.sandrojam.controlevendas.dto.LoginRequestDTO;
import com.sandrojam.controlevendas.dto.LoginResponseDTO;
import com.sandrojam.controlevendas.model.Usuario;
import com.sandrojam.controlevendas.repository.UsuarioRepository;
import com.sandrojam.controlevendas.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                           UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        // Se email/senha estiverem errados, isso lança BadCredentialsException,
        // que vira automaticamente um 403 (tratado pelo Spring Security).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getPerfil())
                .build();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(usuario.getId(), token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
