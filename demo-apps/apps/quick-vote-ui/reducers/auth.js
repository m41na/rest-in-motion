import {LOGIN, LOGOUT} from '../actions/actionType';

const initialState = {};

const authReducer = (state = initialState, action ) => {
    switch(action.type){
        case LOGIN:
            return {...state, signedIn: action.signedIn, user: action.user.user, displayName: action.displayName};
        case LOGOUT:
            return {...state, signedIn: false, user: null, displayName: ""}
        default:
            return state;
    }
}

export default authReducer;