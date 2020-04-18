import { useState } from 'react';

export default function Organize({ auth, organize, scrum }) {
    const [team, setTeam] = useState({ invitee: "", choice: "", invitees: scrum.participants, inviteesError: "", choices: scrum.choices, choicesError: "" });
    const { invitee, invitees, inviteesError, choice, choices, choicesError } = team;

    const handleChange = (e) => {

    }

    const addInvitee = () => {
        if (invitee && invitee.length > 0 && !invitees.includes(invitee)) {
            const updated = [...invitees, invitee]
            setTeam({ ...team, invitee: "", invitees: updated, inviteesError: "" });
        }
    }
    const dropInvitee = () => {
        if (invitee && invitee.length > 0) {
            const updated = invitees.filter(i => i !== invitee);
            setTeam({ ...team, invitee: "", invitees: updated });
        }
    }
    const selectInvitee = (value) => {
        setTeam({ ...team, invitee: value })
    }
    const addChoice = () => {
        if (choice && choice.length > 0 && !choices.includes(choice)) {
            const updated = [...choices, choice]
            setTeam({ ...team, choice: "", choices: updated, choicesError: "" });
        }
    }
    const dropChoice = () => {
        if (choice && choice.length > 0) {
            const updated = choices.filter(i => i !== choice);
            setTeam({ ...team, choice: "", choices: updated });
        }
    }
    const selectChoice = (value) => {
        setTeam({ ...team, choice: value })
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (invitees.length == 0) {
            setTeam({ ...team, inviteesError: '* Invitees list is required' });
            return;
        }
        if (choices.length == 0) {
            setTeam({ ...team, choicesError: '* Choices list is required' });
            return;
        }

        organize({
            team: { title: (title || defaultTitle), organization: (organization || defaultOrganization) },
            organizer: auth.user, choices, participants: team.invitees
        });
    }

    return (
        <div className="card">
            <div className="card-header font-weight-bold">
                Set up Scrum
                </div>
            <div className="card-body">
                <form className="organize-form" action="/organize">
                    <div className="form-group">
                        <div className="input-group mb-3">
                            <div className="input-group-prepend">
                                <span className="input-group-text"><i className="fas fa-minus-circle" onClick={dropChoice}></i></span>
                            </div>
                            <input type="text" className="form-control" id="choice" aria-label="Add choice" placeholder="Add choice" value={choice} onChange={handleChange} />
                            <div className="input-group-append">
                                <span className="input-group-text"><i className="fas fa-plus-circle" onClick={addChoice}></i></span>
                            </div>
                        </div>
                        <div className="form-group">
                            <label htmlFor="choices">Choices</label>
                            <select size="5" className="form-control" id="choices">
                                {choices.map(choice => <option key={choice} value={choice} onClick={() => selectChoice(choice)}>{choice}</option>)}
                            </select>
                            {choicesError ? <div className="error">{choicesError}</div> : null}
                        </div>
                    </div>
                    <div className="form-group">
                        <div className="input-group mb-3">
                            <div className="input-group-prepend">
                                <span className="input-group-text"><i className="fas fa-minus-circle" onClick={dropInvitee}></i></span>
                            </div>
                            <input type="text" className="form-control" id="invitee" aria-label="Invite particiapnt" placeholder="Add invitee" value={invitee} onChange={handleChange} />
                            <div className="input-group-append">
                                <span className="input-group-text"><i className="fas fa-plus-circle" onClick={addInvitee}></i></span>
                            </div>
                        </div>
                        <div className="form-group">
                            <label htmlFor="invited">Participants</label>
                            <select size="5" className="form-control" id="invitees">
                                {invitees.map(invitee => <option key={invitee} value={invitee} onClick={() => selectInvitee(invitee)}>{invitee}</option>)}
                            </select>
                            {inviteesError ? <div className="error">{inviteesError}</div> : null}
                        </div>
                    </div>
                    <button type="submit" className="btn btn-primary" onClick={handleSubmit}>Save</button>
                </form>
            </div>
        </div>
    );
}