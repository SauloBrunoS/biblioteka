package ufc.vv.biblioteka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    private LeitorRepository leitorRepository;

    private UsuarioRepository usuarioRepository;

    @Autowired
    public LeitorService(LeitorRepository leitorRepository, UsuarioRepository usuarioRepository) {
        this.leitorRepository = leitorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Leitor criarLeitor(Leitor leitor) {
        validarLeitorAoCadastrar(leitor);

        if (usuarioRepository.existsByEmail(leitor.getUsuario().getEmail())) {
            throw new DuplicateKeyException("Um usuário com este e-mail já está cadastrado.");
        }

        if (leitorRepository.existsByCpf(leitor.getCpf())) {
            throw new DuplicateKeyException("Um usuário com este cpf já está cadastrado.");
        }

        Usuario usuario = leitor.getUsuario();
        usuario.setTipoUsuario(TipoUsuario.LEITOR);
        return leitorRepository.save(leitor);
    }

    public Leitor atualizarLeitor(int id, Leitor leitorAtualizado) {
        Leitor leitorExistente = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        validarLeitorAoEditar(leitorAtualizado);

        leitorExistente.setNomeCompleto(leitorAtualizado.getNomeCompleto());
        leitorExistente.setTelefone(leitorAtualizado.getTelefone());
        leitorExistente.getUsuario().setEmail(leitorAtualizado.getUsuario().getEmail());

        return leitorRepository.save(leitorExistente);
    }

    public void excluirLeitor(int id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        if ((leitor.getEmprestimos() != null && !leitor.getEmprestimos().isEmpty())
                || (leitor.getReservas() != null && !leitor.getReservas().isEmpty())) {
            throw new DataIntegrityViolationException(
                    "Não é possível excluir o leitor porque ele tem empréstimos ou reservas.");
        }
        leitorRepository.delete(leitor);
    }

    private void validarLeitorAoCadastrar(Leitor leitor) {
        if (leitor.getNomeCompleto() == null || leitor.getNomeCompleto().isEmpty() ||
                leitor.getTelefone() == null || leitor.getTelefone().isEmpty() ||
                leitor.getCpf() == null || leitor.getCpf().isEmpty() ||
                leitor.getUsuario() == null || leitor.getUsuario().getEmail() == null
                || leitor.getUsuario().getEmail().isEmpty() || leitor.getUsuario().getSenha() == null
                || leitor.getUsuario().getSenha().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos.");
        }
    }

    private void validarLeitorAoEditar(Leitor leitor) {
        if (leitor.getNomeCompleto() == null || leitor.getNomeCompleto().isEmpty() ||
                leitor.getTelefone() == null || leitor.getTelefone().isEmpty() ||
                leitor.getCpf() == null || leitor.getCpf().isEmpty() ||
                leitor.getUsuario() == null || leitor.getUsuario().getEmail() == null
                || leitor.getUsuario().getEmail().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos.");
        }
    }

}
