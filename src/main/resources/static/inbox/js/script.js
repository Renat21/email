let currentLocation = document.location.protocol + "//" + document.location.host;
attachements = document.querySelector("#attachmentsArray")
function addAttachments(images){
    for(let i = 0; i < images.length; i++) {
        div = document.createElement('li')
        div.style.marginRight = "20px"

        let source =images[i] != null ? '/image/' + images[i]["id"] :'https://bootdey.com/img/Content/avatar/avatar' + getRandomInt() + '.png';
        fileName = images[i]["originalFileName"].length <= 13? images[i]["originalFileName"]: images[i]["originalFileName"].slice(0, 13) + ".."

        extension = images[i]["originalFileName"].split(".")[1]
        if (extension === "jpg" ||  extension === "png")
            extension = "<a href=\"#\" class=\"atch-thumb\"><img style='height: 150px; width: 150px' src=\"" + source + "\"></a>"
        else
            extension = ""

        div.innerHTML = extension +
            "                                <div style='font-size: 12px' class=\"file-name\">" + fileName + "</div>\n" +
            "                                <span style='font-size: 12px'>" + (images[i]["size"] / 1024 / 1024) .toFixed(2) + "MB</span>\n" +
            "                                <div class=\"links\">\n" +
            "                                    <a href=\"" + source + "\">View</a> -\n" +
            "                                    <a href=\"" + source + "\" download=\"" + images[i]["originalFileName"] + "\">Download</a>\n" +
            "                                </div>"
        attachements.appendChild(div)

    }
}


$(function (){
    console.log(message)
    content = document.querySelector(".view-mail")
    login = document.querySelector("#messageFromName")
    email = document.querySelector("#messageFromEmail")
    attachCount = document.querySelector("#countAttachments")
    messageDate = document.querySelector("#messageDate")


    messageDate.innerText = message["time"]
    attachCount.innerText += message["images"].length + " attachments — "
    login.innerText = message["userFrom"]["name"] + " " + message["userFrom"]["surname"]
    email.innerText = "[" + message["userFrom"]["email"] + "]"
    content.innerHTML = message["content"]


    addAttachments(message["images"])
})
