import Home from './Home';
import Register from './Register';
import Teams from './Teams';
import Organize from './Organize';
import Scrummage from './Scrummage';
import Participant from './Participant';
import Participate from './Participate';
import Alert from '../containers/Alert';

const Page = ({ home: { current, showAlert }, auth, votes, scrum, teams, changePage, login, register, organize, player, selectTeam, registerTeam, searchInvited, joinSession, submitVote, lockScrum, revealVotes }) => {
    return (
        <>
            {current == "home" ? <Home changePage={changePage} login={login} /> : null}
            {current == "register" ? <Register register={register} /> : null}
            {current == "teams" ? <Teams auth={auth} teams={teams} selectTeam={selectTeam} registerTeam={registerTeam} /> : null}
            {current == "organize" ? <Organize auth={auth} scrum={scrum} teams={teams} organize={organize}  /> : null}
            {current == "scrummage" ? <Scrummage scrum={scrum} votes={votes} lockScrum={lockScrum} revealVotes={revealVotes} /> : null}
            {current == "participant" ? <Participant player={player} searchInvited={searchInvited} joinSession={joinSession} /> : null}
            {current == "participate" ? <Participate player={player} submitVote={submitVote} /> : null}
            {showAlert? <Alert /> : null}
        </>
    )
};

export default Page;