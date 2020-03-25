package works.hop.handler;

public class BasicHandlerPromise extends HandlerPromise {

    public BasicHandlerPromise(OnSuccessPromise onSuccess, OnFailurePromise onFailure) {
        this.OnSuccess(onSuccess);
        this.OnFailure(onFailure);
    }
}
