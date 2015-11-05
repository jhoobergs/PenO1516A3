const PORT=8080; 
var bodyParser = require('body-parser');
var bcrypt = require('bcrypt');
var express = require('express');
var AWS = require('aws-sdk');
AWS.config.update({accessKeyId: 'AKIAJEF2PONKFQ3LGT5A', secretAccessKey: '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'});
AWS.config.update({region: 'us-west-2'});
var dd = new AWS.DynamoDB();
var app = express();

// Log the requests
app.use(function(req, res, next) {
  console.log('%s %s', req.method, req.url);
  next();
});
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.get('/uitleg', function(req, res) {
    if(req.query != null && req.query.token == "HEHJHjEZJzhejh2938|kjzodj"){
    res.sendFile('uitleg.html', {root: __dirname});
    }
    else{
    res.send("");
    }
});



require('./friends.js')(app);
require('./scoreboard.js')(app, AWS);
require('./user.js')(app, AWS, bcrypt,dd);

returnData = function(res, status, result, error){
    res.send({
        'statusCode' : status,
        'body' : result,
        'error' : error
    });    
}

app.listen(PORT, function(){
    //Callback triggered when server is successfully listening. Hurray!
    console.log("Server listening on: http://localhost:%s", PORT);
});