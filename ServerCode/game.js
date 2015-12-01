exports = module.exports = function(app, AWS, dd){
var marshal = require('dynamodb-marshaler/marshal');
var defaultImage = "http://52.26.187.234:8080/defaultProfile.jpg";
app.post('/game/create', function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        //console.log(req.body);
        if(req.body.Name == null ||req.body.MinPlayers == null || req.body.MaxPlayers == null ||req.body.MinPlayers >                       req.body.MaxPlayers || req.body.CenterLocationLatitude == null || req.body.CenterLocationLongitude == null ||
          req.body.CircleRadius == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                   var id = getTimeBaseUniqueId();
                   putnewGameItem(req.body, user, id);
                   var result = {"GameId" : id};
                   returnData(res, 1, result, null);
                }
            });
        }
    }
});
    
app.post('/game/join',  function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.GameId == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                getGame(req.body.GameId, function(data){
                if (data == null) {
                    console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
                }
                else
                {
                    if(data.Items.length == 1){
                        var found = false;
                        var players = data.Items[0].Players;
                        for (var i in players) {
                            if(i == user){
                                //console.log("gelijk");
                                found = true;
                                break;
                            }
                        }
                        if(!found && Object.keys(players).length< data.Items[0].MaxPlayers){
                            var setTimer = false;
                            if(Object.keys(players).length == data.Items[0].MinPlayers-1){
                                 setTimer = true;       
                            }
                            var isStarted = false;
                            
                            if(data.Items[0].Timer != null && new Date(data.Items[0].Timer) < new Date())
                                isStarted = true;
                            if(Object.keys(players).length == data.Items[0].MaxPlayers-1){
                                 isStarted = true;    
                                 
                            }
                            var isDefender = true;
                            if(data.Items[0].AmountAttackers < data.Items[0].AmountDefenders)
                                isDefender = false;
                            var isDefenderLeader = false;
                            if(isDefender && data.Items[0].AmountDefenders == 0)
                                isDefenderLeader = true;
                            addPlayerToGame(req.body.GameId, user, setTimer,isDefender, isStarted, isDefenderLeader, function(succes){
                                if(succes){
                                    if(isStarted){
                                        players[user] = {};
                                        AddFirstMissions(req.body.GameId, data.Items[0], players, function(succes){
                                            if(succes){
                                            returnData(res, 1, result, null);
                                            }
                                            else{
                                            returnData(res, 0, null, '{UpdateItemError}');
                                            }
                                            });
                                        }
                                    else{
                                    var result = {};
                                    returnData(res, 1, result, null);
                                    }
                                }
                                else{
                                    returnData(res, 0, null, '{UpdateItemError}');
                                }
                            });
                        }  
                        else if(!found && players.length == data.Items[0].MaxPlayers){
                            var error = {'Errors' : [5]};
                            returnData(res, 2, null, error);
                        }
                        else
                        {
                            var result = {};
                            returnData(res, 1, result, null);
                        }
                    }
                    else{
                        var error = {'Errors' : [4]};
                        returnData(res, 2, null, error);
                    }
                }
                });
                }
            });
        }
    }
});

AddFirstMissions = function(GameId, gameData, players, callback){
var amountDone = 0;
var amountToDo = Object.keys(players).length;
for (var i in players) {

var missions = [];
 missions.push(type1Mission(gameData.CircleCenter.Latitude, gameData.CircleCenter.Longitude, gameData.CircleRadius));
 missions.push(type2Mission());
 missions.push(type3Mission(1));
 missions.push(type4Mission(1));
 missions.push(type5Mission(1));
setMissionsForUser(GameId, i, missions, function(succes){
    amountDone++;
        if(succes){
           if(amountDone == amountToDo){
            var result = {};
            callback(true);
            
        }
        }
        else{
            callback(false);
            returnData(res, 0, null, '{UpdateItemError}');
        }
    });
    }
}
    
app.post('/game/getData',  function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.GameId == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                getGame(req.body.GameId, function(data){
                if (data == null) {
                    console.error("No Data sended");
                }
                else
                {
                    if(data.Items.length == 1){
                        var found = false;
                        var players = data.Items[0].Players;
                        for (var i in players) {
                            if(i == user){
                                //console.log("gelijk");
                                found = true;
                                break;
                            }
                        }
                    if(found){                        
                    var gamedata = data.Items[0];
                    if(gamedata.Timer != null && !gamedata.IsStarted && new Date(data.Items[0].Timer) < new Date()){
                        AddFirstMissions(req.body.GameId, gamedata, gamedata.Players, function(succes){
                            if(succes){
                                getGame(req.body.GameId, function(newData){
                                fillAndReturnGetData(res, newData.Items[0], user);
                                });
                            }
                            else{
                                var error = {};
                                returnData(res, 0, null, error);
                            }
                        });
                    }
                    else{
                        fillAndReturnGetData(res, gamedata, user);
                    }
                    
                        
                    }  
                    else{
                        var error = {};
                        returnData(res, 0, null, error);
                    }
                }
                    else{
                        var error = {'Errors' : [4]};
                        returnData(res, 2, null, error);
                    }
                }
                });
                }
            });
        }
    }
});
    
