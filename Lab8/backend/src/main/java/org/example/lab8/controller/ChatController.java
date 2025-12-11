package org.example.lab8.controller;

import org.example.lab8.model.ChatMessage;
import org.example.lab8.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private UserSessionService sessionService;

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }

    @MessageMapping("/chat.username")
    @SendTo("/topic/users")
    public ChatMessage registerUser(ChatMessage message) {
        sessionService.addUser(message.getSender());
        return new ChatMessage(ChatMessage.MessageType.JOIN,
                message.getSender(),
                String.join(",", sessionService.getOnlineUsers()));
    }
}