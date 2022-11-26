let $name = document.getElementById("friendLists");
var currentLocation = document.location.protocol + "//" + document.location.host;
var jsonDataOfThePage;
var profilesOfStrangers = [];
var friendProfiles = [];
var profilesReceived = [];
var isInviteSendStrangers = [];

let currentPage = "friends";
var page = 0;
var value = ""
var isLoading = false;

// Кнопки друзей и заявок
document.querySelector(".friends").addEventListener("click", friendsButtonClicked, true)
document.querySelector(".invites").addEventListener("click", invitesButtonClicked, true)

// Нажатие на кнопку друзей
function friendsButtonClicked(){
    $name.innerHTML = ""
    page = 0;
    addSearchButton();
    reloadFriendData(page);
    currentPage = "friends"

    const currentButton = document.querySelector(".invites")
    currentButton.classList.remove("border-info")
    const button = document.querySelector(".friends")
    if (!button.classList.contains("border-info"))
        button.classList.add("border-info")
}


// Нажатие на кнопку заявок
function invitesButtonClicked(){
    $name.innerHTML = ""
    page = 0;
    addSearchButton();
    reloadInvitesData(page);
    currentPage = "invites"

    const currentButton = document.querySelector(".friends")
    currentButton.classList.remove("border-info")
    const button = document.querySelector(".invites")
    if (!button.classList.contains("border-info"))
        button.classList.add("border-info")
}


// ПОИСК ПО ПОЛЬЗОВАТЕЛЯМ
function addSearchButton() {
    var searchBlock = document.getElementById("searchLine")
    var buttonBlock = document.getElementById("searchButton")

    searchBlock.value = value
    buttonBlock.onclick = toSearch
}

function toSearch() {
    value = document.getElementById("searchLine").value;
    $name.innerHTML = "";
    page = 0;
    addSearchButton();
    if (currentPage === "friends")
        reloadFriendData(page)
    else
        reloadInvitesData(page)
}


// КНОПКА УДАЛЕНИЯ ДРУГА
function deleteButton(name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("job-right", "my-4", "flex-shrink-0")
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "d-block", "w-100d-sm-inline-block", "btn-light");
    $friendButton.innerHTML += "Удалить";
    $friendButton.id = name;
    $friendButton.onclick = function () {
        deleteFriend(name)
    };

    buttonContainer.appendChild($friendButton)
    return buttonContainer;
}

function deleteFriend($name) {
    createAjaxQuery('/user/' + $name + '/unfriend', successDeleteFriendHandler($name))
}

function successDeleteFriendHandler($username) {
    let obj = document.querySelector("." + $username).parentNode;
    document.getElementById($username).parentNode.parentNode.appendChild(sendButton($username));
    document.getElementById($username).parentNode.parentNode.removeChild(document.getElementById($username).parentNode);
    $name.removeChild(obj);

    // Если еще нет надписи "Возможные друзья"
    if (document.getElementById("strangeStatusText") === null)
    {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Возможные друзья", "strangeStatusText"));
    }

    // Если больше нет друзей, то убираем надпись "Друзья"
    if ($name.querySelectorAll(".deleteFriendClass").length === 0 && document.getElementById("friendStatusText") !== null)
        $name.removeChild(document.getElementById("friendStatusText"))

    $name.appendChild(obj);
}

// КНОПКА ОТПРАВКИ ЗАПРОСА В ДРУЗЬЯ
function sendButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("job-right", "my-4", "flex-shrink-0");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "d-block", "w-100d-sm-inline-block", "btn-info");
    $friendButton.innerHTML += "Отправить";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        sendFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function sendFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend', successSendFriendHandler($name))
}

function successSendFriendHandler($name) {
    let obj = document.getElementById($name);
    let $mainObj = obj.parentNode;
    let parent = $mainObj.parentNode;
    parent.removeChild($mainObj);
    parent.appendChild(alreadySendButton($name));

}

// КНОПКА ЗАЯВКА ОТПРАВЛЕНА
function alreadySendButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("job-right", "my-4", "flex-shrink-0");
    const $friendButton = document.createElement('div');
    $friendButton.classList.add("btn", "d-block", "w-100d-sm-inline-block", "btn-dark");
    $friendButton.innerHTML += "Отправлена";
    $friendButton.id = $name;
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}



