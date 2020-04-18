import { useState } from 'react';

export default function Teams({auth, selectTeam, teams}) {

  const [team, setTeam] = useState({ title: "", titleError: "", organization: "", organizationError: "" });
  const [edits, setEdits] = useState({ editTitle: false, editOrganization: false });
  const [options, setOptions] = useState({ titles: [...new Set(teams.map(t => t.title))], organizations: [...new Set(teams.map(t => t.organization))] })
  const { title, titleError, organization, organizationError } = team;
  const { editTitle, editOrganization } = edits;
  const { titles, organizations } = options;

  const handleNext = (e) => {
    e.preventDefault();
    selectTeam(team);
  }

  const handleChange = (e) => {
    if (titleError && "title" === e.target.id) {
      setTeam({ ...team, title: e.target.value, titleError: "" });
    }
    else if (organizationError && "organization" === e.target.id) {
      setTeam({ ...team, organization: e.target.value, organizationError: "" });
    }
    else {
      setTeam({ ...team, [e.target.id]: e.target.value });
    }
  }
  const onEditTitle = () => {
    setEdits({ ...edits, editTitle: true })
  }
  const onCancelEditTitle = () => {
    setEdits({ ...edits, editTitle: false })
  }
  const applyEditTitle = () => {
    setEdits({ ...edits, editTitle: false })
    setOptions({ ...options, titles: [...options.titles, title] })
  }
  const selectTitle = (title) => {
    console.log('selecting title', title)
    setTeam({ ...team, title })
  }
  const onEditOrganization = () => {
    setEdits({ ...edits, editOrganization: true })
  }
  const onCancelEditOrganization = () => {
    setEdits({ ...edits, editOrganization: false })
  }
  const applyEditOrganization = () => {
    setEdits({ ...edits, editOrganization: false })
    setOptions({ ...options, organizations: [...options.organizations, organization] })
  }
  const selectOrganization = (organization) => {
    console.log('selecting organization', organization)
    setTeam({ ...team, organization })
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title && !defaultTitle) {
      setTeam({ ...team, titleError: '* Title is a required field' });
      return;
    }
    if (!organization && !defaultOrganization) {
      setTeam({ ...team, organizationError: '* Organization is a required field' });
      return;
    }
  }

  //set default title and organization of the values exist
  const defaultTitle = (titles && titles.length > 0) ? titles[0] : "";
  const defaultOrganization = (organizations && organizations.length > 0) ? organizations[0] : "";

  return (
    <div className="card">
      <div className="card-header font-weight-bold">
        Select Team
        </div>
      <div className="card-body">
        <form className="teams-form" action="/organize/team">
          <div className="form-group">
            <label htmlFor="title">Title</label>
            {editTitle ?
              (<div className="input-group mb-3">
                <div className="input-group-prepend">
                  <span className="input-group-text"><i className="fas fa-times cursor-pointer" onClick={onCancelEditTitle}></i></span>
                </div>
                <input type="text" className="form-control" aria-label="Title" id="title" value={title} onChange={handleChange} />
                <div className="input-group-append">
                  <span className="input-group-text"><i className="fas fa-save cursor-pointer" onClick={applyEditTitle}></i></span>
                </div>
              </div>) :
              (<div className="input-group mb-3">
                <select className="custom-select" id="title" defaultValue={title}>
                  {titles && titles.map(optTitle => <option key={optTitle} value={optTitle} onClick={() => selectTitle(optTitle)}>{optTitle}</option>)}
                </select>
                <div className="input-group-append">
                  <label className="input-group-text" htmlFor="titleText"><i className="fas fa-edit cursor-pointer" onClick={onEditTitle}></i></label>
                </div>
              </div>)
            }
            {titleError ? <div className="error">{titleError}</div> : null}
          </div>
          <div className="form-group">
            <label htmlFor="organization">Organization</label>
            {editOrganization ?
              (<div className="input-group mb-3">
                <div className="input-group-prepend">
                  <span className="input-group-text"><i className="fas fa-times cursor-pointer" onClick={onCancelEditOrganization}></i></span>
                </div>
                <input type="text" className="form-control" aria-label="Title" id="organization" value={organization} onChange={handleChange} />
                <div className="input-group-append">
                  <span className="input-group-text"><i className="fas fa-save cursor-pointer" onClick={applyEditOrganization}></i></span>
                </div>
              </div>) :
              (<div className="input-group mb-3">
                <select className="custom-select" id="organization" defaultValue={organization}>
                  {organizations && organizations.map(optOrg => <option key={optOrg} value={optOrg} onClick={() => selectOrganization(optOrg)}>{optOrg}</option>)}
                </select>
                <div className="input-group-append">
                  <label className="input-group-text" htmlFor="titleText"><i className="fas fa-edit cursor-pointer" onClick={onEditOrganization}></i></label>
                </div>
              </div>)
            }
            {organizationError ? <div className="error">{organizationError}</div> : null}
          </div>
          <button type="submit" className="btn btn-primary" onClick={handleNext}>Save</button>
        </form>
      </div>
    </div>
  );
}