package org.example.lab8.listener;

import org.example.lab8.model.ChatMessage;
import org.example.lab8.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    @Autowired
    private UserSessionService sessionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        Principal principal = event.getUser();
        if (principal == null) return;

        String username = principal.getName();   // ✔ CORRECT

        sessionService.removeUser(username);

        ChatMessage msg = new ChatMessage(
                ChatMessage.MessageType.LEAVE,
                username,
                String.join(",", sessionService.getOnlineUsers())
        );

        messagingTemplate.convertAndSend("/topic/users", msg);
    }
}