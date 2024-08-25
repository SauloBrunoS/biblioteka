package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Colecao;
import ufc.vv.biblioteka.model.Livro;

import java.util.Optional;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;

@RepositoryRestResource(collectionResourceRel = "livros", path = "livros")
public interface LivroRepository extends JpaRepository<Livro, Integer> {

        @Query("SELECT l FROM Livro l " +
                        "WHERE (:search IS NULL OR " +
                        "l.titulo LIKE %:search% OR " +
                        "l.sinopse LIKE %:search% OR " +
                        "l.isbn LIKE %:search% OR " +
                        "CAST(l.qtdPaginas AS string) LIKE %:search% OR " +
                        "CAST(l.numeroCopias AS string) LIKE %:search% OR " +
                        "CAST(l.dataPublicacao AS string) LIKE %:search% OR " +
                        "EXISTS (SELECT 1 FROM l.autores a WHERE a.nomeCompleto LIKE %:search%)) " +
                        "AND (:colecao IS NULL OR :colecao MEMBER OF l.colecoes)")
        Page<Livro> findByAllFields(String search, Colecao colecao, Pageable pageable);

        Optional<Livro> findByIsbn(String isbn);

        Page<Livro> findByAutorId(int autorId, Pageable pageable);
}
