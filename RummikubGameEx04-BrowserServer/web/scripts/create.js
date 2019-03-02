/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$("#newGameBtn").on("click", function () {
    var parameters = $("form").serialize();

    $.ajax({
        data: parameters,
        url: "CreateNewGame",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (data) {
            //  data.redirect contains the string URL to redirect to
            var obj = JSON.parse(data);
            if (obj[0] === "false") {
                window.location.href = obj[1];
            }
            else{
                 $("p").text(obj[1]);
            }
        }
    });
    return false;
});


function changeComboBoxVals() {
    changeAmountOfHumanPlayersVals();
}

function changeAmountOfHumanPlayersVals() {
    var amountOfPlayers = $(".numOfPlayers").val();

    $(".numOfHumanPlayers").empty();

    for (var i = 1; i <= amountOfPlayers; i++) {
        $(".numOfHumanPlayers").append("<option value=" + i + ">" + i + "</option>");
    }
}

$(".numOfPlayers").change(changeComboBoxVals);
changeComboBoxVals();

$(".playerName").change(function () {
    if ($(".playerName").val().replace(/\s/g, "").length !== 0) { //playerName is not empty
        $("p").text("");
    }
});
