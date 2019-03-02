/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function checkIfGameCanStart() {

    $.ajax({
        url: "WaitRoom",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (response) {
            var responseParse = JSON.parse(response);
            var isGameCanStart = responseParse[0];

            if (isGameCanStart === "true") {
                window.location.href = responseParse[1];
            }
        }
    });
}

$(function () {
    $.ajaxSetup({cache: false});
    setInterval(checkIfGameCanStart, 2000);
});