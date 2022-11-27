let currentLocation = document.location.protocol + "//" + document.location.host;
attachementsImages = document.querySelector("#attachmentsImages")
attachementsDocs = document.querySelector("#attachmentsDocuments")
function addAttachments(images){
    let div;
    let fileName;
    let extension;
    for (let i = 0; i < images.length; i++) {
        let source = images[i] != null ? '/image/' + images[i]["id"] : 'https://bootdey.com/img/Content/avatar/avatar' + getRandomInt() + '.png';
        extension = images[i]["originalFileName"].split(".")[1]
        fileName = images[i]["originalFileName"].length <= 13 ? images[i]["originalFileName"] : images[i]["originalFileName"].slice(0, 13) + ".."
        if (extension === "jpg" || extension === "png") {
            div = document.createElement('li')
            div.style.marginRight = "20px"

            div.innerHTML = "<a href=\"" + source + "\" class=\"atch-thumb\"><img style='height: 150px; width: 150px' src=\"" + source + "\"></a>" +
                "                                <div style='font-size: 12px' class=\"file-name\">" + fileName + "</div>\n" +
                "                                <span style='font-size: 12px'>" + (images[i]["size"] / 1024 / 1024).toFixed(2) + "MB</span>\n" +
                "                                <div class=\"links\">\n" +
                "                                    <a href=\"" + source + "\">View</a> -\n" +
                "                                    <a href=\"" + source + "\" download=\"" + images[i]["originalFileName"] + "\">Download</a>\n" +
                "                                </div>"
            attachementsImages.appendChild(div)
        } else {
            div = document.createElement('div')
            div.classList.add("attachment")

            div.innerHTML = "<span class=\"badge\">" + extension + "</span> <a href=\"" + source + "\" download=\"" + images[i]["originalFileName"] + "\">" + fileName + "</a> <i>("+ (images[i]["size"] / 1024 / 1024).toFixed(2) +"MB)</i>"
            let attachFile = div.querySelector(".badge")

            if (extension === "pdf" || extension === "docx" || extension === "txt")
                attachFile.style.backgroundColor = "#17a2b8"
            else if (extension === "xls")
                attachFile.style.backgroundColor = "#28a745"
            else
                attachFile.style.backgroundColor = "#dc3545"
            attachementsDocs.appendChild(div)
        }

    }
}


$(function (){
    console.log(message)
    let content = document.querySelector(".view-mail")
    let login = document.querySelector("#messageFromName")
    let email = document.querySelector("#messageFromEmail")
    let attachCount = document.querySelector("#countAttachments")
    let messageDate = document.querySelector("#messageDate")



    messageDate.innerText = message["time"]
    attachCount.innerText += message["images"].length + " attachments — "
    login.innerText = message["userFrom"]["name"] + " " + message["userFrom"]["surname"]
    email.innerText = "[" + message["userFrom"]["email"] + "]"
    content.innerHTML = message["content"]

    addAttachments(message["images"])
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
