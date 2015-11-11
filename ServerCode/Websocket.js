exports = module.exports = function(){
var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ port: 8081 });

wss.on('connection', function connection(ws) {
    console.log(wss.clients.length);
  ws.on('message', function incoming(message) {
    console.log('received: %s', message);
      wss.broadcast(message);
  });
    
  /*   ws.on('broadcast', function incoming(message) {
    console.log('br received: %s', message);
  });*/
    
 wss.broadcast = function broadcast(data) {
  wss.clients.forEach(function each(client) {
    client.send(data);
  });
};

wss.broadcast('broadcastReceived');
});
    
}