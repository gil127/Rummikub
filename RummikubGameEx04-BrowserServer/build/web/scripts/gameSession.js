/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var boardOfTheGame;
var playerHand;
var currentPlayerName = "";
var playerTurn = false;
var isPlayerCanUseBoard = false;
var isPlayerStillPlay = false;
var action = "";
var serialNumberChoosen = 0;
var insertInto = "";
var numOfSerials = 0;

getPlayerHand();
changeVisibilityOfInsertButtons(true);
changeDisableOfButtons();
checkIfPlayerCanUseBoard();

$(function () {
    $.ajaxSetup({cache: false});
    setInterval(setBoard, 1000);
    setInterval(setLabel, 1000);
    setInterval(getIfGameIsOver, 1000);
    setInterval(checkIfPlayerTurn, 1000);
});

function setLabel() {
    if (!playerTurn) {
        $.ajax({
            url: "GameSession?method=getMsg",
            async: false,
            timeout: 2000
        }).done(function (response) {
            setLabelWithText(response);
        });
    }
}

function setLabelWithText(text) {
    $("#gameInfo").empty();
    $("#gameInfo").append(text);
}

function getPlayerHand() {
    $.ajax({
        url: "GameSession?method=getHand",
        async: false,
        timeout: 2000
    }).done(function (response) {
        playerHand = JSON.parse(response);
        var currHand = $("#playerHand");
        if (currHand.children($("cube")).length !== 0) {
            currHand.empty();
        }

        var numOfCubes = playerHand.length;

        for (var i = 0; i < numOfCubes; i++) {
            if (i % 14 === 0) {
                currHand.append("<br>")
            }
            currHand.append(playerHand[i]);
        }
    });
}

function finishTurnClicked() {
    $.ajax({
        url: "GameSession?method=finishTurnClicked",
        async: false,
        timeout: 2000
    }).done(function (response) {
        setLabelWithText(response)
        playerTurn = false;
        isPlayerStillPlay = false;
        checkIfPlayerCanUseBoard();
        changeDisableOfButtons();
        getPlayerHand();
    });
}

function getIfGameIsOver() {
    $.ajax({
        url: "GameSession?method=getGameOver",
        async: false,
        timeout: 2000
    }).done(function (response) {
        checkIfGameIsOver(response);
    });
}

function getBoardFromServer() {
    $.ajax({
        url: "GameSession?method=getBoard",
        async: false,
        timeout: 2000
    }).done(function (response) {
        boardOfTheGame = response;
    });
}

function setBoard() {
    if (!playerTurn) {
        updateBoard();
    }
}

function updateBoard()
{
    getBoardFromServer();
    var newBoard = JSON.parse(boardOfTheGame);

    if (newBoard.length !== 0) {
        var currBoard = $("#board");
        if (currBoard.children($("cubeBoard")).length !== 0) {
            currBoard.empty();
        }

        var length = newBoard.length;
        numOfSerials = 0;
        var serial = new Array(100);
        var numOfCubesInARow = 0;
        var index = 0;
        for (var i = 0; i < length; i++) {
            if (newBoard[i] === "<br>") {
                numOfSerials++;
                if (numOfCubesInARow < 15) {
                    currBoard.append("<Label>" + numOfSerials + "</Label>");
                    currBoard.append(serial);
                } else {
                    numOfCubesInARow = 0;
                    currBoard.append(newBoard[i]);
                    currBoard.append("<Label>" + numOfSerials + "</Label>");
                    currBoard.append(serial);
                }

                serial = [];
                serial = new Array(100);
                index = 0;
            } else {
                serial[index] = newBoard[i];
                index++;
                numOfCubesInARow++;
            }
        }


        setCubeButtonDisabled();
    }
}

function setCurrentPlayerName()
{
    $.ajax({
        url: "GameSession?method=getCurrentPlayerName",
        async: false,
        timeout: 2000
    }).done(function (response) {
        if (response !== null) {
            $("#currentPlayerName").html(response);
            if (response !== currentPlayerName)
            {
                currentPlayerName = response;
                $('#timerShow').countdown('option', {until: +60});
            }
        }
    });
}

