package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.repository.ColecaoRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.service.ColecaoService;

@RepositoryRestController("/colecoes")
public class ColecaoController {

    private ColecaoRepository colecaoRepository;

    private LivroRepository livroRepository;

    private ColecaoService colecaoService;

    @Autowired
    public ColecaoController(ColecaoRepository colecaoRepository, ColecaoService colecaoService,
            LivroRepository livroRepository) {
        this.colecaoRepository = colecaoRepository;
        this.colecaoService = colecaoService;
        this.livroRepository = livroRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colecao> getColecaoById(@PathVariable int id) {
        return colecaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createColecao(@RequestBody Colecao colecao) {
        try {
            Colecao createdColecao = colecaoService.createColecao(colecao);
            return ResponseEntity.ok(createdColecao);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar o autor.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatedColecao(@PathVariable int id, @RequestBody Colecao colecaoDetails) {
        try {
            Colecao updatedColecao = colecaoService.updateColecao(id, colecaoDetails);
            return ResponseEntity.ok(updatedColecao);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado ao atualizar o autor.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColecao(@PathVariable int id) {
        try {
            colecaoService.deleteColecaoById(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao excluir o autor.");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Colecao>> buscarColecoes(@RequestParam String search,
            Pageable pageable) {
        Page<Colecao> colecoes = colecaoRepository.findByNomeAndDescricao(search, pageable);
        return ResponseEntity.ok(colecoes);
    }

    @GetMapping("{id}/livros")
    public ResponseEntity<Page<Livro>> buscarLivrosPorColecaoId(@PathVariable("id") int idColecao,
            @RequestParam String search, @RequestParam(required=false) Integer idAutor, Pageable pageable) {
        Page<Livro> livros = livroRepository.findByAllFields(search, idColecao, idAutor, pageable);
        return ResponseEntity.ok(livros);
    }
}
