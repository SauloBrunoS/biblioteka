package ufc.vv.biblioteka.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.model.Colecao;
import ufc.vv.biblioteka.repository.ColecaoRepository;

@Service
public class ColecaoService {

    private ColecaoRepository colecaoRepository;

    public ColecaoService(ColecaoRepository colecaoRepository) {
        this.colecaoRepository = colecaoRepository;
    }

    public Colecao createColecao(Colecao colecao) {
        validarColecao(colecao);

        if (colecaoRepository.existsByNome(colecao.getNome())) {
            throw new DuplicateKeyException("Uma coleção com este nome já está cadastrada.");
        }

        return colecaoRepository.save(colecao);
    }

    public Colecao updateColecao(int id, Colecao updatedColecao) {
        Colecao existingColecao = colecaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coleção não encontrada"));

        validarColecao(updatedColecao);

        existingColecao.setNome(updatedColecao.getNome());
        existingColecao.setDescricao(updatedColecao.getDescricao());

        return colecaoRepository.save(existingColecao);
    }

    public void deleteColecaoById(int id) {
        Colecao colecao = colecaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coleção não encontrada"));

        // Verifica se a coleção tem livros associados
        if (colecao.getLivros() != null && !colecao.getLivros().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Não é possível excluir a coleção porque ele tem livros associados.");
        }

        colecaoRepository.delete(colecao);
    }

    private void validarColecao(Colecao colecao) {
        if (colecao == null) {
            throw new IllegalArgumentException("Coleção não pode ser nula.");
        }
        if (colecao.getNome() == null || colecao.getNome().isEmpty() ||
                colecao.getDescricao() == null ||
                colecao.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos.");
        }
    }

}
