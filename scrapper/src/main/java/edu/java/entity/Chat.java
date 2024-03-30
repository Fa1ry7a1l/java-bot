package edu.java.entity;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "telegram_chat")
public class Chat {
    @Id
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime registeredAt;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
        name = "telegram_chat_link",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<Link> links;

    public Chat(Long id, OffsetDateTime registeredAt) {
        this.id = id;
        this.registeredAt = registeredAt;
        this.links = new HashSet<>();
    }
}
