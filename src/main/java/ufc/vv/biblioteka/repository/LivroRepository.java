package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Livro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@RepositoryRestResource(collectionResourceRel = "livros", path = "livros")
public interface LivroRepository extends JpaRepository<Livro, Integer> {

        @Query("SELECT l FROM Livro l WHERE " +
                        "(:search IS NULL OR " +
                        "l.titulo iLIKE '%'||:search||'%' OR " +
                        "l.isbn iLIKE '%'||:search||'%' OR " +
                        "CAST(l.qtdPaginas AS string) iLIKE '%'||:search||'%' OR " +
                        "CAST(l.numeroCopiasDisponiveis AS string) iLIKE '%'||:search||'%' OR " +
                        "CAST(l.numeroCopiasTotais AS string) iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(l.dataPublicacao, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "EXISTS (SELECT 1 FROM l.autores a WHERE a.nomeCompleto iLIKE '%'||:search||'%')) " +
                        "AND (:colecaoId IS NULL OR :colecaoId IN (SELECT c.id FROM l.colecoes c)) " +
                        "AND (:autorId IS NULL OR :autorId IN (SELECT a.id FROM l.autores a))")
        Page<Livro> findByAllFields(
                        @Param("search") String search,
                        @Param("colecaoId") Integer colecaoId,
                        Integer autorId,
                        Pageable pageable);

        Optional<Livro> findByIsbn(String isbn);

        @Query("SELECT l FROM Livro l WHERE l.isbn ILIKE '%'||:search||'%'")
        List<Livro> findAllWithISBNFilter(@Param("search") String search);

}
