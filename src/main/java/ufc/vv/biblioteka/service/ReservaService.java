package ufc.vv.biblioteka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ufc.vv.biblioteka.exception.LimiteExcedidoException;
import ufc.vv.biblioteka.exception.ReservaNaoPodeMaisSerCancelaException;
import ufc.vv.biblioteka.model.Leitor;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.model.Reserva;
import ufc.vv.biblioteka.model.StatusReserva;
import ufc.vv.biblioteka.repository.LeitorRepository;
import ufc.vv.biblioteka.repository.LivroRepository;
import ufc.vv.biblioteka.repository.ReservaRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

@Service
public class ReservaService {

    private LivroRepository livroRepository;

    private LeitorRepository leitorRepository;

    private ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(LivroRepository livroRepository,
            LeitorRepository leitorRepository, ReservaRepository reservaRepository) {
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
        if (leitor.getQuantidadeReservasRestantes() == 0) {
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

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelarReserva(int reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.EM_ESPERA && reserva.getStatus() != StatusReserva.EM_ANDAMENTO) {
            throw new ReservaNaoPodeMaisSerCancelaException("Esta reserva não pode mais ser cancelada");
        }

        reserva.setStatus(StatusReserva.CANCELADA);

        Livro livro = reserva.getLivro();
        Optional<Reserva> reservaEmEsperaMaisAntiga = livro.getReservas().stream()
                .filter(reservaLivro -> reservaLivro.getStatus() == StatusReserva.EM_ESPERA)
                .min(Comparator.comparing(Reserva::getDataCadastro));

        // Se houver uma reserva em espera, atualizar o status e a dataLimite
        if (reservaEmEsperaMaisAntiga.isPresent()) {
            Reserva reservaEmEspera = reservaEmEsperaMaisAntiga.get();
            reservaEmEspera.marcarComoEmAndamento();
            reservaRepository.save(reservaEmEspera);
        }

        reservaRepository.save(reserva);

        return reserva;
    }
}