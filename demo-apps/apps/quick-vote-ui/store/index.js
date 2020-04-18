import { applyMiddleware, createStore } from 'redux';
import thunkMiddleware from 'redux-thunk';
import { createLogger } from 'redux-logger';
import { composeWithDevTools } from 'redux-devtools-extension'

import rootReducer from '../reducers';

export default function configureStore(preloadedState) {
    const middlewares = [createLogger(), thunkMiddleware];
    const middlewareEnhancer = applyMiddleware(...middlewares);
    const enhancers = [middlewareEnhancer];
    const composedEnhancers = composeWithDevTools(...enhancers);
    const store = createStore(rootReducer, preloadedState, composedEnhancers);
    return store;
}