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
                    //console.log(data.Items[0].Friends);
                    for (var i in data.Items[0].Friends) {
                        //console.log(i);
                        getUser(data.Items[0].Friends[i].Name, function(friendData){
                            if(friendData.Items[0].ImageURL == null)
                                friendData.Items[0].ImageURL = 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg';
                            result.push({"Name" : friendData.Items[0].Username, "ImageURL": friendData.Items[0].ImageURL});
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
            S:req.body.SearchValue
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
                    value = data.Items[i];
                    var item = {};
                    item.Name = data.Items[i].Username;
                    item.ImageURL = 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg';
                    if(data.Items[i].ImageURL != null)
                        item.ImageURL = data.Items[i].ImageURL;
                    result.push(item);
                    
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
                        for (var i in friends) {
                            if(friends[i] == req.body.Username){
                                found = true;
                                break;
                            }
                        }                            
                        if(!found){
                            console.log("adding");
                            addFriendToUser(req.body.Username, user, function(succes){
                                if(succes){
                                    addFriendToUser(user, req.body.Username, function(succes){
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
    
addFriendToUser = function(friend, user, callback){
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
                    "DateAdded" : {"S" : new Date().toISOString()}
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
}