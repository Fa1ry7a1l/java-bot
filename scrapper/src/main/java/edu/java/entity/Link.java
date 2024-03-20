package edu.java.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private URI url;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToMany(mappedBy = "links",
                cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Chat> chats;

    public Link(Long id, URI url, String description, OffsetDateTime updatedAt) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.updatedAt = updatedAt;
        chats = new HashSet<>();
    }
}
