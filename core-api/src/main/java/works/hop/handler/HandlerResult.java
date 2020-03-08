package works.hop.handler;

public class HandlerResult {

    private final Long startInMillis;
    private Boolean success = Boolean.TRUE;

    public HandlerResult() {
        this.startInMillis = System.currentTimeMillis();
    }

    public Long duration() {
        return System.currentTimeMillis() - this.startInMillis;
    }

    public Boolean isSuccess() {
        return this.success;
    }

    public void succeeded(Boolean status) {
        this.success = status;
    }
}
