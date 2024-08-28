package ufc.vv.biblioteka.service;

import java.time.LocalDate;

import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ufc.vv.biblioteka.exception.DataRenovacaoException;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.LivroIndisponivelException;
import ufc.vv.biblioteka.exception.ReservaEmEsperaException;
import ufc.vv.biblioteka.model.Emprestimo;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;
import ufc.vv.biblioteka.repository.EmprestimoRepository;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.repository.ReservaRepository;

import java.util.Optional;

@Service
public class EmprestimoService {

    // final
    private EmprestimoRepository emprestimoRepository;

    private LivroRepository livroRepository;

    private LeitorRepository leitorRepository;

    private ReservaRepository reservaRepository;

    private UsuarioService usuarioService;

    @Autowired
    public EmprestimoService(EmprestimoRepository emprestimoRepository,
            LivroRepository livroRepository, UsuarioService usuarioService,
            LeitorRepository leitorRepository, ReservaRepository reservaRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Emprestimo emprestarLivro(int livroId, int leitorId, String senha) {

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        if (!usuarioService.verificarSenha(leitor.getUsuario().getId(), senha)) {
            throw new AccessDeniedException("Senha incorreta");
        }

        // Verificar se o leitor já possui um empréstimo não devolvido para este livro
        boolean emprestimoNaoDevolvidoExistente = livro.getEmprestimos().stream()
                .anyMatch(emprestimo -> emprestimo.getLeitor().getId() == leitorId && !emprestimo.isDevolvido());

        if (emprestimoNaoDevolvidoExistente) {
            throw new IllegalStateException("Leitor já possui um empréstimo não devolvido para este livro");
        }

        // Verificar se o leitor possui uma reserva para este livro
        Optional<Reserva> reservaOptional = livro.getReservas().stream()
                .filter(reserva -> reserva.getLeitor().getId() == leitorId
                        && reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .findFirst();

        if (reservaOptional.isPresent()) {
            // Marcar a reserva como atendida
            Reserva reserva = reservaOptional.get();
            reserva.setStatus(StatusReserva.ATENDIDA);
            reservaRepository.save(reserva);

            // Garantir o empréstimo ao leitor
            if (leitor.getQuantidadeEmprestimosRestantes() == 0) {
                throw new LimiteExcedidoException("Limite de empréstimos excedido para o leitor");
            }

            livro.emprestarLivro();
            livroRepository.save(livro);

            // Criar e salvar o empréstimo
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setLivro(livro);
            emprestimo.setLeitor(leitor);
            emprestimo.setDataEmprestimo(LocalDate.now());
            emprestimo.setDevolvido(false);
            emprestimo.setDataLimite(LocalDate.now());
            return emprestimoRepository.save(emprestimo);

        } else {
            // Caso não exista uma reserva, verificar disponibilidade e seguir o fluxo
            // normal

            // Verificar se há cópias disponíveis
            if (livro.getNumeroCopiasDisponiveis() == 0) {
                throw new LivroIndisponivelException("Livro não está disponível para empréstimo");
            }

            // Verificar a quantidade de reservas em andamento
            long reservasEmAndamento = livro.getReservas().stream()
                    .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                    .count();

            if (reservasEmAndamento >= livro.getNumeroCopiasDisponiveis()) {
                throw new LivroIndisponivelException(
                        "Todas as cópias do livro estão reservadas e não estão disponíveis para empréstimo.");
            }

            // Verificar limite de empréstimos do leitor
            if (leitor.getQuantidadeEmprestimosRestantes() == 0) {
                throw new LimiteExcedidoException("Limite de empréstimos excedido para o leitor");
            }

            livro.emprestarLivro();
            livroRepository.save(livro);

            // Criar e salvar o empréstimo
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setLivro(livro);
            emprestimo.setLeitor(leitor);
            emprestimo.setDataEmprestimo(LocalDate.now());
            emprestimo.setDevolvido(false);
            emprestimo.setDataLimite(LocalDate.now().plusDays(14)); // Define a data limite do empréstimo (ex: 14 dias)
            return emprestimoRepository.save(emprestimo);
        }
    }

    @Transactional
    public Emprestimo renovarEmprestimo(int livroId, int leitorId, String senha) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        if (!usuarioService.verificarSenha(leitor.getUsuario().getId(), senha)) {
            throw new AccessDeniedException("Senha incorreta");
        }

        // Verificar se há um empréstimo não devolvido para o livro e leitor
        Emprestimo emprestimo = emprestimoRepository.findByLivroAndLeitorAndDevolvidoFalse(livro, leitor)
                .orElseThrow(() -> new EntityNotFoundException("Não há um empréstimo ativo para este livro e leitor"));

        // Verificar se a renovação está sendo solicitada na data limite do empréstimo
        if (!emprestimo.getDataLimite().equals(LocalDate.now())) {
            throw new DataRenovacaoException("A renovação só pode ser feita na data limite do empréstimo");
        }

        // Verificar se o número máximo de renovações foi atingido
        if (emprestimo.getQuantidadeRenovacoesRestantes() == 0) {
            throw new LimiteExcedidoException("Limite máximo de renovações atingido");
        }

        // Verificar se há reservas em espera para o livro
        boolean reservasEmEsperaExistem = livro.getReservas().stream()
                .anyMatch(reserva -> reserva.getStatus() == StatusReserva.EM_ESPERA);

        if (reservasEmEsperaExistem) {
            throw new ReservaEmEsperaException(
                    "Não é possível renovar o empréstimo, pois há reservas em espera para este livro");
        }

        emprestimo.renovar();
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolverLivro(int emprestimoId, String senha) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

        if (!usuarioService.verificarSenha(emprestimo.getLeitor().getUsuario().getId(), senha)) {
            throw new AccessDeniedException("Senha incorreta");
        }

        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimoRepository.save(emprestimo);

        Livro livro = emprestimo.getLivro();
        livro.devolverLivro();
        livroRepository.save(livro);

        // Verificar se há reservas em espera
        Optional<Reserva> reservaEmEsperaMaisAntiga = livro.getReservas().stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ESPERA)
                .min(Comparator.comparing(Reserva::getDataCadastro));

        // Se houver uma reserva em espera, atualizar o status e a dataLimite
        if (reservaEmEsperaMaisAntiga.isPresent()) {
            Reserva reserva = reservaEmEsperaMaisAntiga.get();
            reserva.marcarComoEmAndamento();
            reservaRepository.save(reserva);
        }

        return emprestimo;
    }
}
