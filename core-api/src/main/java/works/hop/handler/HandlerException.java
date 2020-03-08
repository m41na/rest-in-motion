package works.hop.handler;

public class HandlerException extends RuntimeException {

    public final int status;

    public HandlerException(int status, String message) {
        super(message);
        this.status = status;
    }

    public HandlerException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
