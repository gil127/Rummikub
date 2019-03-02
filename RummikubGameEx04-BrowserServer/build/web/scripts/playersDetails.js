/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

setGameDetails();

function setGameDetails() {
    $.ajax({
        url: "PlayersDetails?method=getPlayersDetails",
        async: false,
        timeout: 2000
    }).done(function (response) {
        var gameDetails = JSON.parse(response);
        var length = gameDetails.length;
        for (var i = 0; i < length; i++) {
            $("body").append("<div align = \"center\"><label>" + gameDetails[i] + "</label></div>");
        }
    });
}