package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.vv.biblioteka.model.Leitor;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;

@Repository
public interface LeitorRepository extends JpaRepository<Leitor, Integer> {
    @Query("SELECT l FROM Leitor l WHERE " +
           "(:search IS NULL OR l.nomeCompleto LIKE %:search% OR l.endereco LIKE %:search% OR l.telefone LIKE %:search% OR l.usuario.email LIKE %:search%)")
    Page<Leitor> findByAllFields(@Param("search") String search, Pageable pageable);

}
