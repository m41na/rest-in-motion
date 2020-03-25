package works.hop.core;

public class JsonResult<T> {

    private int status;
    private T body;
    private String error;

    public JsonResult() {
        this(0, null);
    }

    public JsonResult(int status, String error) {
        this(status, null, error);
    }

    public JsonResult(T body, int status) {
        this(status, body, null);
    }

    public JsonResult(int status, T body, String error) {
        this.status = status;
        this.body = body;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static <B> JsonResult<B> ok(int status, B body){
        return new JsonResult<B>(status, body, null);
    }

    public static <B> JsonResult<B> ok(B body){
        return new JsonResult<B>(201, body, null);
    }

    public static <B> JsonResult<B> err(int status, String error){
        return new JsonResult<B>(status, null, error);
    }

    public static <B> JsonResult<B> err(String error){
        return new JsonResult<B>(400, null, error);
    }
}
