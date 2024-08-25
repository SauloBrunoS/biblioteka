package ufc.vv.biblioteka.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ufc.vv.biblioteka.model.Usuario;
import ufc.vv.biblioteka.repository.UsuarioRepository;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario save(Usuario usuario) {
        String hashedPassword = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(hashedPassword);
        return usuarioRepository.save(usuario);
    }

    public String login(String email, String rawPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(rawPassword, usuario.getSenha())) {
            // Gerar e retornar um JWT
            return generateToken(usuario);
        } else {
            throw new RuntimeException("Credenciais inválidas");
        }
    }
}