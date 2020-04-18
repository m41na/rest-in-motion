import { connect } from 'react-redux';
import Page from '../components/Page';
import { changePageAction, registerAction, loginAction, updateTeamsAction, organizeAction, lockScrumAction, 
    revealVotesAction, updateInvitedAction, showAlertAction, joinSessionAction } from '../actions/home';
import {get, post, put, del} from '../services'
import sock from '../services/sockio';

const onRegisterAction = async (dispatch, user) => {
    try {
        const json = await post("/organizer", user);
        //console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(registerAction(json))
            dispatch(changePageAction('organize'))
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onLoginAction = async (dispatch, email) => {
    try {
        const json = await post("/organizer/login", {email});
        //console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            const body = json.body;
            dispatch(loginAction(body.auth))
            dispatch(updateTeamsAction(body.teams))
            dispatch(changePageAction('teams'))
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onTeamsAction = async (dispatch, team) => {

}

const onSelectTeam = async (dispatch, team) => {
    dispatch(changePageAction('organize'))
}

const onOrganizeAction = async (dispatch, scrum) => {
    try {
        const json = await post("/organizer/scrum", scrum);
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            const body = json.body;
            dispatch(organizeAction(body))
            dispatch(changePageAction('scrummage'))
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onLockScrumAction = async(dispatch) => {
    try {
        const json = await put("/organizer/lock");
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(lockScrumAction())
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onRevealVotesAction = async (dispatch) => {
    try {
        const json = await put("/organizer/show");
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(revealVotesAction())
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onSearchInvitedAction = async (dispatch, email) => {
    try {
        const json = await get("/organizer/participants");
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(updateInvitedAction(json.data));
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onJoinSessionAction = async (dispatch, { name, email }) => {
    try {
        const json = await post("/participant/join", team);
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(joinSessionAction(name, email));
            dispatch(changePageAction('participate'))
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const onSubmitVoteAction = async (dispatch, vote) => {
    try {
        const json = await post("/organizer/vote", vote);
        console.log("remote response", json)
        if (json.error) {
            dispatch(showAlertAction({ type: 'error', dismissable: false, message: json.error }))
        }
        else {
            dispatch(showAlertAction({ type: 'success', dismissable: false, message: "vote submitted successfully" }));
        }
    } catch (err) {
        dispatch(showAlertAction({ type: 'error', dismissable: false, message: err }))
    }
}

const mapStateToProps = state => ({
    auth: state.auth,
    home: state.home,
    organizer: state.organizer,
    scrum: state.scrum,
    teams: state.teams,
    votes: state.votes,
    player: state.player,
});

const mapDispatchToProps = dispatch => {
    return {
        changePage: page => dispatch(changePageAction(page)),
        register: user => onRegisterAction(dispatch, user),
        registerTeam: team => onTeamsAction(dispatch, team),
        selectTeam: team => onSelectTeam(dispatch, team),
        login: email => onLoginAction(dispatch, email),
        organize: team => onOrganizeAction(dispatch, team),
        lockScrum: () => onLockScrumAction(dispatch),
        revealVotes: () => onRevealVotesAction(dispatch),
        searchInvited: email => onSearchInvitedAction(dispatch, email),
        joinSession: player => onJoinSessionAction(dispatch, player),
        submitVote: vote => onSubmitVoteAction(dispatch, vote),
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Page);