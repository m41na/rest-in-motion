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

    public HandlerResult succeeded() {
        this.success = Boolean.TRUE;
        return this;
    }

    public HandlerResult failed() {
        this.success = Boolean.FALSE;
        return this;
    }
}
