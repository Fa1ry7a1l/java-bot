package edu.java.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ScrapperApiException extends ResponseStatusException {

    private final String description;

    public ScrapperApiException(HttpStatusCode status, String description, String reason) {
        super(status, reason);
        this.description = description;
    }
}
