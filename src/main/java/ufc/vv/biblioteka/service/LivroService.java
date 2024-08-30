package ufc.vv.biblioteka.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.ISBNValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ufc.vv.biblioteka.model.Autor;
import ufc.vv.biblioteka.model.Colecao;
import ufc.vv.biblioteka.model.Livro;
import ufc.vv.biblioteka.repository.AutorRepository;
import ufc.vv.biblioteka.repository.ColecaoRepository;
import ufc.vv.biblioteka.repository.LivroRepository;

@Service
public class LivroService {

    private LivroRepository livroRepository;

    private AutorRepository autorRepository;

    private ColecaoRepository colecaoRepository;

    ISBNValidator isbnValidator = new ISBNValidator();

    @Autowired
    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository,
            ColecaoRepository colecaoRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.colecaoRepository = colecaoRepository;
    }

    public Livro adicionarLivro(Livro livro) {
        validarLivro(livro);
        List<Autor> autores = autorRepository.findAllById(livro.getAutores().stream()
                .map(Autor::getId)
                .collect(Collectors.toList()));
        livro.setAutores(autores);

        List<Colecao> colecoes = colecaoRepository.findAllById(livro.getColecoes().stream()
                .map(Colecao::getId)
                .collect(Collectors.toList()));
        livro.setColecoes(colecoes);
        return livroRepository.save(livro);
    }

    public Livro atualizarLivro(int id, Livro livro) {
        if (!livroRepository.existsById(id)) {
            throw new EntityNotFoundException("Livro não encontrado.");
        }
        validarLivro(livro);
        livro.setId(id);
        List<Autor> autores = autorRepository.findAllById(livro.getAutores().stream()
                .map(Autor::getId)
                .collect(Collectors.toList()));
        livro.setAutores(autores);

        List<Colecao> colecoes = colecaoRepository.findAllById(livro.getColecoes().stream()
                .map(Colecao::getId)
                .collect(Collectors.toList()));
        livro.setColecoes(colecoes);
        Livro savedLivro = livroRepository.save(livro);
        return savedLivro;
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

        if (livro.getNumeroCopiasDisponiveis() > livro.getNumeroCopiasTotais()) {
            throw new IllegalArgumentException(
                    "Número de cópias totais não pode ser menor que número de cópias disponíveis.");
        }

        if (livro.getQtdPaginas() < 0) {
            throw new IllegalArgumentException("Número de páginas não pode ser menor que 1.");
        }
    }

}