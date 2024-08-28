package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.ReservaNaoPodeMaisSerCancelaException;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.service.ReservaService;

@RepositoryRestController("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> reservarLivro(@RequestParam int livroId, @RequestParam int leitorId, @RequestParam String senha) {
        try {
            Reserva reserva = reservaService.reservarLivro(livroId, leitorId, senha);
            return ResponseEntity.ok(reserva);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LimiteExcedidoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao reservar o livro.");
        }
    }

    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarReserva(@RequestParam int reservaId, @RequestParam String senha) {
        try {
            Reserva reserva = reservaService.cancelarReserva(reservaId, senha);
            return ResponseEntity.ok(reserva);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ReservaNaoPodeMaisSerCancelaException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado ao cancelar a reserva.");
        }
    }
}