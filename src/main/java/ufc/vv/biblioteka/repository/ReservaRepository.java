package ufc.vv.biblioteka.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;

@RepositoryRestResource(collectionResourceRel = "reservas", path = "reservas")
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
        @Query("SELECT r FROM Reserva r WHERE " +
                        "r.livro.id = :livroId AND (" +
                        "TO_CHAR(r.dataCadastro, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(r.dataLimite, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "r.leitor.nomeCompleto iLIKE '%'||:search||'%') " +
                        "AND (:status IS NULL OR r.status = :status)")
        Page<Reserva> findByLivroIdAndSearch(
                        @Param("livroId") int livroId,
                        @Param("search") String search,
                        @Param("status") StatusReserva status,
                        Pageable pageable);

        @Query("SELECT r FROM Reserva r WHERE " +
                        "r.leitor.id = :leitorId AND (" +
                        "TO_CHAR(r.dataCadastro, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "TO_CHAR(r.dataLimite, 'DD/MM/YYYY') iLIKE '%'||:search||'%' OR " +
                        "r.leitor.nomeCompleto iLIKE '%'||:search||'%') " +
                        "AND (:status IS NULL OR r.status = :status)")
        Page<Reserva> findByLeitorIdAndSearch(
                        @Param("leitorId") int leitorId,
                        @Param("search") String search,
                        @Param("status") StatusReserva status,
                        Pageable pageable);

        List<Reserva> findByStatusAndDataLimite(StatusReserva status, LocalDate dataLimite);

}
