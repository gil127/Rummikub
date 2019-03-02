/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


setMsg();

function setMsg() {
    $.ajax({
        url: "GameOver?method=getMsg",
        async: false,
        timeout: 2000
    }).done(function (response) {
        $("#msg").append(response);
    });
}