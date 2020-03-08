package works.hop.core;

public interface BodyReader<T> {

    T transform(String type, byte[] bytes);
}
