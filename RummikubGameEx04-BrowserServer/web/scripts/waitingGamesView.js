/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

setWaitingGames();

function setWaitingGames() {
    $.ajax({
        url: "WaitingGames?method=displayWaitingGames",
        async: false,
        timeout: 2000
    }).done(function (response) {
        var waitingGame = JSON.parse(response);
        var amountOfNames = Object.keys(waitingGame).length;

        for (var i = 0; i < amountOfNames; i++) {
            $("table").append("<tr><td>" + waitingGame[i] + "</td></tr>");
        }
    });
}