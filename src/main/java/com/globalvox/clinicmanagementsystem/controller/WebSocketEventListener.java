package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.MessageDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long username = (Long) headerAccessor.getSessionAttributes().get("username");
        Long conversationId = (Long) headerAccessor.getSessionAttributes().get("conversationId");
        if(username != 0) {
            logger.info("User Disconnected : " + username);

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setStatus("Offline");
            messageDTO.setSenderId(username);
            messageDTO.setConversationId(conversationId);
            messagingTemplate.convertAndSend("/topic/public/"+conversationId,messageDTO);
        }
    }
}