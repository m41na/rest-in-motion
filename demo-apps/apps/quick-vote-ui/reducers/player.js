import { UPDATE_INVITED, JOIN_SESSION } from "../actions/actionType";

const initialState = {}

const playerReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_INVITED:
            return {...state, choices: action.choices, participants: action.participants};
        case JOIN_SESSION:
            return {...state, name: action.name, organizer: action.email};
        default:
            return state;
    }
}

export default playerReducer;