exports = module.exports = function(app, AWS){
var defaultImage = "http://52.26.187.234:8080/defaultProfile.jpg";
app.get('/scoreboard/list', function(req, res){
var dynamodbDoc = new AWS.DynamoDB.DocumentClient();
user = getUserByToken(res, req.headers, function(user){
    if(user != undefined){
        var params = {
            TableName : "Users"
        };    

        dynamodbDoc.scan(params, function(err, data) {
            if (err) {
                console.error("Unable to query. Error:", JSON.stringify(err, null, 2));
            } else {
                var result = [];
                for (var i in data.Items) {
                    value = data.Items[i];
                    var item = {};
                    item.Username = data.Items[i].Username;
                    item.ImageURL = defaultImage;
                    if(data.Items[i].ImageURL != null)
                        item.ImageURL = data.Items[i].ImageURL;
                    item.Wins = undefinedIntToZero(data.Items[i].Wins);
                    item.Games = undefinedIntToZero(data.Items[i].Games);
                    item.Missions = undefinedIntToZero(data.Items[i].Missions);
                    
                    var added = false;
                    /*for (var i in result) {
                        if(checkIfBetter(req.body.Type, result[i], item)){
                            result.splice(i, 0, item);
                            added = true;
                            break;
                        }
                    }*/
                    if(! added)
                        result.push(item);
                    
                }  
                returnData(res, 1,{'List' :result}, null);
            }
        });
    }
});
});

}

function undefinedIntToZero(val){
    if(val == undefined){
        val = 0;
    }
    return val;
}

function checkIfBetter(type, prev, item){
    //Not used, is implemented in the app
    if(type == "Wins"){
        if(prev.Wins != item.Wins)
            return prev.Wins < item.Wins;
        if(prev.Games != item.Games)
           return prev.Games > item.Games;
        return prev.Missions < item.Missions;
    }
    else if(type == "Games"){
        if(prev.Games != item.Games)
            return prev.Games < item.Games;
        if(prev.Wins != item.Wins)
            return prev.Wins < item.Wins;
        return prev.Missions < item.Missions;
    }
    else if(type == "Missions"){
        if(prev.Missions != item.Missions)
            return prev.Missions < item.Missions;
        if(prev.Games != item.Games)
           return prev.Games > item.Games;
        return prev.Wins < item.Wins;
    }
}