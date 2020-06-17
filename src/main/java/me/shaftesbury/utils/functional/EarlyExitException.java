package me.shaftesbury.utils.functional;

public class EarlyExitException extends RuntimeException {
    private static final long serialVersionUID = 74664975984990L;

    public EarlyExitException(final String message, final Throwable reason) {
        super(message,reason);
    }
}
