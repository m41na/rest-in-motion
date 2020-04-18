import { ORGANIZE } from "../actions/actionType";

const initialState = {}

const scrumReducer = (state = initialState, action) => {
    switch (action.type) {
        case ORGANIZE:
            console.log(JSON.stringify(action))
            return {...state}
        default:
            return state;
    }
}

export default scrumReducer;