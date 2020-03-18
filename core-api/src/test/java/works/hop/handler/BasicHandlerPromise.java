package works.hop.handler;

public class BasicHandlerPromise extends HandlerPromise {

    public BasicHandlerPromise() {
        this.OnSuccess(new OnSuccessPromise());
        this.OnFailure(new OnFailurePromise());
    }
}
