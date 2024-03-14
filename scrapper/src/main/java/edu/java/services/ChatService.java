package edu.java.services;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
    void register(Long id);

    void remove(Long id);
}
