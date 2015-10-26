exports = module.exports = function(app){

app.get('/scoreboard/list', function(req, res){
    returnData(res, 1,{'List' : [
              { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 7, 'Missions' : 10},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 0, 'Games' : 4, 'Missions' : 3},
              { 'Name' : 'Koen', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 2, 'Games' : 9, 'Missions' : 12},
              { 'Name' : 'Kevin', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 3, 'Games' : 5, 'Missions' : 8},
              { 'Name' : 'Moran', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 9, 'Missions' : 11},
              { 'Name' : 'Elisabeth', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 4, 'Games' : 5, 'Missions' : 3},         { 'Name' : 'Jesse', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 7, 'Missions' : 10},
              { 'Name' : 'Jean', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 0, 'Games' : 4, 'Missions' : 3},
              { 'Name' : 'Koen', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 2, 'Games' : 9, 'Missions' : 12},
              { 'Name' : 'Kevin', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 3, 'Games' : 5, 'Missions' : 8},
              { 'Name' : 'Moran', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 5, 'Games' : 9, 'Missions' : 11},
              { 'Name' : 'Elisabeth', 'ImageURL' : 'http://www.benveldkamp.nl/images/PERS/Smurfen-bril.jpg', 'Wins' : 4, 'Games' : 5, 'Missions' : 3}
          
    ]}, null);
});

}