const Header = ({home: {current, pages}, changePage}) => (
  <header>
    <nav className="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
      <a className="navbar-brand" href={pages.home} onClick={()=>changePage('home')}>Quick Vote <i className="fas fa-balance-scale"></i></a>
      <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span className="navbar-toggler-icon"></span>
      </button>
      <div className="collapse navbar-collapse" id="navbarCollapse">
        <ul className="navbar-nav mr-auto">
          <li className="nav-item active">
            <a className="nav-link" href={pages.register} onClick={()=>changePage('register')}>Register {current === 'register'? <span className="sr-only">(current)</span> : null}</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href={pages.teams} onClick={()=>changePage('teams')}>Teams  {current === 'teams'? <span className="sr-only">(current)</span> : null}</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href={pages.organize} onClick={()=>changePage('organize')}>Organize  {current === 'organize'? <span className="sr-only">(current)</span> : null}</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href={pages.participants} onClick={()=>changePage('scrummage')}>Scrummage  {current === 'scrummage'? <span className="sr-only">(current)</span> : null}</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href={pages.participant} onClick={()=>changePage('participant')}>Patricipant  {current === 'participant'? <span className="sr-only">(current)</span> : null}</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href={pages.participate} onClick={()=>changePage('participate')}>Participate  {current === 'participate'? <span className="sr-only">(current)</span> : null}</a>
          </li>
        </ul>
      </div>
    </nav>
  </header>
);

export default Header;