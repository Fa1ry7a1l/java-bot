package edu.java.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubDTO(@JsonProperty("updated_at") OffsetDateTime lastActivityDate) {
}
