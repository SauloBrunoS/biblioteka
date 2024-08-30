package ufc.vv.biblioteka.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Reserva {

    private static final int PRAZO_RESERVA_ATIVA_EM_DIAS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "leitor_id")
    private Leitor leitor;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCadastro;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataLimite;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusReserva status;

    @OneToOne
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    public void marcarComoEmAndamento() {
        this.status = StatusReserva.EM_ANDAMENTO;
        this.dataLimite = LocalDate.now().plusDays(PRAZO_RESERVA_ATIVA_EM_DIAS);
    }

    public void marcarComoEmEspera() {
        this.status = StatusReserva.EM_ESPERA;
    }

    public void marcarComoCancelada() {
        this.status = StatusReserva.CANCELADA;
    }

}
