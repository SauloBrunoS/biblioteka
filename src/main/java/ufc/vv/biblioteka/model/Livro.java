package ufc.vv.biblioteka.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ufc.vv.biblioteka.exception.LivroIndisponivelException;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import java.time.LocalDate;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String titulo;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "livro_autor", joinColumns = @JoinColumn(name = "livro_id"), inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private List<Autor> autores;

    @Column(unique = true)
    @NotNull
    @NotBlank
    private String isbn;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPublicacao;

    private int numeroCopiasDisponiveis;

    private int numeroCopiasTotais;

    private int qtdPaginas;

    @OneToMany(mappedBy = "livro", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Emprestimo> emprestimos;

    @OneToMany(mappedBy = "livro", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Reserva> reservas;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "livro_colecao", joinColumns = @JoinColumn(name = "livro_id"), inverseJoinColumns = @JoinColumn(name = "colecao_id"))
    private List<Colecao> colecoes;

    public void emprestarLivro() {
        if (numeroCopiasDisponiveis > 0) {
            numeroCopiasDisponiveis--;
        } else {
            throw new LivroIndisponivelException("O livro não está disponível para empréstimo.");
        }
    }

    public void devolverLivro() {
        if (numeroCopiasDisponiveis < 0) {
            throw new IllegalArgumentException("O número de cópias não pode ser negativo");
        }
        numeroCopiasDisponiveis++;
    }
}
