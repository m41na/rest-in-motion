import { CHANGE_PAGE, SHOW_ALERT, HIDE_ALERT } from "../actions/actionType";

const initialState = {}

const homeReducer = (state = initialState, action) => {
    switch (action.type) {
        case CHANGE_PAGE:
            return { ...state, current: action.page };
        case SHOW_ALERT: {
            return { ...state, alert: { ...state.alert, ...action.alert }, showAlert: true }
        }
        case HIDE_ALERT: {
            return { ...state, showAlert: false }
        }
        default:
            return state;
    }
};

export default homeReducer;