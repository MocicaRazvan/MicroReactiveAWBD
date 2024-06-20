package com.example.websocketservice.models;


import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.utils.Transformable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class ConversationUser extends IdGenerated implements Transformable<ConversationUser> {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectedStatus connectedStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "connected_chat_room_id")
    private ChatRoom connectedChatRoom;

    @Version
    private Long version;

}
