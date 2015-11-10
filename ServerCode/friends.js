exports = module.exports = function(app ,AWS){

app.get('/friends/list', function(req, res){
    user = getUserByToken(res, req.headers, function(user){
    if(user != undefined){
        getUser(user, function(data){
            if (data == null) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                console.log(data);
                var result = [];
                var number = 0;
                if(data.Items[0].Friends != null){
                    for (var i in data.Items[0].Friends) {
                        console.log(i);
                        friend = getUser(data.Items[0].Friends[i], function(friendData){
                            if(friendData.Items[0].ImageURL == null)
                                friendData.Items[0].ImageURL = 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg';
                            result.push({"Name" : friendData.Items[0].Username, "ImageURL": friendData.Items[0].ImageURL});
                            number++;
                            if(number == data.Items[0].Friends.length){
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

}