function fillAndReturnGetData(res, gamedata, user){
    var item = {};
    item.GameId = gamedata.GameId;
    item.Name = gamedata.Name;
    item.IsFlagCaptured = gamedata.IsFlagCaptured;
    item.DefenderLeader = gamedata.DefenderLeader;
    item.MinPlayers = gamedata.MinPlayers;
    item.MaxPlayers = gamedata.MaxPlayers;
    item.DefenderBase = gamedata.DefenderBase;
    item.CircleRadius = gamedata.CircleRadius;
    item.WinningTeam = gamedata.WinningTeam;
    item.Players = [];
    var players = Object.keys(gamedata.Players);
    for(var player in players){
    var playerData = gamedata.Players[players[player]];
    var locations = playerData.Locations;
    if(locations.length > 0){
    var latestLocation = locations[locations.length -1];
    if(latestLocation != null && gamedata.Timer != null){
        latestLocation.CreatedOn = latestLocation.CreatedOn.substring(0, gamedata.Timer.length-5) + "+00:00";
    }
    }
    var playerReturnData = {
    "Name" : players[player],
    "ImageURL": defaultImage,
    "LatestLocation": latestLocation,
    "HasFlag" : playerData.HasFlag,
    "Lives" : playerData.Lives
    }
    if(playerData.IsDefender){
    playerReturnData["Team"] = "Defender";
    }
    else{
    playerReturnData["Team"] = "Attacker";
    }
    /*if(players[player] == user){
    playerReturnData["IsRequester"] = true;
    }*/
    if(playerReturnData.Name != user){
    item.Players.push(playerReturnData);
    }
    else{
    item.Player = playerReturnData;
    }
    if(players[player] == user){
        item.Missions = gamedata.Players[players[player]].Missions;
        for(var mission in item.Missions){
            item.Missions[mission].Id = parseInt(mission);
        }
    }
    }
    item.CenterLocation = gamedata.CircleCenter;
    if(gamedata.Timer != null)
        item.TimerDate = gamedata.Timer.substring(0, gamedata.Timer.length-5) + "+00:00";
    item.IsStarted = gamedata.IsStarted;
    //console.log(item);
    returnData(res, 1, item, null);  
}
    
app.get('/game/list', function(req, res){
var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
user = getUserByToken(res, req.headers, function(user){
    if(user != undefined){
        var params = {
            TableName : "Games"
        };    

        dynamodbDoc.scan(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                var result = [];
                for (var i in data.Items) {
                    var gamedata = data.Items[i];
                    var item = {};
                    item.GameId = gamedata.GameId;
                    item.Name = gamedata.Name;
                    item.MinPlayers = gamedata.MinPlayers;
                    item.MaxPlayers = gamedata.MaxPlayers;
                    item.Players = [];
                    var players = Object.keys(gamedata.Players);
                    for(var player in players){
                    var playerData = {
                    "Name" : players[player],
                    "ImageURL": "http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg"
                    }
                    item.Players.push(playerData); 
                    }
                    item.CenterLocation = gamedata.CircleCenter;
                    if(gamedata.Timer != null)
                        item.TimerDate = gamedata.Timer.substring(0, gamedata.Timer.length-5) + "+00:00";
                    item.IsStarted = gamedata.IsStarted;
                    result.push(item);
                    
                }  
                returnData(res, 1,{'List' :result}, null);
            }
        });
    }
});
});
    
    
app.post('/game/sendData',  function(req, res){
    
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.GameId == null || req.body.Accelerometer == null || req.body.Location == null || req.body.CompletedMissions == null ||req.body.Died == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            console.log(req.body.GameId);
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                getGame(req.body.GameId, function(data){
                if (data == null) {
                    console.error("Game Doesn't exist");
                }
                else
                {
                    if(data.Items.length == 1){
                        var found = false;
                        var players = data.Items[0].Players;
                        for (var i in players) {
                            if(i == user){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            var error = {'Errors' : [6]};
                            returnData(res, 2, null, error);
                        }  
                        else
                        {
                            addDataToGame(req.body.GameId, user, req.body, data.Items[0], function(succes){
                                if(succes){
                                    getGame(req.body.GameId, function(newData){
                                    fillAndReturnGetData(res, newData.Items[0], user);
                                    });
                                }
                                else{
                                    returnData(res, 0, null, '{UpdateItemError}');
                                }
                            });
                        }
                    }
                    else{
                        var error = {'Errors' : [4]};
                        returnData(res, 2, null, error);
                    }
                }
                });
                }
            });
        }
    }
});
                          

