/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

setError();

function setError() {
    $.ajax({
        url: "errorPage?method=getError",
        async: false,
        timeout: 2000
    }).done(function (response) {
        $("p").text(response);
    });
}