/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$("#joinGame").on("click", function () {
    var playerName = $("#playerName").val();
    var gameName = $("#gameName").val();

    if (playerName.replace(/\s/g, "").length === 0) { //playerName is empty
        $("p").text("Please enter a player name");
    }
    else if(gameName.replace(/\s/g, "").length === 0) { //playerName is empty
        $("p").text("Please enter a game name");
    }
    else {
        var parameters = "gameName=" + document.getElementById("gameName").value + "&playerName=" + document.getElementById("playerName").value;
        $.ajax({
            data: parameters,
            url: "joinGame?method=joinGame",
            timeout: 2000,
            error: function () {
                console.error("Failed to submit");
            },
            success: function (data) {
                var obj = JSON.parse(data);
                if (obj[0] === "true") {
                    window.location.href = obj[1];
                }
                else {
                    $("p").text(obj[1]);
                }
            }
        });
    }
});

$("#gameDetails").on("click", function () {
    var gameName = $("#gameName").val();

    if (gameName.replace(/\s/g, "").length === 0) { //playerName is empty
        $("p").text("Please enter a game name");
    }
    else {
        var parameters = "gameName=" + document.getElementById("gameName").value;

        $.ajax({
            data: parameters,
            url: "joinGame?method=displayGameDetails",
            timeout: 2000,
            error: function () {
                console.error("Failed to submit");
            },
            success: function (data) {
                var obj = JSON.parse(data);
                if (obj[0] === "true") {
                    window.location.href = obj[1];
                }
                else {
                    $("p").text(obj[1]);
                }
            }
        });
    }
});