putnewGameItem = function(data, username, id) {           
    //Team = -1 -> Not set, 0 -> Defender, 1 -> Attacker
    var tableName = 'Games';
    var players = {"M" : {}};
    var rand = Math.random();
    console.log(rand);
    var isDefender = rand > 0.5;
    var amountDefenders = 0;
    var amountAttackers =0;
    if(isDefender)
        amountDefenders +=1;
    else
        amountAttackers +=1
    players.M[username] = getUserExpressionAttributes(isDefender, []);
    var defenderBase = driveTo(data.CenterLocationLatitude, data.CenterLocationLongitude, data.CircleRadius);
    var item = {
	    'GameId' : { 'S': id },
        'Name' : { 'S' : data.Name},
        'CreatedOn' : {'S' : new Date().toISOString()},
        'CreatedBy' : {'S' : username},
	    'MinPlayers' : { 'N' : data.MinPlayers.toString()},
        'MaxPlayers' : { 'N' : data.MaxPlayers.toString()},
        'Players' : players,
        'CircleCenter' : {
                            "M" : {
                                    Latitude:  {"N" : data.CenterLocationLatitude.toString()},
                                    Longitude: {"N" : data.CenterLocationLongitude.toString()}        
                                   }
        },
        'DefenderBase' : {
                            "M" : {
                                    Latitude:  {"N" : defenderBase.Latitude.toString()},
                                    Longitude: {"N" : defenderBase.Longitude.toString()}        
                                   }
        },
        'CircleRadius' : { 'N' : data.CircleRadius.toString()},
        'IsStarted' : { 'BOOL' : false},
        'AmountDefenders' : {'N' : amountDefenders.toString() },
        'AmountAttackers' : {'N' : amountAttackers.toString() },
        'IsFlagCaptured' : { 'BOOL' : false}
    };	
    if(isDefender){
        item["DefenderLeader"] = {'S' : username};     
    }
    
    dd.putItem({
        'TableName': tableName,
        'Item': item
    }, function(err, data) {
        err && console.log(err);
    });
};
 
getUserExpressionAttributes = function(isDefender){
return {
        "M":{
            "DateAdded" : {"S" : new Date().toISOString()},
                    "Locations" : {"L" : []},
                    "AccelerometerData" : {"L" : []},
                    "Missions" : {"L" : []},
                    "HasFlag" : {"BOOL": false},
                    "Lives": {"N" : "3"},
                    "IsDefender" : {"BOOL": isDefender}
            }
    };
} 
    
addPlayerToGame = function(gameId, user, setTimer, isDefender, isStarted, isDefenderLeader, callback){
    var startDate;
    var updateExpression = "SET #attrName.#attrName2 = :user, #attrStarted = :isStarted";
    var expressionAttributesVal = {
            ":user": getUserExpressionAttributes(isDefender),
            ":isStarted": {'BOOL' :isStarted}
        };
    var ExpressionAttributeN = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrStarted" : "IsStarted"
        }
    if(isDefender){
        ExpressionAttributeN["#attrAmountDefenders"] = "AmountDefenders";
        updateExpression += ", #attrAmountDefenders = #attrAmountDefenders + :one";
        expressionAttributesVal[":one"] = {'N' : '1'};
        
    }
    else{
        ExpressionAttributeN["#attrAmountAttackers"] = "AmountAttackers";
        updateExpression += ", #attrAmountAttackers = #attrAmountAttackers + :one";
        expressionAttributesVal[":one"] = {'N' : '1'};
    }   
    if(isDefenderLeader){
        ExpressionAttributeN["#attrDefenderLeader"] = "DefenderLeader";
        updateExpression += ", #attrDefenderLeader = :username";
        expressionAttributesVal[":username"] = {'S' : user};        
    }
    /*if(missions.length > 0){
        ExpressionAttributeN["#attrMissions"] = "Missions";
        updateExpression += ", #attrName.#attrName2.#attrMissions = list_append(#attrName.#attrName2.#attrMissions, :missions)";
        expressionAttributesVal[":missions"] = {'L' : missions};
    }*/
    if(setTimer){
        startDate = new Date();
        startDate = new Date(startDate.setMinutes(startDate.getMinutes() + 5)).toISOString();
        ExpressionAttributeN["#attrTimer"] = "Timer";
        updateExpression += ", #attrTimer = :timer";
        expressionAttributesVal[":timer"] = {"S": startDate};
        
    }
    dd.updateItem({
        TableName: "Games",
        Key: {
            "GameId": {
                "S": gameId
            }
        },
        //UpdateExpression: "ADD #attrName = :user",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        UpdateExpression: updateExpression,
        ExpressionAttributeNames : ExpressionAttributeN,
        ExpressionAttributeValues: expressionAttributesVal
    }, function(err, data) {
        if(err){
            console.log(err);
            callback(false)
        }
        else
        {
            callback(true);
        }
    });
}

