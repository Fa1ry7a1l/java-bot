package edu.java.controllers;

import edu.java.services.ChatService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@AllArgsConstructor
public class TgChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TgChatController.class);

    private final ChatService chatService;

    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {
        chatService.register(id);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        chatService.delete(id);

    }
}
