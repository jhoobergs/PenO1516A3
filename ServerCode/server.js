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

app.get('/friends/list', function(req, res){
    res.send({'List' : [
              { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'}
              ]});
});

app.get('/scoreboard/list', function(req, res){
    res.send({'List' : [
              { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 7, 'Missions' : 10},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 0, 'Games' : 4, 'Missions' : 3},
              { 'Name' : 'Koen', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 2, 'Games' : 9, 'Missions' : 12},
              { 'Name' : 'Kevin', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 3, 'Games' : 5, 'Missions' : 8},
              { 'Name' : 'Moran', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 9, 'Missions' : 11},
              { 'Name' : 'Elisabeth', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 4, 'Games' : 5, 'Missions' : 3},         { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 7, 'Missions' : 10},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 0, 'Games' : 4, 'Missions' : 3},
              { 'Name' : 'Koen', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 2, 'Games' : 9, 'Missions' : 12},
              { 'Name' : 'Kevin', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 3, 'Games' : 5, 'Missions' : 8},
              { 'Name' : 'Moran', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 9, 'Missions' : 11},
              { 'Name' : 'Elisabeth', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 4, 'Games' : 5, 'Missions' : 3}
          
    ]});
});

app.post('/user/login', function(req, res) {
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
console.log(req.body);
var error = "";
if(req.body != null){
    console.log("Querying for Users with id.");
    if(req.body.Username == null ||req.body.Password == null){
        res.send("{Not all params present}");
    }
    else{
        var params = {
            TableName : "Users",
            KeyConditionExpression: "#username = :name",
            ExpressionAttributeNames:{
                "#username": "Username"
            },
            ExpressionAttributeValues: {
                ":name":req.body.Username
            }
        };
    

        dynamodbDoc.query(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                console.log("Query succeeded.");
                if(bcrypt.compareSync(req.body.Password, data.Items[0].Password)){
                    res.send("{Logged in}");
                    data.Items.forEach(function(item) {
                        console.log(" -", item.UserId + ": " + item.Test);
                    });
                }
                else{
                    res.send("{Wrong Password}");
                }
            }
        });
    }
}
else{
    res.send("{No body}");
}  
});

app.post('/user/create', function(req, res) {
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
console.log(req.body);
var error = "";
if(req.body != null){
    console.log("Creating for Users with id.");
    if(req.body.Username == null ||req.body.Password == null || req.body.Password != req.body.PasswordRepeat || req.body.Email == null){
        res.send("{Not all params present}");
    }
    else{
        var params = {
            TableName : "Users",
            KeyConditionExpression: "#username = :name",
            ExpressionAttributeNames:{
                "#username": "Username"
            },
            ExpressionAttributeValues: {
                ":name":req.body.Username
            }
        };    

        dynamodbDoc.query(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                console.log("Query succeeded.");
                if(data.Items.length == 0){
                    //Ok, we can create the user                            
                    putnewUserItem(req.body);
                    res.send("{created}");
                }
                else{
                res.send("{Username does allready exist.}");
                }
            }
        });
    }
}
else{
    res.send("{No body}");
}  
});

putnewUserItem = function(data) {           
    var salt = bcrypt.genSaltSync(10);
    var hash = bcrypt.hashSync(data.Password, salt);
    var tableName = 'Users';
    var item = {
	    'Username' : { 'S': data.Username },
	    'Password' : { 'S' : hash},
        'Email' : { 'S' : data.Email}
    };	
    dd.putItem({
        'TableName': tableName,
        'Item': item
    }, function(err, data) {
        err && console.log(err);
    });
};

app.listen(PORT, function(){
    //Callback triggered when server is successfully listening. Hurray!
    console.log("Server listening on: http://localhost:%s", PORT);
});