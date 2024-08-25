package ufc.vv.biblioteka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.TipoUsuario;
import ufc.vv.biblioteka.model.Usuario;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.repository.UsuarioRepository;

@Service
public class LeitorService {

    @Autowired
    private LeitorRepository leitorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Leitor criarLeitor(Leitor leitor) {
        validarLeitor(leitor);

        if (usuarioRepository.existsByEmail(leitor.getUsuario().getEmail())) {
            throw new DuplicateKeyException("Um usuário com este e-mail já está cadastrado.");
        }

        Usuario usuario = leitor.getUsuario();
        usuario.setTipoUsuario(TipoUsuario.LEITOR);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        leitor.getUsuario().setId(usuarioSalvo.getId());

        return leitorRepository.save(leitor);
    }

    public Leitor atualizarLeitor(int id, Leitor leitorAtualizado) {
        Leitor leitorExistente = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        validarLeitor(leitorAtualizado);

        leitorExistente.setNomeCompleto(leitorAtualizado.getNomeCompleto());
        leitorExistente.setEndereco(leitorAtualizado.getEndereco());
        leitorExistente.setTelefone(leitorAtualizado.getTelefone());

        Usuario usuarioAtualizado = leitorAtualizado.getUsuario();
        if (usuarioAtualizado != null) {
            Usuario usuarioExistente = usuarioRepository.findById(usuarioAtualizado.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            usuarioExistente.setEmail(usuarioAtualizado.getEmail());

            usuarioRepository.save(usuarioExistente);
        }

        return leitorRepository.save(leitorExistente);
    }

    public void excluirLeitor(int id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        if (leitor.getEmprestimos() != null && !leitor.getEmprestimos().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir o leitor porque ele tem empréstimos pendentes.");
        }
        leitorRepository.delete(leitor);
    }

    private void validarLeitor(Leitor leitor) {
        if (leitor.getNomeCompleto() == null || leitor.getNomeCompleto().isEmpty() ||
                leitor.getEndereco() == null || leitor.getEndereco().isEmpty() ||
                leitor.getTelefone() == null || leitor.getTelefone().isEmpty() ||
                leitor.getUsuario() == null || leitor.getUsuario().getEmail() == null
                || leitor.getUsuario().getEmail().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos.");
        }
    }

}
