import {FETCH_TODOS, CREATE_TODO, REMOVE_TODO, UPDATE_TODO} from './actionType';
const fetch = require('node-fetch');

const url = "http://localhost:8090";
const user = 'steve';

const fetchTodos = (dispatch) =>{
    fetch(`${url}/${user}`)
    .then(res => res.json())
    .then(data => {
        dispatch({type: FETCH_TODOS, todos: data.body[user].TODO_LIST})
    })
    .catch(err => {
        console.log(err);
    })
}

const createTodo = (dispatch, todo) => {
    fetch(`${url}/${user}`, { 
        method: 'POST',
        body:    JSON.stringify({id: "", task: todo, completed: false}),
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },})
    .then(res => res.json())
    .then(data => {
        dispatch({type: CREATE_TODO, todos: data.body[user].TODO_LIST})
    })
    .catch(err => {
        console.log(err);
    })
}

const updateTodo = (dispatch, todo) => {
    fetch(`${url}/${user}`, { 
        method: 'PUT',
        body:    JSON.stringify(todo),
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },})
    .then(res => res.json())
    .then(data => {
        dispatch({type: UPDATE_TODO, todos: data.body[user].TODO_LIST})
    })
    .catch(err => {
        console.log(err);
    })
}

const removeTodo = (dispatch, todo) => {
    fetch(`${url}/${user}/${todo.id}`, { method: 'DELETE'})
    .then(res => res.json())
    .then(data => {
        dispatch({type: REMOVE_TODO, todos: data.body[user].TODO_LIST})
    })
    .catch(err => {
        console.log(err);
    })
}

const acceptEvents = (onEvent) => {
    const evtSource = new EventSource(`${url}/sse`, { withCredentials: true });
    evtSource.onmessage = event => {
        onEvent(event.data);
    }
}

export {
    fetchTodos,
    createTodo,
    updateTodo,
    removeTodo,
    acceptEvents,
}