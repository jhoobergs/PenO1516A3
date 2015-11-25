exports = module.exports = function(app, AWS, dd){

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
                            
                            if(data.Items[0].Timer != null)
                                console.log(new Date(data.Items[0].Timer));
                            if(Object.keys(players).length == data.Items[0].MaxPlayers-1){
                                 isStarted = true;    
                                 
                            }
                            var isDefender = true;
                            if(data.Items[0].AmountAttackers < data.Items[0].AmountDefenders)
                                isDefender = false;
                            addPlayerToGame(req.body.GameId, user, setTimer,isDefender, isStarted, function(succes){
                                if(succes){
                                    if(isStarted){
                                var amountDone = 0;
                                players[user] = {};
                                var amountToDo = Object.keys(players).length;
                                for (var i in players) {
                            
                                var missions = [];
                                 missions.push(type1Mission(data.Items[0].CircleCenter.Latitude, data.Items[0].CircleCenter.Longitude, data.Items[0].CircleRadius));
                                 missions.push(type2Mission());
                                 missions.push(type3Mission());
                                setMissionsForUser(req.body.GameId, i, missions, function(succes){
                                    amountDone++;
                                        if(succes){
                                           if(amountDone == amountToDo){
                                            var result = {};
                                            returnData(res, 1, result, null);
                                        }
                                        }
                                        else{
                                            returnData(res, 0, null, '{UpdateItemError}');
                                        }
                                    });
                                    }
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
                    //console.log(item);
                    returnData(res, 1, item, null);  
                        
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
                            addDataToGame(req.body.GameId, user, req.body, function(succes){
                                if(succes){
                                    var result = {};
                                    returnData(res, 1, result, null);
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
        'CircleRadius' : { 'N' : data.CircleRadius.toString()},
        'IsStarted' : { 'BOOL' : false},
        'AmountDefenders' : {'N' : amountDefenders.toString() },
        'AmountAttackers' : {'N' : amountAttackers.toString() }
    };	
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
    
addPlayerToGame = function(gameId, user, setTimer, isDefender, isStarted, callback){
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
    var updateExpression = "SET #attrName.#attrName2.#attrMissions = list_append(#attrName.#attrName2.#attrMissions, :missions)";
    var expressionAttributesVal = {
            ":missions": {'L' : missions}
        };
    var ExpressionAttributeN = {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrMissions" : "Missions"
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

addDataToGame = function(gameId, user, data, callback){
    dd.updateItem({
        TableName: "Games",
        Key: {
            "GameId": {
                "S": gameId
            }
        },
        //UpdateExpression: "ADD #attrName = :user",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        UpdateExpression: "SET #attrName.#attrName2.#attrName3 = list_append(#attrName.#attrName2.#attrName3, :location), #attrName.#attrName2.#attrName4 = list_append(#attrName.#attrName2.#attrName4, :accelerometer)",
        ExpressionAttributeNames : {
        "#attrName" : "Players",
        "#attrName2" : user,
        "#attrName3" : "Locations",
        "#attrName4" : "AccelerometerData"
        },
        ExpressionAttributeValues: {
            ":location": {
               "L": [{"M":{
                    "Longitude" : {"N" : data.Location.Longitude.toString()},
                    "Latitude" : {"N": data.Location.Latitude.toString()}
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
        }
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
            "isFinished": {'BOOL':  false},
            "Type": {'N' : '1'},
            "Description": {'S' : "Drive to (" + type1data.Latitude.toString() + ", " + type1data.Longitude.toString() + ")"},
            "OnPhoneCheckable": {'BOOL' : true}
           }
        }
}
    
function type2Mission(){
    return {"M" :{
            "isFinished": {'BOOL':  false},
            "Type": {'N' : '2'},
            "Description": {'S' : "Gather with your team."},
            "OnPhoneCheckable": {'BOOL' : false}
           }
        }
}

function type3Mission(){
    return {"M" :{
            "isFinished": {'BOOL':  false},
            "Type": {'N' : '3'},
            "Description": {'S' : "Drive a height difference of " + rijHoogteverschil() + "m."},
            "OnPhoneCheckable": {'BOOL' : false}
           }
        }
}

function rijHoogteverschil(){
	return (Math.random()*5+5.0).toFixed(2);
}
    
}
        
                      