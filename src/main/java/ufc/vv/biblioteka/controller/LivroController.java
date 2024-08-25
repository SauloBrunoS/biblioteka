package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
import ufc.vv.biblioteka.model.Colecao;
import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;
import ufc.vv.biblioteka.repository.EmprestimoRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.repository.ReservaRepository;
import ufc.vv.biblioteka.service.LivroService;

@RepositoryRestController("/livros")
public class LivroController {

    private LivroService livroService;

    private LivroRepository livroRepository;

    private EmprestimoRepository emprestimoRepository;

    private ReservaRepository reservaRepository;

    @Autowired
    public LivroController(LivroService livroService, LivroRepository livroRepository,
            EmprestimoRepository emprestimoRepository, ReservaRepository reservaRepository) {
        this.livroService = livroService;
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.reservaRepository = reservaRepository;
    }

    @PostMapping
    public ResponseEntity<?> adicionarLivro(@RequestBody Livro livro) {
        try {
            Livro novoLivro = livroService.adicionarLivro(livro);
            return ResponseEntity.ok(novoLivro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar o livro.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarLivro(@PathVariable int id, @RequestBody Livro livro) {
        try {
            Livro livroAtualizado = livroService.atualizarLivro(id, livro);
            return ResponseEntity.ok(livroAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado ao atualizar o autor.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirLivro(@PathVariable int id) {
        try {
            livroService.excluirLivro(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao excluir o livro.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarLivroPorId(@PathVariable int id) {
        try {
            Livro livro = livroRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));
            return ResponseEntity.ok(livro);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Livro>> buscarLivro(@RequestParam String search, @RequestParam Colecao colecao,
            Pageable pageable) {
        Page<Livro> livros = livroRepository.findByAllFields(search, colecao, pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}/reservas")
    public ResponseEntity<Page<Reserva>> buscarReservasPorLivroId(@PathVariable int id, String search,
            StatusReserva status, Pageable pageable) {
        Page<Reserva> reservas = reservaRepository.findByLivroIdAndSearch(id, search, status, pageable);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}/emprestimos")
    public ResponseEntity<Page<Emprestimo>> buscarEmprestimosPorLivroId(@PathVariable int id, String search,
            boolean devolvido, Pageable pageable) {
        Page<Emprestimo> emprestimos = emprestimoRepository.findByLivroIdAndSearch(id, search, devolvido, pageable);
        return ResponseEntity.ok(emprestimos);
    }

}
