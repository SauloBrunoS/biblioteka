package ufc.vv.biblioteka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Leitor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@RepositoryRestResource(collectionResourceRel = "leitores", path = "leitores")
public interface LeitorRepository extends JpaRepository<Leitor, Integer> {
    @Query("SELECT l FROM Leitor l WHERE " +
            "(:search IS NULL OR " +
            "l.nomeCompleto iLIKE '%'||:search||'%' OR " +
            "l.cpf iLIKE '%'||:search||'%' OR " +
            "l.telefone iLIKE '%'||:search||'%' OR " +
            "l.usuario.email iLIKE '%'||:search||'%')")
    Page<Leitor> findByAllFields(
            @Param("search") String search,
            Pageable pageable);

    boolean existsByCpf(String cpf);

}
