package ufc.vv.biblioteka.service;

import java.time.LocalDate;

import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.LivroIndisponivelException;
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

    private EmprestimoRepository emprestimoRepository;

    private LivroRepository livroRepository;

    private LeitorRepository leitorRepository;

    private ReservaRepository reservaRepository;

    @Autowired
    public EmprestimoService(EmprestimoRepository emprestimoRepository,
            LivroRepository livroRepository,
            LeitorRepository leitorRepository, ReservaRepository reservaRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Emprestimo emprestarLivro(int livroId, int leitorId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        // Verificar se há cópias disponíveis
        if (livro.getNumeroCopias() == 0) {
            throw new LivroIndisponivelException("Livro não está disponível para empréstimo");
        }

        // Verificar a quantidade de reservas em andamento
        long reservasEmAndamento = livro.getReservas().stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .count();

        if (reservasEmAndamento >= livro.getNumeroCopias()) {
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
        emprestimo.setDataLimite(LocalDate.now());
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolverLivro(int emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

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
