package ufc.vv.biblioteka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.JoinColumn;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Colecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String nome;

    @NotNull
    @NotBlank
    private String descricao;

    @ManyToMany
    @JoinTable(name = "colecao_livro", joinColumns = @JoinColumn(name = "colecao_id"), inverseJoinColumns = @JoinColumn(name = "livro_id"))
    private List<Livro> livros;

}
