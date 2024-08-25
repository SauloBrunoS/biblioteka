package ufc.vv.biblioteka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.repository.EmprestimoRepository;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.service.LeitorService;
import java.util.List;
import org.springframework.data.domain.Page;

@RepositoryRestController("/leitores")
public class LeitorController {

    @Autowired
    private LeitorService leitorService;
    @Autowired
    private LeitorRepository leitorRepository;
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Leitor> getLeitorById(@PathVariable int id) {
        return leitorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Leitor> criarLeitor(@RequestBody Leitor leitor) {
        return ResponseEntity.ok(leitorService.criarLeitor(leitor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leitor> atualizarLeitor(@PathVariable int id, @RequestBody Leitor leitorAtualizado) {
        return ResponseEntity.ok(leitorService.atualizarLeitor(id, leitorAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLeitor(@PathVariable int id) {
        leitorService.excluirLeitor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{leitorId}/emprestimos")
    public Page<Emprestimo> getEmprestimosPorLeitor(@PathVariable int leitorId, Pageable pageable) {
        return emprestimoRepository.findByLeitorId(leitorId, pageable);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Leitor>> buscarLeitores(
            @RequestParam String search, Pageable pageable) {
        Page<Leitor> leitores = leitorRepository.findByAllFields(search, pageable);
        return ResponseEntity.ok(leitores);
    }
}