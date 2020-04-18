import { CHANGE_PAGE, REGISTER, LOGIN, LOGOUT, ORGANIZE, UPDATE_TEAMS, SHOW_ALERT, HIDE_ALERT, LOCK_SCRUM, 
    REVEAL_VOTES, UPDATE_INVITED, JOIN_SESSION, SUBMIT_VOTE } from './actionType';

export function changePageAction(page) {
    return { type: CHANGE_PAGE, page }
}

export function registerAction(user) {
    return { type: REGISTER, user }
}

export function loginAction(user) {
    return { type: LOGIN, user }
}

export function logoutAction() {
    return { type: LOGOUT }
}

export function organizeAction(team) {
    return { type: ORGANIZE, team }
}

export function updateTeamsAction(teams){
    return {type: UPDATE_TEAMS, teams}
}

export function showAlertAction(alert) {
    return { type: SHOW_ALERT, alert }
}

export function hideAlertAction() {
    return { type: HIDE_ALERT }
}

export function lockScrumAction() {
    return { type: LOCK_SCRUM }
}

export function revealVotesAction() {
    return { type: REVEAL_VOTES }
}

export function updateInvitedAction({ choices, participants }) {
    return { type: UPDATE_INVITED, choices, participants }
}

export function joinSessionAction(name, email) {
    return { type: JOIN_SESSION, name, email }
}

export function submitVoteAction(vote) {
    return { type: SUBMIT_VOTE, vote }
}