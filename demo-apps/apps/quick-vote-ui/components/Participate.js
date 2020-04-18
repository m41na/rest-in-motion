import { useState } from 'react';

export default function Participant({player: {organizer, name, choices}, submitVote}) {

  const [scrum, setScrum] = useState({ selected: "" });
  const { selected } = scrum;

  const handleSubmit = (e) => {
    e.preventDefault();
    submitVote({name, selected, email: organizer})
  }

  const handleSelect = (selected) => {
    setScrum({...scrum, selected})
  }

  return (
    <div className="card">
      <div className="card-header font-weight-bold">
        Participating as {name}
        </div>
      <div className="card-body">
        <form className="selection-form" action="/vote">
          <div className="selection-row">
            <div className="options">
              <label htmlFor="choices">Select Value</label>
              <select size="5" className="form-control" id="choices">
                {choices && choices.map(choice => <option key={choice} value={choice} onClick={() => handleSelect(choice)}>{choice}</option>)}
              </select>
            </div>
            <div className="selected">
              <label htmlFor="selectedChoice">Selected</label>
              <textarea className="form-control" id="selectedChoice" rows="5" readOnly value={selected}></textarea>
            </div>
          </div>

          <div className="button-row">
            <button type="button" className="btn btn-secondary btn-lg btn-block" onClick={handleSubmit}>Vote</button>
          </div>
        </form>
      </div>
    </div>
  );
}