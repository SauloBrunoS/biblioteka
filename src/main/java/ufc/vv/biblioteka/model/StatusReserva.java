package ufc.vv.biblioteka.model;

public enum StatusReserva {
    ATENDIDA, // A reserva foi atendida e o livro foi retirado pelo leitor
    EXPIRADA, // A reserva expirou e o livro não foi retirado a tempo
    EM_ANDAMENTO, // A reserva está ativa e aguardando o livro ficar disponível
    EM_ESPERA, // O livro ainda não foi devolvido por outro leitor
    CANCELADA // O leitor cancelou a reserva 
}