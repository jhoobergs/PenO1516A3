exports = module.exports = function(app ,AWS, dd){

app.get('/friends/list', function(req, res){
    user = getUserByToken(res, req.headers, function(user){
    if(user != undefined){
        getUser(user, function(data){
            if (data == null) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                var result = [];
                var number = 0;
                if(data.Items[0].Friends != null){
                    if(Object.keys(data.Items[0].Friends).length == 0){
                        returnData(res, 1, {'List' : result}, null);
                    }
                    for (var i in data.Items[0].Friends) {
                        getUser(i, function(friendData){
                            if(friendData != null){
                            if(friendData.Items[0].ImageURL == null)
                                friendData.Items[0].ImageURL = 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg';
                                //console.log(data.Items[0].Friends[friendData.Items[0].Username]);
                                //console.log(friendData.Items[0].Username);
                            result.push(
                                {
                                  "Name" : friendData.Items[0].Username,
                                  "ImageURL": friendData.Items[0].ImageURL,
                                  "Accepted" : data.Items[0].Friends[friendData.Items[0].Username].Accepted,
                                  "IsSender": data.Items[0].Friends[friendData.Items[0].Username].IsSender
                                });
                            }
                            number++;
                            if(number == Object.keys(data.Items[0].Friends).length){
                                returnData(res, 1, {'List' : result}, null);
                            }
                        });                    
                    }
                }
                
                
            }
        });
    
    }
    });
});
    
app.post('/friends/search', function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    user = getUserByToken(res, req.headers, function(user){
    if(user != undefined){
        if(req.body != null){
            if(req.body.SearchValue == null || req.body.SearchValue == ""){
                returnData(res, 0, null, '{Not all params present}');
            }
            else{
        var params = {
            TableName : "Users",
            ScanFilter: {
            Username: {
                ComparisonOperator: 'CONTAINS', 
      AttributeValueList: 
        {
            S:formatUsername(req.body.SearchValue)
        }
        
        }
            }
        }

        dynamodbDoc.scan(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                var result = [];
                for (var i in data.Items) {
                    var value = data.Items[i];
                    var item = {};
                    if(value.Username != user){
                    item.Name = data.Items[i].Username;
                    item.ImageURL = 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg';
                    if(value.ImageURL != null)
                        item.ImageURL = value.ImageURL;
                    result.push(item);
                    }
                    
                }  
                returnData(res, 1,{'List' :result}, null);
            }
        });
            }
        }
        else{
            returnData(res, 0, null, '{Not all params present}');
        }
    }
    });
});

app.post('/friends/add',  function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.Username == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                    getUser(user, function(data){
                        if(data != null){
                        var friends = data.Items[0].Friends;
                        var found = false;
                        var accepted = false;
                        var isSender= true; //Dit betekent dat de gebruik die data krijgt de verstuurder is.
                        for (var i in friends) {
                            if(i == req.body.Username){
                                found = true;
                                accepted = friends[i].Accepted;
                                isSender = friends[i].IsSender;
                                break;
                            }
                        }

                        if((!found || (found && !accepted && !isSender)) && !(user == req.body.Username)){
                            //console.log("adding");
                            if(found){
                                accepted = true;
                            }
                                
                            addFriendToUser(!isSender, accepted, req.body.Username, user, function(succes){
                                if(succes){
                                    addFriendToUser(isSender, accepted, user, req.body.Username, function(succes){
                                    if(succes){
                                        var result = {};
                                        returnData(res, 1, result, null);
                                    }
                                    else{
                                        returnData(res, 0, null, '{UpdateItemError}');
                                    }
                                    });
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
                        
                    });
                }
            });
        }
    }

});
    
    
app.post('/friends/remove',  function(req, res){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    if(req.body != null){
        if(req.body.Username == null){
            returnData(res, 0, null, '{Not all params present}');
        }
        else{
            getUserByToken(res, req.headers, function(user){
                if(user != undefined){
                    //console.log(user);
                    getUser(user, function(data){
                        if(data != null){
                        var friends = data.Items[0].Friends;
                        var found = false;
                        for (var i in friends) {
                            if(i == req.body.Username){
                                found = true;
                                break;
                            }
                        }                            
                        if(found){
                            //console.log("removing");
                            removeFriendFromUser(req.body.Username, user, function(succes){
                                if(succes){
                                    removeFriendFromUser(user, req.body.Username, function(succes){
                                    if(succes){
                                        var result = {};
                                        returnData(res, 1, result, null);
                                    }
                                    else{
                                        returnData(res, 0, null, '{RemoveItemError}');
                                    }
                                    });
                                }
                                else{
                                    returnData(res, 0, null, '{RemoveItemError}');
                                }
                            });
                        }
                        else{
                            var result = {};
                            returnData(res, 1, result, null);
                        }
                        }
                        
                    });
                }
            });
        }
    }

});
    
addFriendToUser = function(isSender, accepted, friend, user, callback){
    dd.updateItem({
        TableName: "Users",
        Key: {
            "Username": {
                "S": friend
            }
        },
        UpdateExpression: "SET #attrName.#attrName2 = :user",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        ExpressionAttributeNames : {
        "#attrName" : "Friends",
        "#attrName2" : user
        },
        ExpressionAttributeValues: {
            ":user": {
                "M": {
                    "DateAdded" : {"S" : new Date().toISOString()},
                    "IsSender" : {"BOOL" :  isSender},
                    "Accepted" : {"BOOL" : accepted}                                  
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

removeFriendFromUser = function(friend, user, callback){
    dd.updateItem({
        TableName: "Users",
        Key: {
            "Username": {
                "S": friend
            }
        },
        UpdateExpression: "REMOVE #attrName.#attrName2",
        //UpdateExpression : "SET #attrName = list_append(#attrName, :user)",
        ExpressionAttributeNames : {
        "#attrName" : "Friends",
        "#attrName2" : user
        }
    }, function(err, data) {
        if(err){
            console.log(err);
            callback(false)
        }
        else
        {
            //console.log("ok");
            callback(true);
        }
    });
}
}