package ufc.vv.biblioteka.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Emprestimo {

    private static final int DATA_LIMITE_PRAZO_DEVOLUCAO_EM_DIAS = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "leitor_id", nullable = false)
    private Leitor leitor;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataLimite;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataDevolucao;

    private boolean devolvido;

    private double valorBase;

    private double multa;

    public void setDataDevolucao(LocalDate dataDevolucao) {
        if (dataDevolucao == null)
            throw new IllegalArgumentException("Data não pode ser nula");
        this.dataDevolucao = dataDevolucao;
        this.devolvido = true;
        this.valorBase = EmprestimoUtils.calcularValorBase(dataEmprestimo, dataLimite, dataDevolucao);
        this.multa = EmprestimoUtils.calcularMulta(dataDevolucao, dataLimite);
    }

    public double calcularValorTotal() {
        return EmprestimoUtils.calcularValorTotal(valorBase, multa);
    }

    public void setDataLimite(LocalDate dataAtual) {
        if (dataLimite == null)
            throw new IllegalArgumentException("Data de hoje não pode ser nula");
        this.dataLimite = dataAtual.plusDays(DATA_LIMITE_PRAZO_DEVOLUCAO_EM_DIAS);
    }
}
