package works.hop.reducer.persist;

import java.util.List;

public interface Crud {

    List<RecordValue> fetch(RecordKey key); //retrieve user's named collection

    RecordValue fetch(long id); //retrieve a record by its id

    long save(RecordEntity record); //add to user's collection

    int update(RecordEntity record);  //update in user's collection

    int delete(RecordKey key); //remove from user's collection
}
