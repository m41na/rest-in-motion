import { useState } from 'react';

export default function Scrummage({scrum, votes, lockScrum, revealVotes}) {

    const [scrummage, setScrummage] = useState({ locked: false, reveal: false, participants: scrum.participants });
    const { locked, reveal, participants } = scrummage;
    const handleChange = (e) => {
        setScrummage({ ...scrummage, [e.target.id]: e.target.value });
    }

    return (
        <div className="card">
            <div className="card-header font-weight-bold">
                Scrummage
        </div>
            <div className="card-body">
                <form className="scrum-form" action="/scrum">
                    <div className="participants-grid">
                        {participants.map(participant => (
                            <div key={participant} className="participant-item"><button type="button" className="btn btn-lg btn-outline-secondary">{participant} <i className="fas fa-user"></i></button></div>
                        ))}
                    </div>
                    <div className="scrum-buttons mt-5" role="group" aria-label="Scrum Control">
                        <button type="button" className="btn btn-secondary" onClick={lockScrum}>Lock</button>
                        <button type="button" className="btn btn-secondary" onClick={revealVotes}>Reveal</button>
                    </div>
                </form>
            </div>
        </div>
    );
}