setMissionsForUser = function(gameId, user, missions, callback){
    var updateExpression = "SET #attrName.#attrName2.#attrMissions = list_append(#attrName.#attrName2.#attrMissions, :missions), #attrIsStarted = :isStarted";
    var expressionAttributesVal = {
            ":missions": {'L' : missions},
            ":isStarted": {'BOOL' : true}
        };
    var ExpressionAttributeN = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrMissions" : "Missions",
        "#attrIsStarted" : "IsStarted"
        }      
    dd.updateItem({
        TableName: "Games",
        Key: {
            "GameId": {
                "S": gameId
            }
        },
        UpdateExpression: updateExpression,
        ExpressionAttributeNames : ExpressionAttributeN,
        ExpressionAttributeValues: expressionAttributesVal
    }, function(err, data) {
        if(err){
            console.log(err);
            callback(false)
        }
        else
        {
            callback(true);
        }
    });
}

addDataToGame = function(gameId, user, data, gameData, callback){
    var UpdateExpress = "SET #attrName.#attrName2.#attrName3 = list_append(#attrName.#attrName2.#attrName3, :location), #attrName.#attrName2.#attrName4 = list_append(#attrName.#attrName2.#attrName4, :accelerometer)"
    var ExpressionAttributeVal = {
            ":location": {
               "L": [{"M":{
                    "CreatedOn" : {"S" : new Date().toISOString()},
                    "Longitude" : {"N" : data.Location.Longitude.toString()},
                    "Latitude" : {"N": data.Location.Latitude.toString()},
                    "Altitude" : {"N": data.Location.Altitude.toString()}
                }
            }]
            },
            ":accelerometer": {
               "L": [{"M":{
                    "X" : {"N" : data.Accelerometer.X.toString()},
                    "Y" : {"N": data.Accelerometer.Y.toString()},
                    "Z" : {"N": data.Accelerometer.Z.toString()}
                }
            }]
            }
        };
    var ExpressionAttributeNam = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrName3" : "Locations",
        "#attrName4" : "AccelerometerData"
        };
    if(data.Died != null && data.Died){
        ExpressionAttributeNam["#attrLives"] = "Lives";
        UpdateExpress += ", #attrName.#attrName2.#attrLives = #attrName.#attrName2.#attrLives + :minusone";
        ExpressionAttributeVal[":minusone"] = {"N": "-1"};
    }
    var changed = false;
    var userMissions = gameData.Players[user].Missions;
    for (var i in data.CompletedMissions){
        var id = data.CompletedMissions[i];
        //console.log(data.CompletedMissions[i]);
        if(userMissions.length > id -1){
            userMissions[id].IsFinished = true;
            changed = true;
        }
    }
    if(changed){
        //console.log(userMissions);
        ExpressionAttributeNam["#attrMissions"] = "Missions";
        UpdateExpress += ", #attrName.#attrName2.#attrMissions = :missions";
        ExpressionAttributeVal[":missions"] = marshal(userMissions);
    }

    if(!gameData.IsFlagCaptured && !gameData.Players[user].IsDefender && getDistanceFromLatLonVincenty(gameData.DefenderBase.Latitude, gameData.DefenderBase.Longitude, data.Location.Latitude,
                                     data.Location.Longitude) < gameData.CircleRadius /10){
        console.log("Flag captured by: " + user);
         ExpressionAttributeNam["#attrIsFlagCaptured"] = "IsFlagCaptured";
        ExpressionAttributeNam["#attrHasFlag"] = "HasFlag";
        UpdateExpress += ", #attrName.#attrName2.#attrHasFlag = :true, #attrIsFlagCaptured = :true";
        ExpressionAttributeVal[":true"] = marshal(true);
    }
    
    if(gameData.IsFlagCaptured && gameData.Players[user].HasFlag && gameData.WinningTeam == null && getDistanceFromLatLonVincenty(gameData.CircleCenter.Latitude, gameData.CircleCenter.Longitude, data.Location.Latitude,
                                     data.Location.Longitude) > gameData.CircleRadius){
        console.log("Flag brought to perimeter by: " + user);
        ExpressionAttributeNam["#attrWinningTeam"] = "WinningTeam";
        UpdateExpress += ", #attrWinningTeam = :attackers";
        ExpressionAttributeVal[":attackers"] = marshal("Attackers");
    }
    
    dd.updateItem({
        TableName: "Games",
        Key: {
            "GameId": {
                "S": gameId
            }
        },
        //UpdateExpression: "ADD #attrName = :user",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        UpdateExpression: UpdateExpress,
        ExpressionAttributeNames : ExpressionAttributeNam,
        ExpressionAttributeValues: ExpressionAttributeVal
    }, function(err, data) {
        if(err){
            console.log(err);
            callback(false)
        }
        else
        {
            callback(true);
        }
    });
}

