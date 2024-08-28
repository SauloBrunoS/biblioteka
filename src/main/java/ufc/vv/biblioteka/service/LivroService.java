package ufc.vv.biblioteka.service;

import java.time.LocalDate;

import org.apache.commons.validator.routines.ISBNValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.repository.LivroRepository;

@Service
public class LivroService {

    private LivroRepository livroRepository;

    ISBNValidator isbnValidator = new ISBNValidator();

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public Livro adicionarLivro(Livro livro) {
        validarLivro(livro);
        return livroRepository.save(livro);
    }

    public Livro atualizarLivro(int id, Livro livro) {
        if (!livroRepository.existsById(id)) {
            throw new EntityNotFoundException("Livro não encontrado.");
        }
        validarLivro(livro);
        livro.setId(id);
        return livroRepository.save(livro);
    }

    public void excluirLivro(int id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));
        if (!livro.getEmprestimos().isEmpty() || !livro.getReservas().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "O livro não pode ser excluído, pois está associado a empréstimos ou reservas.");
        }
        livroRepository.delete(livro);
    }

    private void validarLivro(Livro livro) {

        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo.");
        }

        if (livro.getTitulo() == null || livro.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("O título do livro não pode ser nulo ou vazio.");
        }

        if (livro.getAutores() == null || livro.getAutores().isEmpty()) {
            throw new IllegalArgumentException("A lista de autores não pode ser nula ou vazia.");
        }

        if (livro.getColecoes() == null || livro.getColecoes().isEmpty()) {
            throw new IllegalArgumentException("A lista de autores não pode ser nula ou vazia.");
        }

        if (livro.getIsbn() == null || livro.getIsbn().isEmpty()) {
            throw new IllegalArgumentException("O ISBN do livro não pode ser nulo ou vazio.");
        }

        if (livro.getSinopse() == null || livro.getSinopse().isEmpty()) {
            throw new IllegalArgumentException("A sinopse do livro não pode ser nula ou vazia.");
        }

        if (!isbnValidator.isValidISBN13(livro.getIsbn()) && !isbnValidator.isValidISBN10(livro.getIsbn())) {
            throw new IllegalArgumentException("O ISBN possui formato inválido.");
        }

        if (livro.getDataPublicacao() == null) {
            throw new IllegalArgumentException("A data de publicação do livro não pode ser nula.");
        }

        if (livro.getDataPublicacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de publicação não pode ser uma data futura.");
        }

        if (livro.getNumeroCopiasDisponiveis() < 0) {
            throw new IllegalArgumentException("Número de cópias disponíves não pode ser negativo.");
        }

        if (livro.getNumeroCopiasTotais() < 0) {
            throw new IllegalArgumentException("Número de cópias totais não pode ser negativo.");
        }

        if (livro.getQtdPaginas() < 0) {
            throw new IllegalArgumentException("Número de páginas não pode ser menor que 1.");
        }
    }

}