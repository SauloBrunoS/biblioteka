package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Autor;
import ufc.vv.biblioteka.model.Nacionalidade;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;

@RepositoryRestResource(collectionResourceRel = "autores", path = "autores")
public interface AutorRepository extends JpaRepository<Autor, Integer> {
    @Query("SELECT a FROM Autor a WHERE " +
            "(:search IS NULL OR " +
            "a.nomeCompleto LIKE %:search% OR " +
            "a.biografia LIKE %:search% OR " +
            "CONVERT(a.dataNascimento, 'string') LIKE %:search%) AND " +
            "(:nacionalidade IS NULL OR a.nacionalidade = :nacionalidade)")
    Page<Autor> findByAllFields(@Param("search") String search,
            @Param("nacionalidade") Nacionalidade nacionalidade,
            Pageable pageable);

    boolean existsByNome(String nome);

}