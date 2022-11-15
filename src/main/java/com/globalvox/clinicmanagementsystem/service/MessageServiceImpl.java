package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Conversation;
import com.globalvox.clinicmanagementsystem.entity.Message;
import com.globalvox.clinicmanagementsystem.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> findByConversation(Conversation conversation) {
        return messageRepository.findByConversationOrderByMessageId(conversation);
    }

    @Override
    public Message saveMessage(Message message) {
        return  messageRepository.save(message);
    }

    @Override
    public Message findById(long messageId) {
        return messageRepository.findByMessageId(messageId);
    }
}
