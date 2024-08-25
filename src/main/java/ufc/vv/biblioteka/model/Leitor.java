package ufc.vv.biblioteka.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Leitor {

    private static final int LIMITE_EMPRESTIMOS = 5;

    private static final int LIMITE_RESERVAS_EM_ANDAMENTO = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String nomeCompleto;

    @NotNull
    @NotBlank
    private String endereco;

    @NotNull
    @NotBlank
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @OneToMany(mappedBy = "leitor", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Emprestimo> emprestimos;

    @OneToMany(mappedBy = "leitor", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Reserva> reservas;

    public int getQuantidadeEmprestimosNaoDevolvidos() {
        if (emprestimos == null)
            throw new IllegalStateException("Lista de emprestimos não pode ser nula");
        return (int) this.emprestimos.stream()
                .filter(emprestimo -> !emprestimo.isDevolvido())
                .count();
    }

    public int getQuantidadeEmprestimosRestantes() {
        int qtdEmprestimosNaoDevolvidos = getQuantidadeEmprestimosNaoDevolvidos();
        int emprestimosRestantes = LIMITE_EMPRESTIMOS - qtdEmprestimosNaoDevolvidos;
        if (emprestimosRestantes < 0)
            throw new IllegalStateException(
                    "Limite de emprestimos não pode ser menor que quantidade de empréstimos não devolvidos");
        return emprestimosRestantes;
    }

    public int getQuantidadeReservasEmAndamento() {
        if (reservas == null)
            throw new IllegalStateException("Lista de reservas não pode ser nula");
        return (int) this.reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .count();
    }

    public int getQuantidadeReservasRestantes() {
        int qtdReservasEmAndamento = getQuantidadeReservasEmAndamento();
        int reservasRestantes = LIMITE_RESERVAS_EM_ANDAMENTO - qtdReservasEmAndamento;
        if (reservasRestantes < 0)
            throw new IllegalStateException(
                    "Limite de reservas em andamento não pode ser menor que quantidade de reservas em andamento");
        return reservasRestantes;
    }
}
