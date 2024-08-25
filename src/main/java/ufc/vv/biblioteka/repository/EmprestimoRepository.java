package ufc.vv.biblioteka.repository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.vv.biblioteka.model.Emprestimo;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "(:search IS NULL OR " +
            "STR(e.dataEmprestimo) LIKE %:search% OR " +
            "STR(e.dataLimite) LIKE %:search% OR " +
            "STR(e.dataDevolucao) LIKE %:search% OR " +
            "STR(e.valorBase) LIKE %:search% OR " +
            "STR(e.multa) LIKE %:search% OR " +
            "e.livro.isbn LIKE %:search%) " +
            "AND (:devolvido IS NULL OR e.devolvido = :devolvido) " +
            "AND e.leitor.id = :leitorId")
    Page<Emprestimo> findByLeitorIdAndSearch(@Param("leitorId") int leitorId,
            @Param("search") String search,
            @Param("devolvido") Boolean devolvido,
            Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "(:search IS NULL OR " +
            "STR(e.dataEmprestimo) LIKE %:search% OR " +
            "STR(e.dataLimite) LIKE %:search% OR " +
            "STR(e.dataDevolucao) LIKE %:search% OR " +
            "STR(e.valorBase) LIKE %:search% OR " +
            "STR(e.multa) LIKE %:search% OR " +
            "e.leitor.nomeCompleto LIKE %:search%) " +
            "AND (:devolvido IS NULL OR e.devolvido = :devolvido) " +
            "AND e.livro.id = :livroId")
    Page<Emprestimo> findByLivroIdAndSearch(@Param("livroId") int livroId,
            @Param("search") String search,
            @Param("devolvido") Boolean devolvido,
            Pageable pageable);
}
