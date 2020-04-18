
const io = require('socket.io-client');

const sock = {
    connect: () => {
        var socket = io.connect('ws://localhost:3030');

        socket.on('connect', function (data) {
            socket.emit('join', data);
        });        
    },
    receive: (dispatch) => {
        socket.on('votes', function (data) {
            alert(data);
        });
    }
}

export default sock;