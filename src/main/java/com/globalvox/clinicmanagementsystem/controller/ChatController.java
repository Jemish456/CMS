package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.MessageDTO;
import com.globalvox.clinicmanagementsystem.entity.Conversation;
import com.globalvox.clinicmanagementsystem.entity.Message;
import com.globalvox.clinicmanagementsystem.service.ConversationService;
import com.globalvox.clinicmanagementsystem.service.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        Optional<Conversation> conversation = conversationService.findById(messageDTO.getConversationId());
        /*Checking for previous Conversation */
        if (conversation.isPresent()){
            messageDTO.setConversation(conversation.get());
        }
        Message message =modelMapper.map(messageDTO,Message.class);
        Message message1 =messageService.saveMessage(message);
        System.out.println(message1.getMessageId());
        messageDTO.setConversation(null);
        messageDTO.setMessageId(message1.getMessageId());
        /*sending Message to Topic*/
        messagingTemplate.convertAndSend("/topic/public/"+messageDTO.getConversationId(),messageDTO);
    }

    @MessageMapping("/chat.addUser")
    public void addUser( @Payload MessageDTO messageDTO,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        messageDTO.setStatus("Online");
        headerAccessor.getSessionAttributes().put("username", messageDTO.getSenderId());
        headerAccessor.getSessionAttributes().put("conversationId", messageDTO.getConversationId());
        messagingTemplate.convertAndSend("/topic/public/"+messageDTO.getConversationId(),messageDTO);
    }

}
