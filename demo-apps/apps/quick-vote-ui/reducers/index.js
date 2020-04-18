import {combineReducers} from 'redux';
import homeReducer from './home';
import authReducer from './auth';
import scrumReducer from './scrum';
import teamsReducer from './teams';
import playerReducer from './player';
import votesReducer from './votes';
import staticTextReducer from './staticText';

export default combineReducers ({
    home: homeReducer,
    auth: authReducer,
    scrum: scrumReducer,
    teams: teamsReducer,
    player: playerReducer,
    votes: votesReducer,
    staticText: staticTextReducer
});