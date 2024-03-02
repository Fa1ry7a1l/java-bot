package edu.java.bot.controllers;

import edu.java.bot.services.NotificationService;
import edu.java.dtos.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdateController {
    private final NotificationService service;

    @PostMapping
    public void postUpdates(@RequestBody LinkUpdateRequest request) {
        service.sendUpdateNotification(request);
    }
}
