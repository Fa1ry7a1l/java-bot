package edu.java.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverflowDTO(List<Question> items) {
    public record Question(
        @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate
    ) {
    }
}
