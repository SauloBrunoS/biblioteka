package ufc.vv.biblioteka.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmprestimoUtils {
    private static final double TAXA_MULTA_POR_DIA = 2.0;
    private static final double TAXA_EMPRESTIMO_POR_DIA = 1.0;

    public static double calcularMulta(LocalDate dataDevolucao, LocalDate dataLimite) {
        if (dataDevolucao == null || dataLimite == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas.");
        }
        if (dataDevolucao.isAfter(dataLimite)) {
            long diasAtraso = ChronoUnit.DAYS.between(dataLimite, dataDevolucao);
            return diasAtraso * TAXA_MULTA_POR_DIA;
        }
        return 0.0;
    }

    public static double calcularValorBase(LocalDate dataEmprestimo, LocalDate dataLimite, LocalDate dataDevolucao) {
        if (dataEmprestimo == null || dataDevolucao == null || dataLimite == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas.");
        }
        if (dataEmprestimo.isAfter(dataLimite) || dataEmprestimo.isAfter(dataDevolucao)) {
            throw new IllegalArgumentException(
                    "A data de empréstimo não pode ser posterior à data limite ou à data de devolução.");
        }

        LocalDate dataFinal = dataDevolucao.isAfter(dataLimite) ? dataLimite : dataDevolucao;
        long diasEmprestimo = ChronoUnit.DAYS.between(dataEmprestimo, dataFinal);
        return diasEmprestimo * TAXA_EMPRESTIMO_POR_DIA;
    }

    public static double calcularValorTotal(double valorBase, double multa) {
        if (valorBase < 0 || multa < 0) {
            throw new IllegalArgumentException("Valores não podem ser negativos.");
        }
        return valorBase + multa;
    }
}