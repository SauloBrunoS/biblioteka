package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;
import ufc.vv.biblioteka.repository.EmprestimoRepository;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.repository.ReservaRepository;
import ufc.vv.biblioteka.service.LeitorService;

import org.springframework.data.domain.Page;

@RepositoryRestController("/leitores")
public class LeitorController {

    private LeitorService leitorService;
    private LeitorRepository leitorRepository;
    private EmprestimoRepository emprestimoRepository;
    private ReservaRepository reservaRepository;

    @Autowired
    public LeitorController(LeitorService leitorService, LeitorRepository leitorRepository,
            EmprestimoRepository emprestimoRepository,
            ReservaRepository reservaRepository) {
        this.leitorRepository = leitorRepository;
        this.leitorService = leitorService;
        this.emprestimoRepository = emprestimoRepository;
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leitor> getLeitorById(@PathVariable int id) {
        return leitorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criarLeitor(@RequestBody Leitor leitor) {
        try {
            Leitor novoLeitor = leitorService.criarLeitor(leitor);
            return ResponseEntity.ok(novoLeitor);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar leitor.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarLeitor(@PathVariable int id, @RequestBody Leitor leitorAtualizado) {
        try {
            Leitor leitorAtualizadoResponse = leitorService.atualizarLeitor(id, leitorAtualizado);
            return ResponseEntity.ok(leitorAtualizadoResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar leitor.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirLeitor(@PathVariable int id) {
        try {
            leitorService.excluirLeitor(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir leitor.");
        }
    }

    @GetMapping("/{leitorId}/emprestimos")
    public ResponseEntity<Page<Emprestimo>> getEmprestimosPorLeitorId(@PathVariable("leitorId") int leitorId,
            @RequestParam(required = false) String search, @RequestParam(required = false) Integer livroId,
            @RequestParam(required = false) Boolean devolvido,
            Pageable pageable) {
        Page<Emprestimo> emprestimos = emprestimoRepository.findByLeitorIdAndSearch(leitorId, search, livroId,
                devolvido, pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/{leitorId}/reservas")
    public ResponseEntity<Page<Reserva>> getReservasPorLeitorId(@PathVariable int leitorId, String search,
            StatusReserva status,
            Pageable pageable) {
                Page<Reserva> reservas =  reservaRepository.findByLeitorIdAndSearch(leitorId, search, status, pageable);
                return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Leitor>> buscarLeitores(
            @RequestParam String search, Pageable pageable) {
        Page<Leitor> leitores = leitorRepository.findByAllFields(search, pageable);
        return ResponseEntity.ok(leitores);
    }
}