// КНОПКА ПРИНЯТИЯ ЗАЯВКИ
function addButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("job-right", "my-4", "flex-shrink-0");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "d-block", "w-100d-sm-inline-block", "btn-info");
    $friendButton.innerHTML += "Принять";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        addFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

// КНОПКА ОТКЛОНЕНИЯ ЗАЯВКИ
function denyButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("job-right", "my-4", "flex-shrink-0");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "d-block", "w-100d-sm-inline-block", "btn-light");
    $friendButton.innerHTML += "Отклонить";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        denyFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function addFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend/1', successAddAndDenyButtonHandler($name))
}

function denyFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend/2', successAddAndDenyButtonHandler($name))
}

function successAddAndDenyButtonHandler($name) {
    let obj = document.getElementById($name);
    let $mainObj = obj.parentNode;
    $mainObj.parentNode.parentNode.parentNode.removeChild(obj.parentNode.parentNode.parentNode);
}

function getRandomInt() {
    return Math.floor(Math.random() * 6  + 1);
}


// ФУНКЦИЯ ДОБАВЛЕНИЯ 1 БЛОКА USER В ЗАЯВКАХ
function addReceivedPerson(person) {
    let source = person["image"] != null ? '/image/' +
        person["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar' +getRandomInt() + '.png';
    const personContainer = document.createElement('div');
    personContainer.innerHTML += "<div class=\"" + person["username"] + " job-box d-md-flex align-items-center justify-content-between mb-30\">\n" +
        "                                                        <div class=\"job-left my-4 d-md-flex align-items-center flex-wrap\">\n" +
        "                                                            <div class=\"img-holder mr-md-4 mb-md-0 mb-4 mx-auto mx-md-0 d-md-none d-lg-flex\">\n" +
        "                                                                <img src=\"" + source + "\" class=\"img-responsive\" alt=\"\">\n" +
        "                                                            </div>\n" +
        "                                                            <div class=\"job-content\">\n" +
        "                                                                <h5 class=\"text-center text-md-left\">" + person["surname"] + " " + person["name"] + "</h5>\n" +
        "                                                                <ul class=\"d-md-flex flex-wrap text-capitalize ff-open-sans\">\n" +
        "                                                                    <li class=\"mr-md-4\">\n" +
        "                                                                        <i class=\"zmdi zmdi-pin mr-2\"></i>" + person["username"] + "\n" +
        "                                                                    </li>\n" +
        "                                                                    <li class=\"mr-md-4\">\n" +
        "                                                                        <i class=\"zmdi zmdi-time mr-2\"></i> Online\n" +
        "                                                                    </li>\n" +
        "                                                                </ul>\n" +
        "                                                            </div>\n" +
        "                                                        </div>\n" +
        "                                                        <div class=\"col-md-3\"> " +
        "                                                        </div>" +
        "                                                  </div>"



    personContainer.querySelector("." + person["username"]).querySelector(".col-md-3").appendChild(addButton(person["username"]));
    personContainer.querySelector("." + person["username"]).querySelector(".col-md-3").appendChild(denyButton(person["username"]));

    $name.appendChild(personContainer);

}


// ФУНКЦИЯ ДОБАВЛЕНИЯ 1 БЛОКА USER В ДРУЗЬЯХ
function addPerson(person, isStranger = false, isSend = true, fromRight = false) {
    let source;
    if (fromRight === false)
        source = person["image"] != null ? '/image/' +
            person["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar' +getRandomInt() + '.png';
    else
        source = person["images"][0];
    const personContainer = document.createElement('div');


    personContainer.innerHTML += "<div class=\"" + person["username"] + " job-box d-md-flex align-items-center justify-content-between mb-30\">\n" +
        "                                                        <div class=\"job-left my-4 d-md-flex align-items-center flex-wrap\">\n" +
        "                                                            <div class=\"img-holder mr-md-4 mb-md-0 mb-4 mx-auto mx-md-0 d-md-none d-lg-flex\">\n" +
        "                                                                <img src=\"" + source + "\" class=\"img-responsive\" alt=\"\">\n" +
        "                                                            </div>\n" +
        "                                                            <div class=\"job-content\">\n" +
        "                                                                <h5 class=\"text-center text-md-left\">" + person["surname"] + " " + person["name"] + "</h5>\n" +
        "                                                                <ul class=\"d-md-flex flex-wrap text-capitalize ff-open-sans\">\n" +
        "                                                                    <li class=\"mr-md-4\">\n" +
        "                                                                        <i class=\"zmdi zmdi-pin mr-2\"></i>" + person["username"] + "\n" +
        "                                                                    </li>\n" +
        "                                                                    <li class=\"mr-md-4\">\n" +
        "                                                                        <i class=\"zmdi zmdi-time mr-2\"></i> Online\n" +
        "                                                                    </li>\n" +
        "                                                                </ul>\n" +
        "                                                            </div>\n" +
        "                                                        </div>\n" +
        "                                                        <div class=\"col-md-3\"> " +
        "                                                        </div>" +
        "                                                  </div>"

    console.log(personContainer)
    if (isStranger === false) {
        personContainer.querySelector("." + person["username"]).querySelector(".col-md-3").appendChild(deleteButton(person["username"]));
        personContainer.classList.add("deleteFriendClass")
    } else if (isSend === false)
        personContainer.querySelector("." + person["username"]).querySelector(".col-md-3").appendChild(sendButton(person["username"]));
    else
        personContainer.querySelector("." + person["username"]).querySelector(".col-md-3").appendChild(alreadySendButton(person["username"]));
    $name.appendChild(personContainer);
}

// Создание запросов
function createAjaxQuery(url, toFunction) {
    console.log(currentLocation + url);
    jQuery.ajax({
        type: 'GET',
        url: currentLocation + url,
        contentType: 'application/json',
        success: toFunction
    });
}

// Получение информации и добавление заявок
var successInvitesHandler = function (data) {
    jsonDataOfThePage = data;
    profilesReceived = data;
    addAllInvites()
};

function addAllInvites() {
    if (document.getElementById("receivedText") === null) {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Заявки в друзья", "receivedText"));
    }

    if (profilesReceived != null)
        for (let i = 0; i < profilesReceived.length; i++)
            addReceivedPerson(profilesReceived[i]);
}


// Получение информации и добавление друзей
var successFriendHandler = function (data) {
    jsonDataOfThePage = data;
    console.log(data);
    friendProfiles = data["0"];
    profilesOfStrangers = data["1"];
    isInviteSendStrangers = data["2"];
    addAllPeople();
};

function addAllPeople() {

    if (document.getElementById("friendStatusText") === null && friendProfiles.length !== 0) {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Друзья", "friendStatusText"));
    }

    for (let i = 0; i < friendProfiles.length; i++)
        addPerson(friendProfiles[i]);


    if (profilesOfStrangers.length !== 0 && document.getElementById("strangeStatusText") === null){
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Возможные друзья", "strangeStatusText"));
    }
    for (let i = 0; i < profilesOfStrangers.length; i++)
        addPerson(profilesOfStrangers[i], true, isInviteSendStrangers[i]);

    isLoading = false;
}

// Надписи ("друзья", "возможные друзья", "заявки")
function createTextUnderPerson(text, id) {
    const friendContainer = document.createElement('span');
    friendContainer.style.display = "flex";
    friendContainer.id = id;
    friendContainer.style.justifyContent = "center";
    friendContainer.innerText = text;
    return friendContainer;
}

// Запрос на получение друзей
function reloadFriendData(page) {
    var request = {};
    request.searchLine = value;

    console.log(value);
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/friends/reloadFriendList/" + page,
        contentType: 'application/json',
        data: JSON.stringify(request),
        success: successFriendHandler
    });
}


// Запрос на получение заявок
function reloadInvitesData(page) {
    var request = {};
    request.searchLine = value;

    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/friends/reloadSuggestionList/" + page,
        contentType: 'application/json',
        data: JSON.stringify(request),
        success: successInvitesHandler
    });
}

// Генерация начальной страницы с друзьями
$(function () {
    addSearchButton();
    reloadFriendData(page);
})

// Добавление информации при скролле
$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height() + 100 && !isLoading) {
        if (currentPage === "friends" && friendProfiles.length + profilesOfStrangers.length !== 0) {
            page++;
            isLoading = true;
            reloadFriendData(page);
        }
        else if (currentPage === "invites" && profilesReceived.length !== 0){
            page++;
            isLoading = true;
            reloadInvitesData(page)
        }
    }
});