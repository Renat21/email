currentLabel = document.querySelector("#currentLabel")

document.querySelector("#noLabel").addEventListener("click", switchLabel, true)
document.querySelector("#work").addEventListener("click", switchLabel, true)
document.querySelector("#family").addEventListener("click", switchLabel, true)
document.querySelector("#friends").addEventListener("click",switchLabel, true)
document.querySelector("#news").addEventListener("click", switchLabel, true)

document.querySelector("#bold").addEventListener("click",
    function (){document.execCommand( 'bold',false,null)}, true)
document.querySelector("#italic").addEventListener("click",
    function (){document.execCommand( 'italic',false,null)}, true)
document.querySelector("#underline").addEventListener("click",
    function (){document.execCommand( 'underline',false,null)}, true)


document.querySelector("#justifyLeft").addEventListener("click",
    function (){document.execCommand( 'justifyLeft',false,null)}, true)
document.querySelector("#justifyRight").addEventListener("click",
    function (){document.execCommand( 'justifyRight',false,null)}, true)
document.querySelector("#justifyCenter").addEventListener("click",
    function (){document.execCommand( 'justifyCenter',false,null)}, true)
document.querySelector("#justifyFull").addEventListener("click",
    function (){document.execCommand( 'justifyFull',false,null)}, true)


document.querySelector("#indent").addEventListener("click",
    function (){document.execCommand( 'indent',false,null)}, true)
document.querySelector("#outdent").addEventListener("click",
    function (){document.execCommand( 'outdent',false,null)}, true)


document.querySelector("#orderedlist").addEventListener("click",
    function (){document.execCommand( 'insertOrderedList',false,null)}, true)
document.querySelector("#unorderedlist").addEventListener("click",
    function (){document.execCommand( 'insertUnorderedList',false,null)}, true)


function switchLabel(event) {
    label = event.currentTarget
    newLabel = label.querySelector(".label").cloneNode( true );
    newLabel.style.marginLeft = "10px"
    newLabel.id = "cloneLabel"
    currentLabel.innerHTML = ""
    currentLabel.appendChild(newLabel)
}




