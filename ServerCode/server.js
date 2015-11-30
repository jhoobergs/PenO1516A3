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
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json()); //Body's in json format will be accepted.

app.get('/uitleg', function(req, res) {
    if(req.query != null && req.query.token == "HEHJHjEZJzhejh2938|kjzodj"){
    res.sendFile('uitleg.html', {root: __dirname});
    }
});
var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
getUserByToken = function(res, header, callback){
    //This function will return the username of the user that sent the request.
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
                    callback(data.Items[0].Username);  //execute the callback funtion (3e parameter of the function)         
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
    //Get all data of a user by its username
    var params = {
            TableName : "Users",
            KeyConditionExpression: "#username = :name",
            ExpressionAttributeNames:{
                "#username": "Username"
            },
            ExpressionAttributeValues: {
                ":name":formatUsername(username)
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

getGame = function(gameId, callback){
    //Get all data of a game by its gameId
    var params = {
            TableName : "Games",
            KeyConditionExpression: "#gameId = :gameId",
            ExpressionAttributeNames:{
                "#gameId": "GameId"
            },
            ExpressionAttributeValues: {
                ":gameId":gameId
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
    //use the node-uuid package to generate a random (timebased) unique id
    return uuid.v1();
}

getDistanceFromLatLonVincenty = function(lat1, lon1, lat2, lon2){
    //Javascript implementation of Vincenty's algorithm to calculate distance between 2 latitude-longitude pairs
  var p1 = {};
  var p2 = {};
  p1.lat = deg2rad(lat1);
  p1.lon = deg2rad(lon1);
    p2.lat = deg2rad(lat2);
    p2.lon = deg2rad(lon2);
  var a = 6378137, b = 6356752.3142,  f = 1/298.257223563;  // WGS-84 ellipsiod
  var L = p2.lon - p1.lon;
  var U1 = Math.atan((1-f) * Math.tan(p1.lat));
  var U2 = Math.atan((1-f) * Math.tan(p2.lat));
  var sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
  var sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
  
  var lambda = L, lambdaP = 2*Math.PI;
  var iterLimit = 20;
  while (Math.abs(lambda-lambdaP) > 1e-12 && --iterLimit>0) {
    var sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
    var sinSigma = Math.sqrt((cosU2*sinLambda) * (cosU2*sinLambda) + 
      (cosU1*sinU2-sinU1*cosU2*cosLambda) * (cosU1*sinU2-sinU1*cosU2*cosLambda));
    if (sinSigma==0) return 0;  // co-incident points
    var cosSigma = sinU1*sinU2 + cosU1*cosU2*cosLambda;
    var sigma = Math.atan2(sinSigma, cosSigma);
    var sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
    var cosSqAlpha = 1 - sinAlpha*sinAlpha;
    var cos2SigmaM = cosSigma - 2*sinU1*sinU2/cosSqAlpha;
    if (isNaN(cos2SigmaM)) cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0 (§6)
    var C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
    lambdaP = lambda;
    lambda = L + (1-C) * f * sinAlpha *
      (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
  }
  if (iterLimit==0) return NaN  // formula failed to converge

  var uSq = cosSqAlpha * (a*a - b*b) / (b*b);
  var A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
  var B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
  var deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
    B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
  var s = b*A*(sigma-deltaSigma);
  
  s = s.toFixed(3); // round to 1mm precision
  return s;
}

deg2rad = function(deg) {
  return deg * (Math.PI/180);
}

formatUsername = function(string) {
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}

//Username to lowercase when saving. First letter to Uppercase when returning.
require('./friends.js')(app, AWS, dd);
require('./scoreboard.js')(app, AWS);
require('./game.js')(app, AWS, dd);
require('./user.js')(app, AWS, bcrypt,dd);
require('./Websocket.js')();



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

