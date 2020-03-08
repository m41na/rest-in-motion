package works.hop.core;

public interface BodyWriter<T> {

    byte[] transform(T object);
}
