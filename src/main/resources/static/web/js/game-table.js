var app = new Vue ({
    el: "#app",
    data: {
        games: [],
        scores: []
    },
    methods:{
        findData: function () {
            $.get('/api/games', function (data) {
                app.games = data;
                app.scoresPlayers(app.games);
            })
        },
        scoresPlayers: function (games){
            for (i = 0 ; i < games.length; i++){
               var gamePlayers = games[i].gamePlayers;

               for (j = 0 ; j< gamePlayers.length; j++){
                var index =  app.scores.findIndex(scorePlayer => scorePlayer.player === gamePlayers[j].player.userName);
                if ( index == -1 ){
                    var scorePlayer = {
                        player: gamePlayers[j].player.userName,
                        nLoss: 0,
                        nTies: 0,
                        nWins: 0,
                        nTotal: 0,
                    };

                    if (gamePlayers[j].score.score == 0.0) {scorePlayer.nLoss ++}
                    else if (gamePlayers[j].score.score == 0.5) {scorePlayer.nTies ++}
                    else if (gamePlayers[j].score.score == 1.0) {scorePlayer.nWins ++};

                    scorePlayer.nTotal += gamePlayers[j].score.score;

                    app.scores.push(scorePlayer);
                }
                else{
                    if (gamePlayers[j].score.score == 0.0) {app.scores[index].nLoss ++}
                    else if (gamePlayers[j].score.score == 0.5) {app.scores[index].nTies ++ }
                    else if (gamePlayers[j].score.score == 1.0) {app.scores[index].nWins ++ };
                if (gamePlayers[j].score.score !=null) {
                    app.scores[index].nTotal += gamePlayers[j].score.score;
                }
                }
               };
            }
        }
    }
})

app.findData();

