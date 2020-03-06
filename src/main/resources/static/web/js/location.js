var app = new Vue ({
    el: "#app",
    data: {
        getIdUrl: null,
        columns: ["1","2","3","4","5","6","7","8","9","10"],
        rows: ["A","B","C","D","E","F","G","H","I","J"],
        view: {},
        opponentMail : {},
        mainPlayerMail : {},
    },
    methods: {
        findGame : function(){
            const urlParams = new URLSearchParams(window.location.search);
            app.getIdUrl = urlParams.get('Gp');
        },
        findGameView : function () {
            $.get('/api/game_view/'+ app.getIdUrl, function (data) {
                app.view = data;
                app.locatePlayer();
                app.locateShip();
                app.locateSalvoes();
            })
        },
        locateShip : function(){
        for (i=0 ; i< app.view.ships.length ; i++){
            for (k=0; k <app.view.ships[i].locations.length; k++){
                var element = document.getElementById (app.view.ships[i].locations[k]);
                element.classList.add('location');

                for( j = 0 ; j < app.view.salvoes.length; j++ ){
                    if(app.opponentMail.id == app.view.salvoes[j].playerId)
                        for(l = 0 ; l < app.view.salvoes[j].locations.length ; l++){
                            if(app.view.salvoes[j].locations[l] == app.view.ships[i].locations[k])
                            element.innerHTML = "x";
                        }
                }
            }
        }
       },

       locatePlayer : function() {
       for (i=0 ; i < app.view.gamePlayers.length ; i++){
            if (app.view.gamePlayers[i].id == app.getIdUrl)
                app.mainPlayerMail = app.view.gamePlayers[i].player
            else
                app.opponentMail = app.view.gamePlayers[i].player

       }
       },
       locateSalvoes : function(){
               for (i=0 ; i< app.view.salvoes.length ; i++){
                   for (j=0; j < app.view.salvoes[i].locations.length; j++){
                        if (app.mainPlayerMail.id == app.view.salvoes[i].playerId){
                       var elements = document.getElementById (app.view.salvoes[i].locations[j]);
                       elements.classList.add('salvoes')}
                        else {
                        var elements2 = document.getElementById (app.view.salvoes[i].locations[j]+"O");
                                                     elements2.classList.add('salvoes2');
                                                     elements2.innerHTML = app.view.salvoes[i].turn;

                        }
                   }
               }
              },
}
})
app.findGame();
app.findGameView();



