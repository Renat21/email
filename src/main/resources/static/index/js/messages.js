let stompClientPost
let currentLocation = document.location.protocol + "//" + document.location.host;
let messages = document.querySelector(".messages")
let messageParameters = document.querySelector("#messageParameters")
let searchLine = document.querySelector("#searchLine")

let countOfMessagesReceived = 0;
let messagesCountNow = 15
let page = 0


function getRandomInt() {
    return Math.floor(Math.random() * 6 + 1);
}

function setRead(div, message){
    if (message["userTo"]["id"] === currentUser["id"] && message["messageRead"] === false)
        div.classList.add("unread")
    return div
}

function addMessageBlock(message) {
    console.log(message)
    var div = document.createElement('tr');
    div = setRead(div, message)
    const help = document.createElement('div');
    help.innerHTML = message["content"].replace("<br>", " ")
    message["content"] = help.innerText


    let source =message["userFrom"]["image"] != null ? '/image/' +
        message["userFrom"]["image"]["id"] :'https://bootdey.com/img/Content/avatar/avatar' + getRandomInt() + '.png';
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


    div.querySelector(".messageContent").innerText = message["content"].slice(0, 25).trim()
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
        createAjaxQuery("/readMessage/" + message["id"])
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


const message_connect = function () {
    if (currentUser) {
        const socket = new SockJS('/message-mail')
        stompClientPost = Stomp.over(socket)
        stompClientPost.connect({}, onPostConnected, onPostError)
    }
}

const onPostConnected = function () {
    stompClientPost.subscribe('/topic/messageMail/' + currentUser["email"], OnMessageReceived)
}

const OnMessageReceived = function (data) {
    addMessageBlock(JSON.parse(data["body"]))
}

const onPostError = (error) => {
    console.log(error)
}

document.querySelector("#deleteMessages").addEventListener('click', deleteMessages, true)

function deleteMessages() {
    createAjaxQueryWithData("/deleteReceivedMessages", deleteMessagesFromPage,  {"messages":getMarkedCheckBoxes()})
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

    if (newPage * messagesCountNow + 1 <= countOfMessagesReceived && newPage >= 0) {
        createAjaxQuery("/messages/" + newPage + "/" + (searchLine.value !== "" ?  searchLine.value: "default"), successMessageHandler)
        page = newPage
    }
}


$(function (){
    updateFullPage(0)
})

function updateFullPage(newPage){
    createAjaxQuery("/getMessagesReceived/" +  (searchLine.value !== "" ?  searchLine.value: "default"),
        function (data){
            countOfMessagesReceived = data
            updateMessagesByPage(newPage)
        })
}

// ???????????? ???? ?????????????????? ????????????
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

// ?????????????????? ???????????????????? ?? ???????????????????? ????????????
var successMessageHandler = function (data) {
    setPageParameters()
    messages.innerHTML = ""
    for (let i = 0; i < data.length; i++) {
        addMessageBlock(data[i])
    }
};

function setPageParameters() {
    messageParameters.innerHTML = (page * messagesCountNow + 1) + "-" + (
        (page + 1) * messagesCountNow > countOfMessagesReceived
            ? countOfMessagesReceived: (page + 1) * messagesCountNow) + " of " + countOfMessagesReceived
}


document.addEventListener('DOMContentLoaded', message_connect, true)