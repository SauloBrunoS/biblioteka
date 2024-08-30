package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Autor;
import ufc.vv.biblioteka.model.Nacionalidade;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "autores", path = "autores")
public interface AutorRepository extends JpaRepository<Autor, Integer> {
        @Query("SELECT a FROM Autor a WHERE " +
                        "(:search IS NULL OR " +
                        "a.nomeCompleto iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(a.dataNascimento, 'DD/MM/YYYY') iLIKE '%'||:search||'%') AND " +
                        "(:nacionalidade IS NULL OR a.nacionalidade = :nacionalidade)")
        Page<Autor> findByAllFields(
                        @Param("search") String search,
                        @Param("nacionalidade") Nacionalidade nacionalidade,
                        Pageable pageable);

        boolean existsByNomeCompleto(String nome);

        @Query("SELECT a FROM Autor a WHERE a.nomeCompleto ILIKE '%'||:search||'%'")
        List<Autor> findAllWithFilter(@Param("search") String search);

}