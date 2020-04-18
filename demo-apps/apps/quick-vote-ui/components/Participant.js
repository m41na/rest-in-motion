import { useState } from 'react';

export default function Participant({ player: { participants }, searchInvited, joinSession }) {

  const [scrum, setScrum] = useState({ email: "", error: false });
  const { email, error } = scrum;

  const handleChange = (e) => {
    setScrum({ ...scrum, [e.target.id]: e.target.value, error: false });
  }

  const handleSelect = (name) => {
    joinSession({name, email})
  }

  const handleSearchTeam = () => {
    if (!email) {
      setScrum({ ...scrum, error: true })
    }
    else {
      searchInvited(email);
    }
  }

  return (
    <div className="card">
      <div className="card-header font-weight-bold">
        Join Scrum
        </div>
      <div className="card-body">
        <form className="organize-form" action="/organize">
          <div className="form-group">
            <div className="input-group mb-3">
              <input type="email" className="form-control" placeholder="Organizer's email" aria-label="Organizer's email" id="email" value={email} onChange={handleChange} />
              <div className="input-group-append">
                <span className="input-group-text cursor-pointer" id="basic-addon2" onClick={handleSearchTeam}>Search</span>
              </div>
            </div>
            {error ? <div className="error">* This is a required field</div> : null}
          </div>
          <div className="participants-grid">
            {participants.map(participant => (
              <div key={participant} className="participant-item"><button type="button" className="btn btn-lg btn-outline-secondary" onClick={() => handleSelect(participant)}>{participant} <i className="fas fa-user"></i></button></div>
            ))}
          </div>
        </form>
      </div>
    </div>
  );
}