function checkIfPlayerTurn() {
    if (!playerTurn) {
        $.ajax({
            url: "GameSession?method=isPlayerTurn",
            timeout: 2000
        }).done(function (response) {
            var temp = JSON.parse(response);
            if (temp !== playerTurn) {
                playerTurn = temp;
                if (playerTurn === true) {
                    getPlayerHand();
                    changeDisableOfActionButtons(false);
                } else {
                    changeDisableOfButtons();
                }
            }
        });
    }
}

function checkIfPlayerCanUseBoard() {
    $.ajax({
        url: "GameSession?method=IsPlayerCanUseBoard",
        timeout: 2000
    }).done(function (response) {
        var temp = JSON.parse(response);
        if (temp === true) {
            isPlayerCanUseBoard = true;
            if (isPlayerStillPlay) {
                changeDisableOfActionButtons();
            }
        } else {
            isPlayerCanUseBoard = false;
        }
    });
}

function changeDisableOfButtons() {
    var typeOfActionBtn = !playerTurn ? "disableBtn" : "btn";
    var typeOfActionWithBoard = !playerTurn || !isPlayerCanUseBoard ? "disableBtn" : "btn";
    $("#createSerialBtn").prop('class', typeOfActionBtn);
    $("#insertToSerialBtn").prop('class', typeOfActionWithBoard);
    $("#replaceJokerBtn").prop('class', typeOfActionWithBoard);
    $("#takeBackBtn").prop('class', typeOfActionWithBoard);
    $("#finishTrnBtn").prop('class', typeOfActionBtn);
    $("#cancelBtn").prop('class', typeOfActionBtn);
    $("#finishActionBtn").prop('class', typeOfActionBtn);

    setCubeButtonDisabled();
}

function setCubeButtonDisabled() {
    var disableButtons = !playerTurn || action === "";
    setPlayerHandDisable(disableButtons);
    setBoardDisable(disableButtons);
}

function setBoardDisable(disable) {
    if ($(".cubeBoard").length !== 0) {
        $(".cubeBoard").prop('disabled', disable || !isPlayerCanUseBoard);
    }
}

function setPlayerHandDisable(disable) {
    if ($(".cube").length !== 0) {
        $(".cube").prop('disabled', disable);
    }
}

function changeVisibilityOfInsertButtons(visible) {
    var value = visible ? "hidden" : "visible";
    document.getElementById("serialNumberToInsertComboBox").style.visibility = value;
    document.getElementById("insertOptionComboBox").style.visibility = value;
    document.getElementById("setSerialNumberBtn").style.visibility = value;

    if (!visible) {
        $("#finishActionBtn").prop('class', "disableBtn");
    } else {
        $("#finishActionBtn").prop('class', "btn");
    }

    setCubeButtonDisabled();

    $("#serialNumberToInsertComboBox").empty();

    for (var i = 1; i <= numOfSerials; i++) {
        $("#serialNumberToInsertComboBox").append("<option value=" + i + ">" + i + "</option>");
    }
}

function checkIfGameIsOver(response)
{
    if (response === "true")
    {
        window.location.href = "gameOver.html";
    }
}

function quitOnClick() {
    $.ajax({
        url: "GameSession?method=quitClicked",
        cache: false,
        async: false,
        timeout: 2000
    }).done(function (response) {
        checkIfGameIsOver(response);
    });
}

function createSeqClicked() {
    action = "create";
    changeDisableOfActionButtons(true);
}

function insertClicked() {
    changeVisibilityOfInsertButtons(false);
    changeDisableOfActionButtons(true);
}

function replaceClicked() {
    action = "replace";
    changeDisableOfActionButtons(true);
}

function takeBackTileClicked() {
    action = "takeBack";
    changeDisableOfActionButtons(true);
    setPlayerHandDisable(true);
}

function cancelClicked() {

    changeStateAfterFinishAction();
    emptyTempCubeView();
}

function emptyTempCubeView() {
    $("#tmpCurrentTurnView").empty();
    updateBoard();
    getPlayerHand();
}

function setPositionClicked() {
    serialNumberChoosen = document.getElementById("serialNumberToInsertComboBox").value;
    insertInto = document.getElementById("insertOptionComboBox").value;
    action = "insert";
    changeVisibilityOfInsertButtons(true);
}

