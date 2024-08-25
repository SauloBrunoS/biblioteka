package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ufc.vv.biblioteka.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);
}
