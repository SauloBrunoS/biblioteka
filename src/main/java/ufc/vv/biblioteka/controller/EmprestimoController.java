package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.LivroIndisponivelException;
import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.service.EmprestimoService;

@RepositoryRestController("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @Autowired
    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping("/emprestar")
    public ResponseEntity<?> emprestarLivro(@RequestParam int livroId, @RequestParam int leitorId, @RequestParam String senha ) {
        try {
            Emprestimo emprestimo = emprestimoService.emprestarLivro(livroId, leitorId, senha);
            return ResponseEntity.ok(emprestimo);
        } catch (LivroIndisponivelException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (LimiteExcedidoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao emprestar livro.");
        }

    }

    @PostMapping("/devolver")
    public ResponseEntity<?> devolverLivro(@RequestParam int emprestimoId, @RequestParam String senha) {
        try {
            Emprestimo emprestimo = emprestimoService.devolverLivro(emprestimoId, senha);
            return ResponseEntity.ok(emprestimo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao devolver livro.");
        }
    }
}
