package works.hop.handler;

public class HandlerResult {

    private final Long startInMillis;
    private Boolean success;

    public HandlerResult() {
        this(Boolean.TRUE);
    }

    public HandlerResult(Boolean success) {
        this.success = success;
        this.startInMillis = System.currentTimeMillis();
    }

    public static HandlerResult ok() {
        return new HandlerResult();
    }

    public static HandlerResult fail() {
        return new HandlerResult(Boolean.FALSE);
    }

    public Long duration() {
        return System.currentTimeMillis() - this.startInMillis;
    }

    public Boolean success() {
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
