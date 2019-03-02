/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var playersName;
setPlayersNames();

function setPlayersNames() {
    $.ajax({
        url: "chooseNames?method=getNames",
        async: false,
        timeout: 2000
    }).done(function (response) {
        playersName = JSON.parse(response);
    });
    var amountOfplayersNames = Object.keys(playersName).length;
    $(".namesOfPlayer").empty();

    for (var i = 0; i < amountOfplayersNames; i++) {
        $(".namesOfPlayer").append("<option value=" + playersName[i] + ">" + playersName[i] + "</option>");
    }
}

$("#joinGame").on("click", function () {

    var parameters = $("form").serialize();

    $.ajax({
        data: parameters,
        url: "chooseNames?method=removeChosenName",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (data) {
            var responseParse = JSON.parse(data);
            if (responseParse[0] === "true") {
                window.location.href = responseParse[1];
            }
            else {
                $("p").text(responseParse[1]);
            }
        }
    });

    return false;
});
