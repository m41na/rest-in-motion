import {FETCH_TODOS, CREATE_TODO, UPDATE_TODO, REMOVE_TODO} from './actionType';

const reducer = (state, action) => {
  switch (action.type) {
    case FETCH_TODOS: return action.todos;
    case CREATE_TODO: return action.todos;
    case REMOVE_TODO: return action.todos;
    case UPDATE_TODO: return action.todos;
    default: throw new Error('Unexpected action');
  }
};

export default reducer;