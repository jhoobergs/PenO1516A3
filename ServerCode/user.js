exports = module.exports = function(app, AWS, bcrypt,dd){

app.post('/user/login', function(req, res) {
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
console.log(req.body);
if(req.body != null){
    if(req.body.Username == null ||req.body.Password == null){
        //res.send("{Not all params present}");
        returnData(res, 0, null, '{Not all params present}');
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
                //console.log("Logging in");
                if(data.Items.length == 1){
                    if(bcrypt.compareSync(req.body.Password, data.Items[0].Password)){
                            createToken(data.Items[0].Username, function(token) {
                            var result = {
                                'Username' : data.Items[0].Username,
                                'Token' : token
                            };
                            returnData(res, 1, result, null);
                        });                    
                    
                    }                
                    else{
                        var error = {'Errors' : [1]};
                        returnData(res, 2, null, error);
                                        
                    }
                }
                else{
                        var error = {'Errors' : [1]};
                        returnData(res, 2, null, error);
                    }
            }
        });
    }
}
else{
    returnData(res, 0, null, '{Not all params present}');
}  
});

app.post('/user/create', function(req, res) {
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
console.log(req.body);
var error = "";
if(req.body != null){
    //console.log("Creating for Users with id.");
    if(req.body.Username == null ||req.body.Password == null || req.body.Password != req.body.PasswordRepeat || req.body.Email == null){
        returnData(res, 0, null, '{Not all params present}');
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
                //console.log("Query succeeded.");
                if(data.Items.length == 0){
                    //Ok, we can create the user                            
                    putnewUserItem(req.body);
                    createToken(req.body.Username, function(token) {
                        var result = {
                            'Username' : req.body.Username,
                            'Token' : token
                        };
                        returnData(res, 1,result, null);
                    });                    
                }
                else{
                var error = {'Errors' : [2]};
                returnData(res, 2, null, error);
                }
            }
        });
    }
}
else{
    returnData(res, 0, null, '{Not all params present}');
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

putnewTokenItem = function(Username, Token) {   
    var tableName = 'Tokens';
    var item = {
	    'Token' : { 'S': Token },
	    'Username' : { 'S' : Username}
    };	
    dd.putItem({
        'TableName': tableName,
        'Item': item
    }, function(err, data) {
        err && console.log(err);
    });
};

function createToken(Username, callback){
    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
    var salt = bcrypt.genSaltSync(10);
                    var token = bcrypt.hashSync(salt, salt); //Just some randomness
                    
                    var checkIfTokenExistsParams = {
                            TableName : "Tokens",
                            KeyConditionExpression: "#token = :name",
                            ExpressionAttributeNames:{
                            "#token": "Token"
                        },
                        ExpressionAttributeValues: {
                            ":name":token
                        }                    
                    };
                    
                    dynamodbDoc.query(checkIfTokenExistsParams, function(err, data) {
                        if (err) {
                            console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
                        } else {
                            if(data.Items.length  == 0){
                                putnewTokenItem(Username, token);
                                callback(token);
                            }
                            else{
                                createToken(Username);
                            }
                        }
                    });
    
}

}