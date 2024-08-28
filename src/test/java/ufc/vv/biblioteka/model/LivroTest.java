package ufc.vv.biblioteka.model;

import org.junit.jupiter.api.Test;

import ufc.vv.biblioteka.exception.LivroIndisponivelException;

import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    @Test
    void testaEmprestarLivroComSucesso() {
        Livro livro = new Livro();
        livro.setNumeroCopiasDisponiveis(5);

        livro.emprestarLivro();

        assertEquals(4, livro.getNumeroCopiasDisponiveis());
    }

    @Test
    void testarEmprestarLivroSemCopiasDisponiveis() {
        Livro livro = new Livro();
        livro.setNumeroCopiasDisponiveis(0);

        assertThrows(LivroIndisponivelException.class, livro::emprestarLivro);
    }

    @Test
    void testar() {
        Livro livro = new Livro();
        livro.setNumeroCopiasDisponiveis(0);

        assertThrows(LivroIndisponivelException.class, livro::emprestarLivro);
    }

    @Test
    void testaDefinirNumeroCopiasComValorNegativo() {
        Livro livro = new Livro();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            livro.setNumeroCopiasDisponiveis(-5);
        });

        // Verifica se a mensagem da exceção é a esperada
        assertEquals("Número de cópias não pode ser negativo", exception.getMessage());
    }

}