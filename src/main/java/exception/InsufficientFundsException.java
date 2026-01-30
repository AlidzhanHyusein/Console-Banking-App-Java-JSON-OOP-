package exception;

import jdk.jfr.Experimental;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String ex) {
        super(ex);
    }
}
