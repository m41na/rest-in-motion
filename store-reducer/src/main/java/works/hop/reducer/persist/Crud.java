package works.hop.reducer.persist;

import java.util.List;

public interface Crud<T extends RecordValue> {

    List<T> fetch(RecordKey key); //retrieve user's named collection

    long save(RecordKey key, T record); //add to user's collection

    int update(T record);  //update in user's collection

    int delete(Long id); //remove from user's collection
}
