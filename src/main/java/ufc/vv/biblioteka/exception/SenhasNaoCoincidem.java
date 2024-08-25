package ufc.vv.biblioteka.exception;

public class SenhasNaoCoincidem extends RuntimeException {
    public SenhasNaoCoincidem(String message) {
        super(message);
    }
}