package com.example.websocketservice.models;


import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.utils.Transformable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class ChatRoom extends IdGenerated implements Transformable<ChatRoom> {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_room_users",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<ConversationUser> users;


    @Override
    public String toString() {
        return "ChatRoom{" +
                "users=" + users.stream().map(ConversationUser::getEmail).toList() +
                ", id=" + super.getId() +
                '}';
    }
}
