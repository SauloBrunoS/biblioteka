package ufc.vv.biblioteka.repository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Colecao;

@RepositoryRestResource(collectionResourceRel = "colecoes", path = "colecoes")
public interface ColecaoRepository extends JpaRepository<Colecao, Integer> {
    @Query("SELECT c FROM Colecao c WHERE " +
            "(:search IS NULL OR " +
            "c.nome LIKE %:search% OR " +
            "c.descricao LIKE %:search%)")
    Page<Colecao> findByNomeAndDescricao(String search,
            Pageable pageable);

    boolean existsByNome(String nome);
}