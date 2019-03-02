/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
uploadGameOnClick();

function uploadGameOnClick() {
    $.ajax({
        type: "GET",
        url: "uploadGame",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (data) {
            if (data !== null) {
                var obj = JSON.parse(data);
                window.location.href = obj[0];
                $("p").text(obj[1]);
            }
        }
    });
    return false;
}
