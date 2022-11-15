package com.globalvox.clinicmanagementsystem.entity;

import com.sun.istack.Nullable;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id",referencedColumnName = "conversation_id")
    private Conversation conversation;

    private String textMessage;

    @Column(nullable = true)
    private String messagePhoto;

    @Nullable
    private long senderId;

    @Nullable
    private long receiverId;
}