function driveTo(latitude,longitude,straal){
	start = [latitude,longitude]
	var randomX = Math.random()/10000;
	var randomY = Math.random()/10000;
    
    var randomSignX = Math.random();
    var randomSignY = Math.random();
    if(randomSignX > 0.5)
        randomX = -1 * randomX;
    if(randomSignY > 0.5)
        randomY = -1 * randomY;
    
	var list = [];
    latitude = latitude + randomX;
	longitude = longitude +randomY;
	while (getDistanceFromLatLonVincenty(start[0],start[1],latitude,longitude)<straal) {
  	
	list[list.length] = {Latitude:latitude,Longitude:longitude};
	//console.log(list.toString());
    latitude = latitude + randomX;
	longitude = longitude +randomY;
	

	}
	var locatie = list[Math.floor(Math.random() * list.length)];
	console.log(getDistanceFromLatLonVincenty(start[0],start[1],latitude,longitude).toString());
	console.log(locatie);
    return locatie;
	
}
    
function type1Mission(Latitude, Longitude, CircleRadius){
    var type1data = driveTo(Latitude, Longitude, CircleRadius);
    return {"M" :{
            "IsActive" : {'BOOL':  true},
            "IsFinished": {'BOOL':  false},
            "Type": {'N' : '1'},
            "Description": {'S' : "Drive to (" + type1data.Latitude.toString() + ", " + type1data.Longitude.toString() + ")"},
            "Location":{"M" : {
                "Latitude" : {'N' : type1data.Latitude.toString()},
                "Longitude" : {'N' : type1data.Longitude.toString()}
            }},
            "OnPhoneCheckable": {'BOOL' : true}
           }
        };
}
    
function type2Mission(){
    return {"M" :{
            "IsActive" : {'BOOL':  true},
            "IsFinished": {'BOOL':  false},
            "Type": {'N' : '2'},
            "Description": {'S' : "Gather with your team."},
            "OnPhoneCheckable": {'BOOL' : false}
           }
        };
}

function type3Mission(difficulty){
    var difference = rijHoogteverschil(difficulty);
    return {"M" :{
            "IsActive" : {'BOOL':  true},
            "IsFinished": {'BOOL':  false},
            "Type": {'N' : '3'},
            "Description": {'S' : "Drive a height difference of " + difference + "m."},
            "HeightDifference" : { 'N' : difference.toString() },
            "OnPhoneCheckable": {'BOOL' : true}
           }
        };
}

function type4Mission(difficulty){
    return {"M" :{
            "IsActive" : {'BOOL':  true},
            "IsFinished": {'BOOL':  false},
            "Type": {'N' : '4'},
            "Description": {'S' : "Search for light."},
            "AmountOfLight" : {'N' : (difficulty*50000).toString() },
            "OnPhoneCheckable": {'BOOL' : true}
           }
        };
} 
    
function type5Mission(difficulty){
    return {"M" :{
            "IsActive" : {'BOOL':  true},
            "IsFinished": {'BOOL':  false},
            "Type": {'N' : '5'},
            "Description": {'S' : "Get this speed"},
            "SpeedValue" : {'N' : (15+3*difficulty).toString() },
            "OnPhoneCheckable": {'BOOL' : true}
           }
        };
}
    
function rijHoogteverschil(difficulty){
	return ((Math.random()*5+5.0)*difficulty).toFixed(2);
}
    
}
        
                      