package works.hop.reducer.persist;

import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

public class JdbcReducer<S> extends AbstractReducer<S> {

    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String FETCH_BY_ID = "FETCH_BY_ID";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";

    public JdbcReducer(String name, State<S> state) {
        super(name, state);
    }

    @Override
    public S reduce(State<S> state, Action action) {
        switch (action.getType().get()) {
            case CREATE_RECORD:
                RecordEntity toCreate = (RecordEntity) action.getBody();
                ((JdbcState) state).save(toCreate);
                return state.apply(toCreate.getKey().getUserKey(), toCreate.getKey().getCollectionKey());
            case DELETE_RECORD:
                RecordKey toDelete = (RecordKey) action.getBody();
                ((JdbcState) state).delete(toDelete);
                return state.apply(toDelete.getUserKey(), toDelete.getCollectionKey());
            case UPDATE_RECORD:
                RecordEntity toUpdate = (RecordEntity) action.getBody();
                ((JdbcState) state).update(toUpdate);
                return state.apply(toUpdate.getKey().getUserKey(), toUpdate.getKey().getCollectionKey());
            default:
                return null;
        }
    }

    @Override
    public Object compute(Action action) {
        switch (action.getType().get()) {
            case FETCH_ALL:
                RecordKey toFetch = (RecordKey) action.getBody();
                return this.state().apply(toFetch.getUserKey(), toFetch.getCollectionKey());
            case FETCH_BY_ID:
                long fetchId = (long) action.getBody();
                return ((JdbcState) this.state()).fetch(fetchId);
            default:
                return null;
        }
    }
}