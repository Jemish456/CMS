package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Conversation;
import com.globalvox.clinicmanagementsystem.entity.Message;


import java.util.List;

public interface MessageService {

    List<Message> findByConversation(Conversation conversation);

    Message  saveMessage(Message message);

    Message findById(long messageId);

}
