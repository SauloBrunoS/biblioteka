package ufc.vv.biblioteka.repository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    @Query("SELECT r FROM Reserva r WHERE " +
            "r.livro.id = :livroId AND " +
            "(:search IS NULL OR " +
            "CAST(r.dataCadastro AS string) LIKE %:search% OR " +
            "CAST(r.dataLimite AS string) LIKE %:search% OR " +
            "r.leitor.nomeCompleto LIKE %:search% AND " +
            "(:status IS NULL OR r.status = :status)")
    Page<Reserva> findByLivroIdAndSearch(
            @Param("livroId") int livroId,
            @Param("search") String search,
            @Param("status") StatusReserva status,
            Pageable pageable);

    @Query("SELECT r FROM Reserva r WHERE " +
            "r.leitor.id = :leitorId AND " +
            "(:search IS NULL OR " +
            "CAST(r.dataCadastro AS string) LIKE %:search% OR " +
            "CAST(r.dataLimite AS string) LIKE %:search% OR " +
            "r.leitor.nomeCompleto LIKE %:search% AND " +
            "(:status IS NULL OR r.status = :status)")
    Page<Reserva> findByLeitorIdAndSearch(
            @Param("leitorId") int leitorId,
            @Param("search") String search,
            @Param("status") StatusReserva status,
            Pageable pageable);

}
