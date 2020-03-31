package works.hop.reducer.persist;

import java.util.List;

public interface Crud<T> {

    List<T> fetchAll(String userKey, String collectionKey);

    int save(String userKey, String collectionKey, T record);

    int update(String userKey, String collectionKey, T record);

    int delete(String userKey, String collectionKey, Long id);
}
