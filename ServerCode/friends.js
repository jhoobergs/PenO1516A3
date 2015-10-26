exports = module.exports = function(app){

app.get('/friends/list', function(req, res){
    returnData(res, 1, {'List' : [
              { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'}
              ]}, null);
});

}