import staticText from '../staticText.json';

export default {
    auth: {
        signedIn: false,
        user: {
            id: 1,
            firstName: "Steve",
            lastName: "Mikes",
            emailAddress: "steve.mikes@email.com",
            phoneNumber: "569-609-4545",
            dateCreated: "2020-02-03T00:00:00.000+0000"
        },
        displayName: ""
    },
    home: {
        pages: { home: "#/", register: '#/register', organize: '#/organize', scrummage: "#/scrummage", participant: '#/participant', participate: '#/participate' },
        current: 'home',
        showRegister: true,
        alertTypes: ['primary', 'secondary', 'success', 'info', 'warning', 'error'],
        showAlert: false,
        alert: {
            type: "primary",
            message: 'place alert message here',
            dismissable: true,
            timeout: 3000,
        }
    },
    scrum: {
        id: "",
        email: "",
        title: "",
        organization: "",
        choices: [],
        participants: []
    },
    votes: [],
    teams: [],
    player: {
        name: "",
        vote: "",
        organizer: "",
        choices: [],
        participants: [],
    },
    staticText,
}