exports = module.exports = function(app, AWS, dd){

app.post('/game/create', function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.Name == null ||req.body.MinPlayers == null || req.body.MaxPlayers == null ||req.body.MinPlayers >                       req.body.MaxPlayers){
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
                            value = players[i];
                            console.log(value);
                            if(value.Name == user){
                                found = true;
                                break;
                            }
                        }
                        if(!found && players.length < data.Items[0].MaxPlayers){
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

putnewGameItem = function(data, username, id) {           
    
    var tableName = 'Games';
    var item = {
	    'GameId' : { 'S': id },
        'Name' : { 'S' : data.Name},
        'CreatedOn' : {'S' : new Date().toISOString()},
	    'MinPlayers' : { 'N' : data.MinPlayers.toString()},
        'MaxPlayers' : { 'N' : data.MaxPlayers.toString()},
        'Players' : {
            "L": [{
                "M":{
                    "Name" : {'S' : username}
                }
            }
                ]
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
        UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        ExpressionAttributeNames : {
        "#attrName" : "Players"
        },
        ExpressionAttributeValues: {
            ":user": {
                "L": [{
                "M":{
                    "Name" : {"S" : user}
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
        
                      