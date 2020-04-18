import { UPDATE_TEAMS } from "../actions/actionType";

const initialState = []

const teamsReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_TEAMS:
            return [...state, ...action.teams]
        default:
            return state;
    }
}

export default teamsReducer;