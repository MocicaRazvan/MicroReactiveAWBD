package com.example.websocketservice.models.generic;

import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.utils.Transformable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class NotificationTemplate<R extends IdGenerated, E extends Enum<E>> extends IdGenerated implements Transformable<NotificationTemplate<R, E>> {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private ConversationUser sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private ConversationUser receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private E type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reference_id", nullable = false)
    private R reference;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String extraLink;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
