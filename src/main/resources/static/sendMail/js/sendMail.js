let currentLocation = document.location.protocol + "//" + document.location.host;
let messages = document.querySelector(".messages")
let messageParameters = document.querySelector("#messageParameters")
let searchLine = document.querySelector("#searchLine")

let countOfMessagesSent = 0;
let messagesCountNow = 15
let page = 0

function getRandomInt() {
    return Math.floor(Math.random() * 6 + 1);
}

function addMessageBlock(message) {
    console.log(message)
    var div = document.createElement('tr');
    const help = document.createElement('div');
    help.innerHTML = message["content"].replace("<br>", " ")
    message["content"] = help.innerText


    let source =message["userTo"]["image"] != null ? '/image/' +
        message["userTo"]["image"]["id"] :'https://bootdey.com/img/Content/avatar/avatar' + getRandomInt() + '.png';
    // "<td class=\"inbox-small-cells\"><i class=\"fa fa-star inbox-started\"></i></td>"

    div.innerHTML += "<td class=\"inbox-small-cells\">\n" +
        "                                    <input id=\"messageId_"+message["id"]+"\" type=\"checkbox\" class=\"mail-checkbox\">\n" +
        "                                </td>\n" +
        "                                <td class=\"inbox-small-cells\"><i class=\"fa fa-star\"></i></td>\n" +
        "                                <td class=\"view-message\"><img style=\"width: 25px;height: 25px\" src=\"" + source + "\" class=\"img-responsive\" alt=\"\"></td>\n" +
        "                                <td class=\"view-message dont-show\">" + message["userFrom"]["name"] + " " + message["userFrom"]["surname"] +"</td>\n" +
        "                                <td class=\"view-message messageContent\"></td>\n" +
        "                                <td id=\"inbox-small-cells\" class=\"view-message inbox-small-cells\"></td>\n" +
        "                                <td class=\"view-message text-right\">" + message["time"].split(" ")[1] + "</td>";

    div.querySelector(".messageContent").innerText =  message["content"].slice(0, 25).trim()
    if (message["star"] === true)
        div.querySelector(".fa-star").classList.add("inbox-started")
    const span = document.createElement('span')
    if (message["messageType"]) {
        if (message["messageType"] === "FRIENDS")
        {
            span.classList.add("label", "label-success", "pull-right")
            span.innerText = "Friend"
        }
        else if (message["messageType"] === "WORK")
        {
            span.classList.add("label", "label-danger", "pull-right")
            span.innerText = "Work"
        }
        else if (message["messageType"] === "FAMILY")
        {
            span.classList.add("label", "label-info", "pull-right")
            span.innerText = "Family"
        }
        else if (message["messageType"] === "NEWS")
        {
            span.classList.add("label", "label-warning", "pull-right")
            span.innerText = "News"
        }
        div.querySelector(".dont-show").appendChild(span);
    }

    if (message["images"].length > 0)
        div.querySelector("#inbox-small-cells").innerHTML = "<i class=\"fa fa-paperclip\"></i>"

    $(div.querySelectorAll(".view-message")).on('click', function () {
        window.location = '/inbox/' + message["id"];
    })

    $(div.querySelector(".fa-star")).on('click', function () {
        createAjaxQuery("/setStarMessage/" + message["id"])
        let star = document.querySelector("#messageId_" + message["id"]).parentNode.parentNode.querySelector(".fa-star")
        if (star.classList.contains("inbox-started"))
            star.classList.remove("inbox-started")
        else
            star.classList.add("inbox-started")
    })

    messages.insertBefore(div, messages.firstChild);
}


document.querySelector("#deleteMessages").addEventListener('click', deleteMessages, true)

function deleteMessages() {
    createAjaxQueryWithData("/deleteSendMessages", deleteMessagesFromPage,  {"messages":getMarkedCheckBoxes()})
}

function deleteMessagesFromPage(data) {
    messagesId = JSON.parse(data)["messages"]
    for (let i = 0; i < messagesId.length; i++) {
        div = document.querySelector("#messageId_" + messagesId[i]).parentNode.parentNode
        div.parentNode.removeChild(div)
    }
}

function getMarkedCheckBoxes(){
    let messages = []
    let boxes = document.querySelectorAll(".mail-checkbox")
    for (let i = 0; i < boxes.length; i++) {
        console.log(boxes[i].checked)
        if (boxes[i].checked===true)
            messages.push(Number(boxes[i].id.split("_")[1]))
    }
    return messages
}

// document.querySelector("#previousPage").addEventListener("click", function (){
//     updateMessagesByPage(page - 1)
// }, true)
// document.querySelector("#nextPage").addEventListener("click", function () {
//     updateMessagesByPage(page + 1)
// }, true)


document.querySelector("#searchButton").addEventListener('click' , function (){
    updateFullPage(0)
}, true)


document.querySelector("#previousPage").addEventListener("click", function (){
    updateFullPage(page - 1)
}, true)
document.querySelector("#nextPage").addEventListener("click", function () {
    updateFullPage(page + 1)
}, true)



function updateMessagesByPage(newPage){
    console.log(newPage, messagesCountNow, countOfMessagesSent)
    if (newPage * messagesCountNow + 1 <= countOfMessagesSent && newPage >= 0) {
        createAjaxQuery("/messagesSend/" + newPage + "/" + (searchLine.value !== "" ?  searchLine.value: "default"), successMessageHandler)
        page = newPage
    }
}


$(function (){
    updateFullPage(0)
})

function updateFullPage(newPage){
    createAjaxQuery("/getMessagesSend/" +  (searchLine.value !== "" ?  searchLine.value: "default"),
        function (data){
            countOfMessagesSent = data
            updateMessagesByPage(newPage)
        })
}

// Запрос на получение друзей
function createAjaxQueryWithData(url, toFunction, request) {
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + url,
        contentType: 'application/json',
        data: JSON.stringify(request),
        success: toFunction
    });
}


function createAjaxQuery(url, toFunction) {
    console.log(currentLocation + url);
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + url,
        contentType: 'application/json',
        success: toFunction
    });
}

function setPageParameters() {
    messageParameters.innerHTML = (page * messagesCountNow + 1) + "-" + (
        (page + 1) * messagesCountNow > countOfMessagesSent
            ? countOfMessagesSent: (page + 1) * messagesCountNow) + " of " + countOfMessagesSent
}

// Получение информации и добавление заявок
var successMessageHandler = function (data) {
    setPageParameters()
    messages.innerHTML = ""
    for (let i = 0; i < data.length; i++) {
        addMessageBlock(data[i])
    }
};
