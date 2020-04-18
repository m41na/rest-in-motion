import {useState} from 'react';
import Link from 'next/link';

const Home = ({ login, changePage }) => {

    const [page, setPage] = useState({email: "", error: false});
    const {email, error} = page;

    const handleChange = (e) => {
        setPage({...page, [e.target.id]: e.target.value, error: false})
    }

    const handleLogin = () => {
        if(!email){
            setPage({...page, error: true})
        }
        else{
            login(email);
        }
    }

    return (
        <div className="join-method">
            <div className="participant">
                <Link href="#/participants"><a className="large" onClick={() => changePage('participant')}>I am a Participant <i className="fas fa-person-booth text-primary"></i></a></Link>
            </div>
            <div className="organizer">
                <div className="input-group mb-3">
                    <input type="email" className="form-control" placeholder="Organizer's email" aria-label="Organizer's email" id="email" value={email} onChange={handleChange} required />
                    <div className="input-group-append">
                        <span className="input-group-text cursor-pointer" id="basic-addon2" onClick={() => handleLogin(email)}>Login</span>
                    </div>
                </div>
                {error? <div className="error">* This is a required field</div> : null}
                <Link href="#/organizer"><a className="small" onClick={() => changePage('register')}>Register as an Organizer instead <i className="fas fa-user-shield text-primary"></i></a></Link>
            </div>
        </div>
    );
}

export default Home;