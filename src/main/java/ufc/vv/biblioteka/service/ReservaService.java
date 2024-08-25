package ufc.vv.biblioteka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.LivroIndisponivelException;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;
import ufc.vv.biblioteka.repository.EmprestimoRepository;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.repository.ReservaRepository;
import java.time.LocalDate;

@Service
public class ReservaService {

    private EmprestimoRepository emprestimoRepository;

    private LivroRepository livroRepository;

    private LeitorRepository leitorRepository;

    private ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(EmprestimoRepository emprestimoRepository,
            LivroRepository livroRepository,
            LeitorRepository leitorRepository, ReservaRepository reservaRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Reserva reservarLivro(int livroId, int leitorId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        // Verificar limite de empréstimos do leitor
        if (leitor.getLimiteReservas() == 0) {
            throw new LimiteExcedidoException("Limite de reservas excedido para o leitor");
        }

        // Verificar a quantidade de reservas em andamento
        long reservasEmAndamento = livro.getReservas().stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .count();

        Reserva reserva = new Reserva();
        reserva.setLivro(livro);
        reserva.setLeitor(leitor);
        reserva.setDataCadastro(LocalDate.now());
       
        if (reservasEmAndamento < livro.getNumeroCopias()) {
            reserva.marcarComoEmAndamento();
        } else {
            reserva.marcarComoEmEspera();
        }

        livro.emprestarLivro();
        livroRepository.save(livro);

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