import React, { useState, useEffect, useReducer } from 'react';
import { Paper, Typography, TextField, Button, List, ListItem, ListItemText, ListItemSecondaryAction, IconButton } from '@material-ui/core';
import Delete from '@material-ui/icons/Delete';
import AddCircle from '@material-ui/icons/AddCircle';
import reducer from './state';
import {fetchTodos, createTodo, updateTodo, removeTodo, acceptEvents} from './service';

function App() {
  const [todos, dispatch] = useReducer(reducer, [])
  const [todo, setTodo] = useState("");
  const [events, setEvents] = useState([])
  
  useEffect(() => {
    fetchTodos(dispatch)
    acceptEvents(event => {
      if(event){
        console.log('event', event)
        setEvents(e => [...e, JSON.parse(event)])
      }
    });
  }, [])

  const handleChange = (e) => {
    setTodo(e.target.value)
  }

  const handleDelete = (todo) => {
    removeTodo(dispatch, todo)
  }

  const addTodo = () => {
    createTodo(dispatch, todo);
    setTodo("")
  }

  const toggleDone = (todo) => {
    updateTodo(dispatch, {...todo, completed: !todo.completed})
  }

  const isCompleted = (completed) => {
    return completed? {textDecoration: 'line-through'} : {textDecoration: 'none'}
  }

  return (
    <div className="App">
      <Paper>
        <Typography variant='h2' align='center' gutterBottom>Todo List</Typography>
        <List>
          {todos.map((todo, i) => <ListItem key={todo.id}>
            <ListItemText primary={todo.task} style={isCompleted(todo.completed)} onClick={() => toggleDone(todo)}/>
            <ListItemSecondaryAction>
              <IconButton color='primary' onClick={() => handleDelete(todo)}><Delete /></IconButton>
            </ListItemSecondaryAction>
          </ListItem >)}
        </List>
        <TextField type="text" id="todo" value={todo} onChange={handleChange}></TextField>
        <Button onClick={addTodo} color="primary" variant="outlined"><AddCircle /></Button>
        <List>
        {events.map((event, i) => <ListItem key={i}>
            <ListItemText primary={JSON.stringify(event)}/>
          </ListItem >)}
        </List>
      </Paper>
    </div>
  );
}

export default App;
