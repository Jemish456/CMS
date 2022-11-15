package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Conversation;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;

import java.util.Optional;

public interface ConversationService {

    Optional<Conversation>  findConversation(Doctor doctor, Patient patient);

    void saveConversation(Conversation conversation);

    Optional<Conversation> findById(Long id);
}
