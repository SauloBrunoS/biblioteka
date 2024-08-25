package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
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
import ufc.vv.biblioteka.model.Autor;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.model.Nacionalidade;
import ufc.vv.biblioteka.repository.AutorRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.service.AutorService;

import org.springframework.data.domain.Page;

@RepositoryRestController("/autores")
public class AutorController {

    private AutorRepository autorRepository;

    private AutorService autorService;

    private LivroRepository livroRepository;

    @Autowired
    public AutorController(AutorRepository autorRepository, AutorService autorService,
            LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.autorService = autorService;
        this.livroRepository = livroRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable int id) {
        return autorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAutor(@RequestBody Autor autor) {
        try {
            Autor createdAutor = autorService.createAutor(autor);
            return ResponseEntity.ok(createdAutor);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar o autor.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAutor(@PathVariable int id, @RequestBody Autor autorDetails) {
        try {
            Autor updatedAutor = autorService.updateAutor(id, autorDetails);
            return ResponseEntity.ok(updatedAutor);
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
    public ResponseEntity<?> deleteAutor(@PathVariable int id) {
        try {
            autorService.deleteAutorById(id);
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
    public ResponseEntity<Page<Autor>> buscarAutores(@RequestParam String search, Nacionalidade nacionalidade,
            Pageable pageable) {
        Page<Autor> autores = autorRepository.findByAllFields(search, nacionalidade, pageable);
        return ResponseEntity.ok(autores);
    }

    @GetMapping("/{id}/livros")
    public ResponseEntity<Page<Livro>> getLivrosByAutor(@PathVariable int id, Pageable pageable) {
        Page<Livro> livros = livroRepository.findByAutorId(id, pageable);
        return ResponseEntity.ok(livros);
    }
}