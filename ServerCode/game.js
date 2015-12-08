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
                   getUser(user, function(userData){
                       if(userData!= null){
                           var id = getTimeBaseUniqueId();
                           putnewGameItem(req.body, userData.Items[0], id);
                           var result = {"GameId" : id};
                           returnData(res, 1, result, null);
                       }
                       else{
                            returnData(res, 0, null, '{Not all params present}');
                       }
                   });
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
                    console.error("Unable to query. Error:");
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
                            getUser(user, function(userData){
                                if(userData!= null){
                                    addPlayerToGame(req.body.GameId, userData.Items[0], setTimer,isDefender, isStarted, isDefenderLeader, function(succes){
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
                                else
                                {
                                    returnData(res, 0, null, '{getUserError}');
                                }
                            });
                        }  
                        else if(!found && Object.keys(players).length == data.Items[0].MaxPlayers){
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
 missions.push(marshal(type1Mission(gameData.CircleCenter.Latitude, gameData.CircleCenter.Longitude, gameData.CircleRadius)));
 missions.push(marshal(type2Mission()));
 missions.push(marshal(type3Mission(1)));
 missions.push(marshal(type4Mission(1)));
 missions.push(marshal(type5Mission(1)));
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
    var location = playerData.Location;
    if(location != null && Object.keys(location).length > 0 && gamedata.Timer != null){
        location.CreatedOn = location.CreatedOn.substring(0, location.CreatedOn.length-5) + "+00:00";
    }
    else{
        location = null;
    }
    
    var playerReturnData = {
    "Name" : players[player],
    "ImageURL": playerData.ImageURL,
    "LatestLocation": location,
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
            console.log("Not all params present");
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            console.log(req.body.GameId);
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                getGame(req.body.GameId, function(data){
                if (data == null) {
                    console.error("Game Doesn't exist");
                    var error = {'Errors' : [4]};
                    returnData(res, 2, null, error);                    
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
                                    var newWinners = false;
                                    if(newData.Items[0].WinningTeam == null && newData.Items[0].AmountDefenderMissions >= 20){
                                        newData.Items[0].WinningTeam = "Defenders";
                                        newWinners = true;
                                    }
                                    if(newWinners){
                                        setWinningTeam(req.body.GameId, "Defenders");
                                    }
                                    if(newWinners || (newData.Items[0].WinningTeam != null && newData.Items[0].IsGameDataAddedToUser == null)){
                                        for(player in newData.Items[0].Players){
                                            var winSurplus = 0;
                                            if((newData.Items[0].WinningTeam == "Defenders" && newData.Items[0].Players[player].IsDefender) ||(newData.Items[0].WinningTeam == "Attackers" && ! newData.Items[0].Players[player].IsDefender)){
                                                winSurplus = 1;
                                            }
                                            addGameDataToUser(player, newData.Items[0].Players[player], winSurplus);
                                        }
                                        setGameDataAddedToUser(req.body.GameId);
                                    }
                                    fillAndReturnGetData(res, newData.Items[0], user); 
                                    
                                    });
                                }
                                else{
                                    console.log("UpdateItemError");
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

    
app.post('/game/attack',  function(req, res){
    
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.GameId == null || req.body.AttackedUser == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            console.log(req.body.GameId);
            user = getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                getGame(req.body.GameId, function(data){
                if (data == null) {
                    console.error("Game Doesn't exist");
                    returnData(res, 0, null, null);
                    
                }
                else
                {
                    if(data.Items.length == 1){
                        var gameData = data.Items[0];
                        var player1Data = gameData.Players[req.body.AttackedUser];
                        var player2Data = gameData.Players[user]
                        if(player1Data != null && player2Data != null){
                            if(player2Data.IsDefender){
                            //console.log(player1Data);
                            var loc1 = player1Data.Location;
                            var loc2 = player2Data.Location;
                            //console.log(loc1);
                            //console.log(loc2);
                            if(loc1 == null || loc2 == null || Object.keys(loc1).length == 0 || Object.keys(loc2).length == 0){
                                var error = {'Errors' : [8]}; //One of the two has no location
                                returnData(res, 2, null, error);
                            }
                            else if(getDistanceFromLatLonVincenty(loc1.Latitude, loc1.Longitude, loc2.Latitude, loc2.Longitude) < 20){
                                allDeath = true;
                                for (i in gameData.Players){
                                    var player = gameData.Players[i];
                                    if((!player.IsDefender) && player.Lives > 0){
                                        allDeath = false;
                                        break;
                                    }
                                }
                                
                                //Will die
                                setUserDied(req.body.GameId, req.body.AttackedUser, gameData, allDeath, function(succes){
                                    if(succes){
                                        returnData(res, 1,{}, null);
                                        console.log(user  + " killed " + req.body.AttackedUser);
                                    }
                                    else{
                                        console.log("UpdateItemError attack");
                                        returnData(res, 0, null, '{UpdateItemError}');
                                    }
                                });
                            }
                            else{
                                var error = {'Errors' : [7]}; //To far
                                returnData(res, 2, null, error);
                            }
                            }
                            else{
                                var error = {'Errors' : [9]}; //Only defenders can use this
                                returnData(res, 2, null, error);
                            }
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

putnewGameItem = function(data, userData, id) {   
    console.log(userData);
    var username = userData.Username;
    //Team = -1 -> Not set, 0 -> Defender, 1 -> Attacker
    var tableName = 'Games';
    var players = {"M" : {}};
    var rand = Math.random();
    //console.log(rand);
    var isDefender = rand > 0.5;
    var amountDefenders = 0;
    var amountAttackers =0;
    if(isDefender)
        amountDefenders +=1;
    else
        amountAttackers +=1
    players.M[username] = getUserExpressionAttributes(isDefender, userData);
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
        'AmountDefenderMissions' : { 'N' : "0" },
        'AmountAttackerMissions' : { 'N' : "0" },
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
 
getUserExpressionAttributes = function(isDefender, userData){
var imageURL = defaultImage;
if(userData != null){
    imageURL = userData.ImageURL;
}
return {
        "M":{
            "DateAdded" : {"S" : new Date().toISOString()},
                    "Location" : {"M" : {}},
                    "AccelerometerData" : {"M" : {}},
                    "Missions" : {"L" : []},
                    "HasFlag" : {"BOOL": false},
                    "Lives": {"N" : "3"},
                    "IsDefender" : {"BOOL": isDefender},
                    "ImageURL" : {"S" : imageURL}
            }
    };
} 
    
addPlayerToGame = function(gameId, userData, setTimer, isDefender, isStarted, isDefenderLeader, callback){
    var user = userData.Username;
    var startDate;
    var updateExpression = "SET #attrName.#attrName2 = :user, #attrStarted = :isStarted";
    var expressionAttributesVal = {
            ":user": getUserExpressionAttributes(isDefender, userData),
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
        startDate = new Date(startDate.setMinutes(startDate.getMinutes() + 1)).toISOString();
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
    var UpdateExpress = "SET #attrName.#attrName2.#attrName3 = :location, #attrName.#attrName2.#attrName4 = :accelerometer"
    var ExpressionAttributeVal = {
            ":location": {
                "M":{
                    "CreatedOn" : {"S" : new Date().toISOString()},
                    "Longitude" : {"N" : data.Location.Longitude.toString()},
                    "Latitude" : {"N": data.Location.Latitude.toString()},
                    "Altitude" : {"N": data.Location.Altitude.toString()}
                }
            },
            ":accelerometer": {
               "M":{
                    "X" : {"N" : data.Accelerometer.X.toString()},
                    "Y" : {"N": data.Accelerometer.Y.toString()},
                    "Z" : {"N": data.Accelerometer.Z.toString()}
                }            
            }
        };
    var ExpressionAttributeNam = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrName3" : "Location",
        "#attrName4" : "AccelerometerData"
        };
    if(data.Died != null && data.Died){
        ExpressionAttributeNam["#attrLives"] = "Lives";
        UpdateExpress += ", #attrName.#attrName2.#attrLives = #attrName.#attrName2.#attrLives + :minusone";
        ExpressionAttributeVal[":minusone"] = {"N": "-1"};
        if(gameData.IsFlagCaptured && gameData.Players[user].HasFlag){
            ExpressionAttributeNam["#attrIsFlagCaptured"] = "IsFlagCaptured";
            ExpressionAttributeNam["#attrHasFlag"] = "HasFlag";
            UpdateExpress += ", #attrName.#attrName2.#attrHasFlag = :false, #attrIsFlagCaptured = :false";
            ExpressionAttributeVal[":false"] = marshal(false);
        }
    }
    var amountChanged = 0;
    var userMissions = gameData.Players[user].Missions;
    for (var i in data.CompletedMissions){
        var id = data.CompletedMissions[i];
        //console.log(data.CompletedMissions[i]);
        if(userMissions.length > id -1){
            if(! userMissions[id].IsFinished){
            userMissions[id].IsFinished = true;
            amountChanged++;
            }
        }
    }
    var AllCompleted = true; 
    for (var i in userMissions){    
         if(! userMissions[i].IsFinished)
            AllCompleted = false;
    }
        
    if(AllCompleted){
        userMissions.push(type1Mission(gameData.CircleCenter.Latitude, gameData.CircleCenter.Longitude, gameData.CircleRadius));
 userMissions.push(type2Mission());
 userMissions.push(type3Mission(1));
 userMissions.push(type4Mission(1));
 userMissions.push(type5Mission(1));
    }
    
    if(amountChanged > 0 || AllCompleted){
        //console.log(userMissions);
        ExpressionAttributeNam["#attrMissions"] = "Missions";
        UpdateExpress += ", #attrName.#attrName2.#attrMissions = :missions";
        ExpressionAttributeVal[":missions"] = marshal(userMissions);
        
        if(gameData.Players[user].IsDefender)
            ExpressionAttributeNam["#attrAmountMissions"] = "AmountDefenderMissions";
        else
            ExpressionAttributeNam["#attrAmountMissions"] = "AmountAttackerMissions";
        
        UpdateExpress += ", #attrAmountMissions =  #attrAmountMissions + :amountmissions";
        ExpressionAttributeVal[":amountmissions"] = marshal(amountChanged);
    }

    if(!gameData.IsFlagCaptured && !gameData.Players[user].IsDefender && getDistanceFromLatLonVincenty(gameData.DefenderBase.Latitude, gameData.DefenderBase.Longitude, data.Location.Latitude,
                                     data.Location.Longitude) < gameData.CircleRadius /10 && !data.Died){
        console.log("Flag captured by: " + user);
        ExpressionAttributeNam["#attrIsFlagCaptured"] = "IsFlagCaptured";
        ExpressionAttributeNam["#attrHasFlag"] = "HasFlag";
        UpdateExpress += ", #attrName.#attrName2.#attrHasFlag = :true, #attrIsFlagCaptured = :true";
        ExpressionAttributeVal[":true"] = marshal(true);
    }
    
    if(gameData.IsFlagCaptured && gameData.Players[user].HasFlag && gameData.WinningTeam == null && getDistanceFromLatLonVincenty(gameData.CircleCenter.Latitude, gameData.CircleCenter.Longitude, data.Location.Latitude,
                                     data.Location.Longitude) > gameData.CircleRadius  && !data.Died){
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

setUserDied = function(gameId, user, gameData, allDeath, callback){
    var UpdateExpress = "SET #attrName.#attrName2.#attrLives = #attrName.#attrName2.#attrLives + :minusone"
    var ExpressionAttributeVal = {
            ":minusone": {"N": "-1"}
        };
    var ExpressionAttributeNam = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrLives" : "Lives"
        };
    if(gameData.IsFlagCaptured && gameData.Players[user].HasFlag){
            ExpressionAttributeNam["#attrIsFlagCaptured"] = "IsFlagCaptured";
            ExpressionAttributeNam["#attrHasFlag"] = "HasFlag";
            UpdateExpress += ", #attrName.#attrName2.#attrHasFlag = :false, #attrIsFlagCaptured = :false";
            ExpressionAttributeVal[":false"] = marshal(false);
    }
    if(allDeath){
        UpdateExpress += ", #attrWinners = :winners";
        ExpressionAttributeVal[":winners"] = {"S": "Defenders"};
        ExpressionAttributeNam["#attrWinners"] = "WinningTeam";
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

setWinningTeam = function(gameId, winningTeam){
    var UpdateExpress = "SET #attrName = :winners"
    var ExpressionAttributeVal = {
            ":winners": {"S": winningTeam}
        };
    var ExpressionAttributeNam = {
        "#attrName" : "WinningTeam",
        };
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
        }
    });
}

setGameDataAddedToUser = function(gameId){
    var UpdateExpress = "SET #attrName = :true"
    var ExpressionAttributeVal = {
            ":true": {"BOOL": true}
        };
    var ExpressionAttributeNam = {
        "#attrName" : "IsGameDataAddedToUser",
        };
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
        }
    });
}

addGameDataToUser = function(username, data, winnerPlus){
    var amountMissions = 0;
    for (k in data.Missions){
        if(data.Missions[k].IsFinished){
            amountMissions +=1;
        }
    }    
    
    var UpdateExpress = "SET #attrMissions = #attrMissions + :missions, #attrWins = #attrWins + :wins, #attrGames = #attrGames + :one";
    var ExpressionAttributeVal = {
            ":one": {"N": "1"},
            ":missions" : {"N" : amountMissions.toString()},
            ":wins" : { "N" : winnerPlus.toString() }
        };
    var ExpressionAttributeNam = {
        "#attrMissions" : "Missions",
        "#attrWins" : "Wins",
        "#attrGames" : "Games"
        };
    dd.updateItem({
        TableName: "Users",
        Key: {
            "Username": {
                "S": username
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
	//console.log(getDistanceFromLatLonVincenty(start[0],start[1],latitude,longitude).toString());
	//console.log(locatie);
    return locatie;
	
}
    
function type1Mission(Latitude, Longitude, CircleRadius){
    var type1data = driveTo(Latitude, Longitude, CircleRadius);
    return {
            "IsActive" :  true,
            "IsFinished": false,
            "Type": 1,
            "Description": "Drive to (" + type1data.Latitude.toString() + ", " + type1data.Longitude.toString() + ")",
            "Location": type1data,
            "OnPhoneCheckable": true
           };
}
    
function type2Mission(){
    return {
            "IsActive" : true,
            "IsFinished": false,
            "Type": 2,
            "Description": "Gather with your team.",
            "OnPhoneCheckable": false
           };
}

function type3Mission(difficulty){
    var difference = rijHoogteverschil(difficulty);
    return {
            "IsActive" : true,
            "IsFinished": false,
            "Type": 3,
            "Description": "Drive a height difference of " + difference + "m.",
            "HeightDifference" : difference,
            "OnPhoneCheckable": true
           };
}

function type4Mission(difficulty){
    return {
            "IsActive" : true,
            "IsFinished": false,
            "Type": 4,
            "Description": "Search for light.",
            "AmountOfLight" : difficulty*50000,
            "OnPhoneCheckable": true
           };
} 
    
function type5Mission(difficulty){
    return {
            "IsActive" : true,
            "IsFinished": false,
            "Type": 5,
            "Description": "Get this speed",
            "SpeedValue" : (15+3*difficulty),
            "OnPhoneCheckable": true
           };
}
    
function rijHoogteverschil(difficulty){
	return ((Math.random()*5+5.0)*difficulty).toFixed(2);
}
    
}
        
                      