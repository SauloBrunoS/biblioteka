package ufc.vv.biblioteka.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.Livro;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "emprestimos", path = "emprestimos")
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

        @Query("SELECT e FROM Emprestimo e WHERE " +
                        "(:search IS NULL OR " +
                        "TO_CHAR(e.dataEmprestimo, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(e.dataLimite, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(e.dataDevolucao, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "CAST(e.valorBase AS string) iLIKE '%'||:search||'%' OR " +
                        "CAST(e.multa AS string) iLIKE '%'||:search||'%' OR " +
                        "CAST(e.quantidadeRenovacoes AS string) iLIKE '%'||:search||'%' OR " +
                        "e.livro.isbn iLIKE '%'||:search||'%') " +
                        "AND (:devolvido IS NULL OR e.devolvido = :devolvido) " +
                        "AND e.leitor.id = :leitorId")
        Page<Emprestimo> findByLeitorIdAndSearch(
                        @Param("leitorId") int leitorId,
                        @Param("search") String search,
                        @Param("devolvido") Boolean devolvido,
                        Pageable pageable);

        @Query("SELECT e FROM Emprestimo e WHERE " +
                        "(:search IS NULL OR " +
                        "TO_CHAR(e.dataEmprestimo, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(e.dataLimite, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(e.dataDevolucao, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "CAST(e.valorBase AS string) iLIKE '%'||:search||'%' OR " +
                        "CAST(e.multa AS string) iLIKE '%'||:search||'%' OR " +
                        "e.leitor.nomeCompleto iLIKE '%'||:search||'%') " +
                        "AND (:devolvido IS NULL OR e.devolvido = :devolvido) " +
                        "AND e.livro.id = :livroId")
        Page<Emprestimo> findByLivroIdAndSearch(
                        @Param("livroId") int livroId,
                        @Param("search") String search,
                        @Param("devolvido") Boolean devolvido,
                        Pageable pageable);

        Optional<Emprestimo> findByLivroAndLeitorAndDevolvidoFalse(Livro livro, Leitor leitor);
}
