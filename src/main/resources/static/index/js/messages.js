let stompClientPost
let currentLocation = document.location.protocol + "//" + document.location.host;
let page = 1
let messages = document.querySelector(".messages")

function getRandomInt() {
    return Math.floor(Math.random() * 6 + 1);
}

function addMessageBlock(message) {
    console.log(message)
    const div = document.createElement('tr');
    const help = document.createElement('div');
    help.innerHTML = message["content"].replace("<br>", " ")
    message["content"] = help.innerText

    let source =message["userFrom"]["image"] != null ? '/image/' +
        message["userFrom"]["image"]["id"] :'https://bootdey.com/img/Content/avatar/avatar' + getRandomInt() + '.png';
    // "<td class=\"inbox-small-cells\"><i class=\"fa fa-star inbox-started\"></i></td>"

    div.innerHTML += "<td class=\"inbox-small-cells\">\n" +
        "                                    <input type=\"checkbox\" class=\"mail-checkbox\">\n" +
        "                                </td>\n" +
        "                                <td class=\"inbox-small-cells\"><i class=\"fa fa-star\"></i></td>\n" +
        "                                <td class=\"view-message\"><img style=\"width: 25px;height: 25px\" src=\"" + source + "\" class=\"img-responsive\" alt=\"\"></td>\n" +
        "                                <td class=\"view-message dont-show\">" + message["userFrom"]["name"] + " " + message["userFrom"]["surname"] +"</td>\n" +
        "                                <td class=\"view-message\">" + message["content"].slice(0, 50).trim() +"</td>\n" +
        "                                <td class=\"view-message inbox-small-cells\"></td>\n" +
        "                                <td class=\"view-message text-right\">" + message["time"].split(" ")[1] + "</td>";


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

    $(div).on('click', function () {
        window.location = '/inbox/' + message["id"];
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




function findMessageType(label) {
    if (label === "Work")
        return "WORK"
    else if (label === "News")
        return "NEWS"
    else if (label === "Family")
        return "FAMILY"
    else if (label === "Friends")
        return "FRIENDS"
    else return null
}

$(function (){
    createAjaxQuery("/messages/0", successMessageHandler)
})


function createAjaxQuery(url, toFunction) {
    console.log(currentLocation + url);
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + url,
        contentType: 'application/json',
        success: toFunction
    });
}

// Получение информации и добавление заявок
var successMessageHandler = function (data) {
    for (let i = 0; i < data.length; i++) {
        addMessageBlock(data[i])
    }
};


document.addEventListener('DOMContentLoaded', message_connect, true)