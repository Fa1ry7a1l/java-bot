package edu.java.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Link {
    private Long id;
    private URI url;
    private String description;
    private OffsetDateTime updatedAt;
}
