package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.Conversation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MessageDTO {

    private long messageId;

    private  String messagePhoto;

    private String textMessage;

    private long senderId;

    private long receiverId;

    private long conversationId;

    private Conversation conversation;

    private MultipartFile photo;

    private String Status;
}
