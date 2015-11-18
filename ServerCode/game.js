exports = module.exports = function(app, AWS, dd){

app.post('/game/create', function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        console.log(req.body);
        if(req.body.Name == null ||req.body.MinPlayers == null || req.body.MaxPlayers == null ||req.body.MinPlayers >                       req.body.MaxPlayers || req.body.CenterLocationLatitude == null || req.body.CenterLocationLongitude == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            console.log(getDistanceFromLatLonKmVincenty(req.body.CenterLocationLatitude, req.body.CenterLocationLongitude,
                                                        req.body.CenterLocationLatitude+10, req.body.CenterLocationLongitude+10));
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
                                console.log("gelijk");
                                found = true;
                                break;
                            }
                        }
                        if(!found && Object.keys(players).length< data.Items[0].MaxPlayers){
                            addPlayerToGame(req.body.GameId, user, function(succes){
                                if(succes){
                                    var result = {};
                                    returnData(res, 1, result, null);
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
                    item.Players = Object.keys(gamedata.Players);
                    item.CenterLocation = gamedata.CircleCenter;
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
        if(req.body.GameId == null || req.body.Accelerometer == null || req.body.Location == null){
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
    
    var tableName = 'Games';
    var players = {"M" : {}};
    players.M[username] = {"M":{"DateAdded" : {"S" : new Date().toISOString()} }};
    var item = {
	    'GameId' : { 'S': id },
        'Name' : { 'S' : data.Name},
        'CreatedOn' : {'S' : new Date().toISOString()},
	    'MinPlayers' : { 'N' : data.MinPlayers.toString()},
        'MaxPlayers' : { 'N' : data.MaxPlayers.toString()},
        'Players' : players,
        'CircleCenter' : {
                            "M" : {
                                    Latitude:  {"N" : data.CenterLocationLatitude.toString()},
                                    Longitude: {"N" : data.CenterLocationLongitude.toString()}        
                                   }
        }
    };	
    dd.putItem({
        'TableName': tableName,
        'Item': item
    }, function(err, data) {
        err && console.log(err);
    });
};
    
addPlayerToGame = function(gameId, user, callback){
    dd.updateItem({
        TableName: "Games",
        Key: {
            "GameId": {
                "S": gameId
            }
        },
        //UpdateExpression: "ADD #attrName = :user",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        UpdateExpression: "SET #attrName.#attrName2 = :user",
        ExpressionAttributeNames : {
        "#attrName" : "Players",
        "#attrName2" : user
        },
        ExpressionAttributeValues: {
            ":user": {
                "M":{
                    "DateAdded" : {"S" : new Date().toISOString()},
                    "Locations" : {"L" : []},
                    "AccelerometerData" : {"L" : []}
                }
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
    
    
}
        
                      