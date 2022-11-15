'use strict';
// Taking Elements
var messageForm = document.querySelector('#messageForm');
var textMessage = document.querySelector('#textMessage');
var userId = window.location.search.split("=")[1];
var stompClient = null;
var sender = document.getElementById('senderId').value;
var chatArea = document.getElementById('chatArea');
var messagePhoto =  document.getElementById('messagePhoto').files[0];
var photoSelected = document.getElementById('messagePhoto').value != "";
var convId =document.getElementById('conversationId').value;
var isSelected = '';
var image  =document.getElementById("messagePhoto");
var fileName = null;

window.onload= function connect(event) {
    if(!(userId == sender)) {

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    event.preventDefault();
}

/* On Reload Connecting Calling Function */
function onConnected() {

    // Subscribe to the Public Topic
    var subLink= '/topic/public/'+ document.getElementById('conversationId').value;

    stompClient.subscribe(subLink, onMessageReceived)

    // Tell your username to the server
    stompClient.send("/app/chat.addUser", {},
        JSON.stringify({senderId: userId ,conversationId:convId})
    )

}
/*On Message Receive Calling Function */
function onMessageReceived(payload) {

    var message = JSON.parse(payload.body);

    /* For Status (Online,Offline) */
    // if (message.status == "Offline"){
    //
    //     var i = document.getElementById('status');
    //     i.innerText = " offline";
    //     i.classList.add('fa');
    //     i.classList.add('fa-circle');
    //     i.classList.add('offline');
    //
    // }else if(message.status == "Online"){
    //     var i = document.getElementById('status');
    //     i.innerText=" online";
    //     i.classList.add('fa');
    //     i.classList.add('fa-circle');
    //     i.classList.remove('offline');
    //     i.classList.add('online');
    // }

    if (message.textMessage != null || message.messagePhoto != null){
        var ul = document.createElement('ul');
        var newMessage  = document.createElement('li');
        ul.appendChild(newMessage);
        newMessage.classList.add('clearfix');
        var messageAling = document.createElement('div');
        messageAling.classList.add('message');
        messageAling.classList.add('other-message');
        if(userId != message.senderId){
            messageAling.classList.add('float-right');
        }else {
            messageAling.classList.remove('other-message');
            messageAling.classList.add('my-message');
        }
        newMessage.appendChild(messageAling);
        var textmessage;
        if (message.messagePhoto != null){
            var image = document.createElement('img');
            image.src =`/display/chat-image/?id=${message.messageId}`;
            image.style.width = "205px";
            image.style.height = "205px";

            textmessage = document.createElement('div');
            // textmessage.classList.add('message');
            textmessage.appendChild(image)
            messageAling.appendChild(textmessage);
        }

        if (message.textMessage != ''){
            textmessage = document.createTextNode(message.textMessage);
            messageAling.appendChild(textmessage);
        }
        chatArea.append(ul);
        chatArea.append(document.createElement('br'));
        chatArea.scrollTop = chatArea.scrollHeight;
    }

}

// if there is connection problem call this function
function onError() {
    console.log("from Error");
}

//On selecting Image Upload Image AJAX
image.onchange = (e) =>{
    // e.preventDefault();
    let messageFile = e.target.files[0];
    isSelected = messageFile;

        var formData = new FormData();
        formData.set("conversationId", document.getElementById('conversationId').value);
        formData.append("photo",image.files[0]);


        $.ajax({
            url: '/api/file',
            type:'POST',
            data: formData,
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            error: function(jqXHR, textStatus, errorMessage) {
                console.log(errorMessage); // Optional
            },
            success: function (response) {
                fileName = response;

            }
        });


}

//Send Message on submit
function sendMessage(event) {
    event.preventDefault();
    // var messageContent = textMessage.value;
    var imageData = new FormData();
    imageData.append('image', $('#messagePhoto')[0].files[0]);
    if (stompClient) {
        var message = {
            messageId: null,
            messagePhoto: null,
            receiverId: userId,
            senderId: sender,
            textMessage: textMessage.value,
            conversationId: document.getElementById('conversationId').value,
            conversation: null
            // conversation:parseInt(document.getElementById('conversationId').value)
        };

        if (isSelected != '' && isSelected != undefined) {

            message.messagePhoto = document.getElementById('messagePhoto').files[0].name ;

        }


        if (message.textMessage != '' || (isSelected != '' && isSelected != undefined)) {
            // sendMessage(messagePhoto);
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
            textMessage.value = '';
            document.getElementById('messagePhoto').value = '';

        }

    }
}

//Event listener
messageForm.addEventListener('submit', sendMessage);
