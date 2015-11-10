const PORT=8080; 
var bodyParser = require('body-parser');
var bcrypt = require('bcrypt');
var express = require('express');
var AWS = require('aws-sdk');
AWS.config.update({accessKeyId: 'AKIAJEF2PONKFQ3LGT5A', secretAccessKey: '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'});
AWS.config.update({region: 'us-west-2'});
var dd = new AWS.DynamoDB();
var app = express();
var uuid = require('node-uuid'); //random id's

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
});
var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
getUserByToken = function(res, header, callback){
    
    if(header.token == null){
        var error = {'Errors' : [3]};
        returnData(res, 2, null, error);
    }
    else{
    token = header.token;
    var params = {
            TableName : "Tokens",
            KeyConditionExpression: "#token = :token",
            ExpressionAttributeNames:{
                "#token": "Token"
            },
            ExpressionAttributeValues: {
                ":token":token
            }
        };    
        dynamodbDoc.query(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                if(data.Items.length == 1){
                    callback(data.Items[0].Username);                    
                }
                else{
                    var error = {'Errors' : [3]};
                    returnData(res, 2, null, error); 
                }
            }

        });
    }
};

getUser = function(username, callback){
    var params = {
            TableName : "Users",
            KeyConditionExpression: "#username = :name",
            ExpressionAttributeNames:{
                "#username": "Username"
            },
            ExpressionAttributeValues: {
                ":name":username
            }
        }; 

        dynamodbDoc.query(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
                callback(null);
            } else {
                callback(data);
            }
        });
}

getTimeBaseUniqueId = function(){
    return uuid.v1();
}

//Username to lowercase when saving. First letter to Uppercase when returning.
require('./friends.js')(app, AWS);
require('./scoreboard.js')(app, AWS);
require('./game.js')(app, AWS, dd);
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