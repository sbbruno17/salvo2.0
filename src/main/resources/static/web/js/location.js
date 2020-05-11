var app = new Vue({
    el: "#app",
    data: {
        getIdUrl: null,
        columns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        view: {},
        shipLocation: [],
        opponentMail: {},
        mainPlayerMail: {},
        salvoes: {
            turn: 0,
            locations: []
        },
    },
    methods: {
        findGame: function () {
            const urlParams = new URLSearchParams(window.location.search);
            app.getIdUrl = urlParams.get('gp');
        },
        findGameView: function () {
            app.findGame();
            $.get('/api/game_view/' + app.getIdUrl, function (data) {
                app.view = data;
                app.locatePlayer();
                app.locateSalvoes();
                app.gridCreation();
                app.firedSalvoes();

            })
        },

        locatePlayer: function () {
            for (i = 0; i < app.view.gamePlayers.length; i++) {
                if (app.view.gamePlayers[i].id == app.getIdUrl)
                    app.mainPlayerMail = app.view.gamePlayers[i].player
                else
                    app.opponentMail = app.view.gamePlayers[i].player

            }
        },
        locateSalvoes: function () {
            for (i = 0; i < app.view.salvoes.length; i++) {
                for (j = 0; j < app.view.salvoes[i].locations.length; j++) {
                    if (app.mainPlayerMail.id == app.view.salvoes[i].playerId) {
                        // var elements = document.getElementById(app.view.salvoes[i].locations[j]);
                        //elements.classList.add('salvoes')
                    } else {
                        var elements2 = document.getElementById(app.view.salvoes[i].locations[j]);
                        elements2.classList.add('salvoes2');
                        elements2.innerHTML = app.view.salvoes[i].turn;

                    }
                }
            }
        },
        saveShips: function () {
            $(".grid-stack-item").each(function () {
                var coordinate = [];
                var ship = {
                    type: "",
                    locations: ""
                };
                if ($(this).attr("data-gs-width") !== "1") {
                    for (var i = 0; i < parseInt($(this).attr("data-gs-width")); i++) {
                        coordinate.push(String.fromCharCode(parseInt($(this).attr("data-gs-y")) + 65) + (parseInt($(this).attr("data-gs-x")) + i + 1).toString());
                    }
                } else {
                    for (var i = 0; i < parseInt($(this).attr("data-gs-height")); i++) {
                        coordinate.push(String.fromCharCode(parseInt($(this).attr("data-gs-y")) + i + 65) + (parseInt($(this).attr("data-gs-x")) + 1).toString());
                    }
                }

                ship.type = $(this).attr("data-gs-id");
                ship.locations = coordinate;
                app.shipLocation.push(ship);


            })
        },
        addShips: function () {
            app.saveShips();
            $.post({
                    url: "/api/games/players/" + app.getIdUrl + "/ships",
                    data: JSON.stringify(app.shipLocation),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done(function () {
                    alert("Ships added Succesfully");
                    location.reload();
                })
                .fail(function () {
                    alert("error");
                })
        },
        gridCreation: function () {
            const options = {
                //grilla de 10 x 10
                column: 10,
                row: 10,
                //separacion entre elementos (les llaman widgets)
                verticalMargin: 0,
                //altura de las celdas
                disableOneColumnMode: true,
                //altura de las filas/celdas
                cellHeight: 36,
                //necesario
                float: true,
                //desabilitando el resize de los widgets
                disableResize: true,
                //false permite mover los widgets, true impide
                staticGrid: false,
                animate: true,
            }

            //CREACION DE GRILLA Y WIDGETS (NAVES) CUANDO NO HAY SHIPS EN JUEGO

            if (app.view.ships.length == 0) {
                const grid = GridStack.init(options, '#grid');
                grid.addWidget('<div><div id="Submarine" class="grid-stack-item-content SubmarineHorizontal"></div><div/>', {
                        width: 3,
                        heigth: 1,
                        x: 0,
                        y: 0,
                        noResize: true,
                        id: "Submarine"
                    }),
                    grid.addWidget('<div><div id="Carrier" class="grid-stack-item-content CarrierHorizontal"></div><div/>', {
                        width: 5,
                        heigth: 1,
                        x: 0,
                        y: 0,
                        noResize: true,
                        id: "Carrier"
                    }),
                    grid.addWidget('<div><div id="PatrolBoat" class="grid-stack-item-content PatrolBoatHorizontal"></div><div/>', {
                        width: 2,
                        heigth: 1,
                        x: 0,
                        y: 0,
                        noResize: true,
                        id: "PatrolBoat"
                    }),
                    grid.addWidget('<div><div id="Destroyer" class="grid-stack-item-content DestroyerHorizontal"></div><div/>', {
                        width: 3,
                        heigth: 1,
                        x: 0,
                        y: 0,
                        noResize: true,
                        id: "Destroyer"
                    }),
                    grid.addWidget('<div><div id="Battleship" class="grid-stack-item-content BattleshipHorizontal"></div><div/>', {
                        width: 4,
                        heigth: 1,
                        x: 0,
                        y: 0,
                        noResize: true,
                        id: "Battleship"
                    })

                //CONFIGURACION DE LAS NAVES

                $("#Carrier,#Battleship,#Submarine,#Destroyer,#PatrolBoat").click(function () {
                    var idShip = $(this)[0].id;
                    var widthShip = parseInt($(this)[0].dataset.gsWidth);
                    var heigthShip = parseInt($(this)[0].dataset.gsHeight);
                    var x = parseInt($(this)[0].dataset.gsX);
                    var y = parseInt($(this)[0].dataset.gsY);
                    var yShip = 10 - widthShip;
                    var xShip = 10 - heigthShip;

                    if ($(this).children().hasClass(idShip + "Horizontal") && y <= yShip && grid.isAreaEmpty(x, y + 1, heigthShip, widthShip)) {
                        console.log(yShip)
                        console.log("vertical")
                        grid.resize($(this), heigthShip, widthShip);
                        $(this).children().removeClass(idShip + "Horizontal");
                        $(this).children().addClass(idShip + "Vertical");
                    } else if ($(this).children().hasClass(idShip + "Vertical") && x <= xShip && grid.isAreaEmpty(x + 1, y, heigthShip, widthShip)) {
                        console.log(xShip)
                        console.log("horizontal")
                        grid.resize($(this), heigthShip, widthShip);
                        $(this).children().addClass(idShip + "Horizontal");
                        $(this).children().removeClass(idShip + "Vertical");
                    }
                });

                //rotacion de las naves
                //obteniendo los ships agregados en la grilla
                const ships = document.querySelectorAll("#Carrier,#Battleship,#Submarine,#Destroyer,#PatrolBoat");
                ships.forEach(ship => {
                    //asignando el evento de click a cada nave
                    ship.parentElement.onclick = function (event) {
                        //obteniendo el ship (widget) al que se le hace click
                        let itemContent = event.target;
                        //obteniendo valores del widget
                        let itemX = parseInt(itemContent.parentElement.dataset.gsX);
                        let itemY = parseInt(itemContent.parentElement.dataset.gsY);
                        let itemWidth = parseInt(itemContent.parentElement.dataset.gsWidth);
                        let itemHeight = parseInt(itemContent.parentElement.dataset.gsHeight);

                        //si esta horizontal se rota a vertical sino a horizontal
                        if (itemContent.classList.contains(itemContent.id + 'Horizontal')) {
                            //veiricando que existe espacio disponible para la rotacion
                            if (grid.isAreaEmpty(itemX, itemY + 1, itemHeight, itemWidth - 1) && (itemY + (itemWidth - 1) <= 9)) {
                                //la rotacion del widget es simplemente intercambiar el alto y ancho del widget, ademas se cambia la clase
                                grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                                itemContent.classList.remove(itemContent.id + 'Horizontal');
                                itemContent.classList.add(itemContent.id + 'Vertical');
                            } else {
                                alert("Espacio no disponible");
                            }
                        } else {
                            if (grid.isAreaEmpty(itemX + 1, itemY, itemHeight - 1, itemWidth) && (itemX + (itemHeight - 1) <= 9)) {
                                grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                                itemContent.classList.remove(itemContent.id + 'Vertical');
                                itemContent.classList.add(itemContent.id + 'Horizontal');
                            } else {
                                alert("Espacio no disponible");
                            }
                        }
                    }
                })
            } else {
                options.staticGrid = true;
                const grid = GridStack.init(options, '#grid');
                for (var i = 0; i < app.view.ships.length; i++) {

                    var ship = app.view.ships[i];

                    let xShip = parseInt(ship.locations[0].slice(1)) - 1;
                    let yShip = parseInt(ship.locations[0].slice(0, 1).charCodeAt(0)) - 65;

                    if (ship.locations[0][0] == ship.locations[1][0]) {

                        widthShip = ship.locations.length;
                        heigthShip = 1;

                        grid.addWidget('<div id="' + ship.type + '"><div class="grid-stack-item-content' + " " + ship.type + 'Horizontal"></div></div>', {
                            width: widthShip,
                            heigth: heigthShip,
                            x: xShip,
                            y: yShip,
                            noResize: true,
                            id: ship.type
                        })
                    } else {
                        widthShip = 1;
                        heigthShip = ship.locations.length;

                        grid.addWidget('<div id="' + ship.type + '"><div class="grid-stack-item-content' + " " + ship.type + 'Vertical"></div></div>', {
                            width: widthShip,
                            height: heigthShip,
                            x: xShip,
                            y: yShip,
                            noResize: true,
                            id: ship.type
                        })
                    }
                }
            }
        },
        salvoAim: function (salvo) {
            if (app.salvoes.locations.length < 5) {
                app.salvoes.locations.push(salvo)
            } else {
                console.log("Para de disparar, terminator")
            }
        },
        salvoFire: function () {
            if (app.salvoes.locations.lenght < 5) {
                alert("Seize yout bullets!")
            } else if (app.salvoes.locations.length == 5) {
                $.post({
                        url: "/api/games/players/" + app.getIdUrl + "/salvos",
                        data: JSON.stringify(app.salvoes),
                        dataType: "text",
                        contentType: "application/json"
                    })
                    .done(function () {
                        alert("Salvos fired!")
                        location.reload();

                    })
                    .fail(function () {
                        alert("Failed shooting salvo");
                    })
            }
        },
        firedSalvoes: function () {
            app.view.salvoes.forEach(x => {
                x.locations.forEach(y => {
                    document.getElementById(y).classList.add("firedSalvo");
                })
            })
        }
    }
});
app.findGameView();