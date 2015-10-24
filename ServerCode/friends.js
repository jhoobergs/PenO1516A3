exports = module.exports = function(app){

app.get('/friends/list', function(req, res){
    res.send({'List' : [
              { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg'}
              ]});
});

}