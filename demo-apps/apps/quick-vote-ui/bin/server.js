const express = require('express');
const cors = require('cors')
const bodyParser = require('body-parser')
const { loadJson } = require('./handlers');

const app = express()
const server = require('http').createServer(app);
const io = require('socket.io')(server);

const cached = loadJson('./staticText.json');
const repo = loadJson('./store/sample.json');

app.use(cors());
app.use(express.static('public'));
app.use(bodyParser.json());

const connection = (client) => {
    return (data) => {
        client.emit('votes', data);
    }
}

let emitter;

//rest endpoints
app.get('/', (req, res) => res.send(cached));

app.post('/organizer/login', (req, res) => {
    let login = req.body;
    const user = repo.users.filter(user => user.emailAddress === login.email)[0];
    const teams = repo.teams.filter(team => team.organizer === login.email);
    res.json(
        {
            body: {
                auth: {
                    signedIn: true,
                    user,
                    displayName: login.email
                },
                teams,
            },
            error: null
        })
});

app.get('/organizer/:email', (req, res) => {
    const email = req.params.email;
    const scrum = cached.mockData.findByOrganizer.filter(scrum => scrum.organizer === email);
    if (scrum && scrum.length > 0) {
        voting[email] = scrum[0];
        res.json({ data: scrum[0], error: "" })
    }
    else {
        res.json({ data: null, error: "No scrum found by organizer email" });
    }
});

app.post('/organizer/:email/vote', (req, res) => {
    const email = req.params.email;
    emitter(JSON.stringify({ email, vote: req.body }));
    res.json(req.body)
});

app.post('/organizer/:email/lock', (req, res) => {
    res.json({ locked: true })
});

app.post('/organizer/:email/show', (req, res) => {
    res.json({ display: true })
});

//socket-io endpoints
io.on('connection', function (client) {
    console.log('Client connected...');
    emitter = connection(client);

    client.on('join', function (data) {
        console.log(data);
    });

    client.on('messages', function (data) {
        client.emit('broad', data);
        client.broadcast.emit('broad', data);
    });
});

const port = 3030
server.listen(port, () => console.log(`Test server listening on port ${port}!`));