function finishActionClicked() {
    var parameters = getParameters();
    $.ajax({
        url: "GameSession?method=doAction",
        cache: false,
        async: false,
        data: parameters,
        timeout: 2000
    }).done(function (data) {
        var response = JSON.parse(data);
        if (response[0] === "true") {
            isPlayerStillPlay = true;
        } else {
            setLabelWithText(response[1]);
        }
        getPlayerHand();
        updateBoard();
        $("#tmpCurrentTurnView").empty();
        checkIfPlayerCanUseBoard();
        changeStateAfterFinishAction();
    });
}

function changeStateAfterFinishAction() {
    changeVisibilityOfInsertButtons(true);
    action = "";
    changeDisableOfActionButtons(false);
    insertInto = "";
    serialNumberChoosen = 0;
    setCubeButtonDisabled();

}

function getParameters() {
    var cubes = $("#tmpCurrentTurnView").children();
    var parameters = "Action=" + action + "&SerialNumber=" + serialNumberChoosen + "&InsertInto=" + insertInto
            + "&Cubes=";

    var numOfCubes = cubes.length;

    for (var i = 0; i < numOfCubes; i++) {
        parameters = parameters + cubes[i].id;
        if (i !== numOfCubes - 1) {
            parameters = parameters + ",";
        }
    }

    return parameters;
}

function changeDisableOfActionButtons(disable) {
    var typeOfActionBtn = disable ? "disableBtn" : "btn";
    var typeOfActionWithBoard = disable || !isPlayerCanUseBoard ? "disableBtn" : "btn";
    var otherType = !disable ? "disableBtn" : "btn";
    var takeBackTileType = !isPlayerStillPlay || disable ? "disableBtn" : "btn";
    var finishActionType = disable && action !== "" ? "btn" : "disableBtn";
    $("#finishActionBtn").prop('class', finishActionType);
    $("#createSerialBtn").prop('class', typeOfActionBtn);
    $("#insertToSerialBtn").prop('class', typeOfActionWithBoard);
    $("#replaceJokerBtn").prop('class', typeOfActionWithBoard);
    $("#takeBackBtn").prop('class', takeBackTileType);
    $("#cancelBtn").prop('class', otherType);
    $("#finishTrnBtn").prop('class', typeOfActionBtn);
    setCubeButtonDisabled();
}

function onCubeClicked(e) {
    var cubeClicked = e.id;
    var idOfTheCube = "#" + cubeClicked;
    if ($("#tmpCurrentTurnView").has($(idOfTheCube)).length !== 0) {
        if (idOfTheCube.indexOf("cubeBoard") > -1) {
            $("#board").append($(idOfTheCube));
            setBoardDisable(true);
        } else {
            $("#playerHand").append($(idOfTheCube));
            setPlayerHandDisable(false);
        }
    } else {
        if (action === "insert") {
            setCubeButtonsWhileInsertAction(idOfTheCube);
        } else if (action === "replace") {
            setCubeButtonsWhileReplaceAction(idOfTheCube);
        } else if (action === "takeBack") {
            setCubeButtonsWhileTakeBackAction(idOfTheCube);
        } else {
            $("#tmpCurrentTurnView").append($(idOfTheCube));
        }
    }

    if ($("#tmpCurrentTurnView").children().length >= 0) {
        setTempCurrentTurnViewEnable();
    }
}

function setCubeButtonsWhileInsertAction(cube) {
    $("#tmpCurrentTurnView").append($(cube));
    setPlayerHandDisable(true);
    setBoardDisable(true);
}

function setCubeButtonsWhileTakeBackAction(cube) {
    $("#tmpCurrentTurnView").append($(cube));
    setBoardDisable(true);
}

function setCubeButtonsWhileReplaceAction(cube) {
    var id = cube + "";

    if (id.indexOf("cubeBoard") > -1) {
        setBoardDisable(true);
    } else {
        setPlayerHandDisable(true);
    }

    $("#tmpCurrentTurnView").append($(cube));
}

function setTempCurrentTurnViewEnable() {
    $("#tmpCurrentTurnView").children().prop('disabled', false);
}