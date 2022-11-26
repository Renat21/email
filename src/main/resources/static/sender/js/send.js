var userEmail;
let stompClientPost
let currentLocation = document.location.protocol + "//" + document.location.host;




const message_connect = function () {
    userEmail = document.getElementById("userToEmail").value
    if (userEmail) {
        const socket = new SockJS('/message-mail')
        stompClientPost = Stomp.over(socket)
        stompClientPost.connect({}, onPostConnected, onPostError)
    }
}

document.querySelector("#sendMessage").addEventListener("click", message_connect, true)

const onPostConnected = function () {
    stompClientPost.subscribe('/topic/messageMail/' + userEmail)
    sendMessage()
}

const onPostError = (error) => {
    console.log(error)
}



async function createImage(images) {
    var request = {};
    let elem;
    request.images = images; // some data

    let formData = new FormData();
    for (let i = 0; i < images.length; i++) {
        formData.append("file", images[i]);
    }
    const response = await fetch(currentLocation + "/image/createImage", {
        method: "POST",
        body: formData,
    }).then((data) => {
        elem = data.json();
    })
    return elem
}

async function sendMessage() {

    var imageValue
    let images = document.querySelector("#imageList").files
    console.log(images)
    if (images.length !== 0) {
        let imageConverted = createImage(images)
        await imageConverted.then(async function (value) {
            imageValue = value
        })
    }

    let d = new Date();
    let ye = new Intl.DateTimeFormat('ru', {year: 'numeric'}).format(d);
    let mo = new Intl.DateTimeFormat('ru', {month: '2-digit'}).format(d);
    let da = new Intl.DateTimeFormat('ru', {day: '2-digit'}).format(d);
    let time = new Intl.DateTimeFormat('ru',
        {
            hour: "numeric",
            minute: "numeric",
        }).format(d)

    const email = document.getElementById("userToEmail").value;
    const current_content = document.getElementById("content").innerHTML;
    const currentLabel = document.getElementById("currentLabel").innerText.trim()
    const messageType = findMessageType(currentLabel)


    const message = {
        userFrom: currentUser,
        userTo: {email: userEmail},
        messageType: messageType,
        content: current_content,
        images: imageValue != null ? imageValue : null,
        time: `${da}/${mo}/${ye} ${time}`
    }
    console.log("/app/message.send/" + userEmail)
    stompClientPost.send("/app/message.send/" + userEmail, {}, JSON.stringify(message))


    document.querySelector("#imageList").value = ''
    document.getElementById("content").innerHTML = ''
    document.getElementById("currentLabel").innerText = ''
    document.getElementById("userToEmail").value = ''
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
var successHandler = function (data) {
    console.log(data)
};