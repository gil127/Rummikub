/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

setGameDetails();

function setGameDetails() {
    $.ajax({
        url: "GameDetails?method=getGameDetails",
        async: false,
        timeout: 2000
    }).done(function (response) {
        var gameDetails = JSON.parse(response);
        $("body").append("<div align = \"center\"><label> Game name: " + gameDetails[0] + "</label></div>");
        $("body").append("<div align = \"center\"><label> Game Status: " + gameDetails[1] + "</label></div>");
        $("body").append("<div align = \"center\"><label> Number of human players: " + gameDetails[2] + "</label></div>");
        $("body").append("<div align = \"center\"><label> Number of computer players: " + gameDetails[3] + "</label></div>");
        $("body").append("<div align = \"center\"><label> Number of players who join: " + gameDetails[4] + "</label></div>");
        $("body").append("<div align = \"center\"><label> Loaded game: " + gameDetails[5] + "</label></div>");
    });
}