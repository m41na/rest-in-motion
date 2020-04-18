import { useState } from 'react';

export default function Register({register}) {

    const emailPattern = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/
    const [organizer, setOrganizer] = useState({ firstName: "", lastName: "", emailAddress: "", emailAddressError: "", phoneNumber: "" });
    const { firstName, lastName, emailAddress, emailAddressError, phoneNumber } = organizer;
    const handleChange = (e) => {
        if(emailAddressError && "emailAddress" === e.target.id){
            setOrganizer({...organizer, emailAddress: e.target.value, emailAddressError: ""});
        }
        else{
            setOrganizer({ ...organizer, [e.target.id]: e.target.value });
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if(!emailAddress){
            setOrganizer({...organizer, emailAddressError: '* Email is a required field'});
            return;
        }
        if(!emailPattern.test(emailAddress)){
            setOrganizer({...organizer, emailAddressError: '* Email value is not valid'});
            return;
        }
        register(organizer);
    }

    return (
        <div className="card">
            <div className="card-header font-weight-bold">
                Registration
          </div>
            <div className="card-body">
                <form className="organizer-form" action="/organizer">
                    <div className="form-group">
                        <label htmlFor="emailAddress">Email Address</label>
                        <input type="email" className="form-control" id="emailAddress" pattern={emailPattern} value={emailAddress} onChange={handleChange} />
                        {emailAddressError? <div className="error">{emailAddressError}</div> : null}
                    </div>
                    <div className="form-group">
                        <label htmlFor="firstName">First Name</label>
                        <input type="text" className="form-control" id="firstName" value={firstName} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="lastName">Last Name</label>
                        <input type="text" className="form-control" id="lastName" value={lastName} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="phoneNumber">Phone #</label>
                        <input type="text" className="form-control" id="phoneNumber" value={phoneNumber} onChange={handleChange} />
                    </div>
                    <button type="submit" className="btn btn-primary" onClick={handleSubmit}>Submit</button>
                </form>
            </div>
        </div>
    );
}
