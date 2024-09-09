package edu.java.services;

import edu.java.dtos.LinkUpdateRequest;

public interface LinkUpdateSenderService {

    void send(LinkUpdateRequest linkUpdateRequest);
}
