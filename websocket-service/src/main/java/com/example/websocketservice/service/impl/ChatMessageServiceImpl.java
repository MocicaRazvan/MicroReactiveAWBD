package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.exceptions.MoreThenOneChatRoom;
import com.example.websocketservice.exceptions.notFound.NoChatRoomFound;
import com.example.websocketservice.mappers.ChatMessageMapper;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.ChatMessageRepository;
import com.example.websocketservice.service.ChatMessageService;
import com.example.websocketservice.service.ChatRoomService;
import com.example.websocketservice.service.ConversationUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationUserService conversationUserService;

    @Override
    public ChatMessageResponse sendMessage(ChatMessagePayload chatMessagePayload) {
        Set<String> emails = Set.of(chatMessagePayload.getSenderEmail(), chatMessagePayload.getReceiverEmail());
        List<ChatRoom> rooms = chatRoomService.getRoomsByUsers(
                Set.of(chatMessagePayload.getSenderEmail(), chatMessagePayload.getReceiverEmail())
        ).stream().filter(r -> r.getId().equals(chatMessagePayload.getChatRoomId())).toList();
        if (rooms.isEmpty()) {
            throw new NoChatRoomFound(emails);
        }
        if (rooms.size() > 1) {
            throw new MoreThenOneChatRoom(emails);
        }
        return chatMessageMapper.fromPayloadToModel(chatMessagePayload)
                .map(cm -> {
                    cm.setChatRoom(rooms.getFirst());
                    return cm;
                }).map(chatMessageRepository::save)
                .map(c -> {
                    log.error("Chat msg payload: {}", chatMessagePayload);
                    ChatMessageResponse cmr = chatMessageMapper.fromModelToResponse(c);

                    // the front subscribes to both queues
                    // to make sure that the messages are sent for the sender also and not to make a fake ws on the front
                    // we make like this
//                    if (c.getReceiver().getConnectedChatRoom() == null ||
//                            !Objects.equals(c.getReceiver().getConnectedChatRoom().getId(), c.getChatRoom().getId())) {
//                        messagingTemplate.convertAndSendToUser(chatMessagePayload.getSenderEmail(), "/queue/messages", cmr);
//                    } else {
//                        messagingTemplate.convertAndSendToUser(chatMessagePayload.getReceiverEmail(), "/queue/messages", cmr);
//                    }

                    // todo make the check bette
                    // extra safe check just to be sure
                    // sometimes the front in dev mode navigates before the stomp publish  message is processed
                    log.error("Sender is {} , receiver chat is {}",
                            c.getSender().getConnectedChatRoom() != null ? c.getSender().getConnectedChatRoom().getId() : null,
                            c.getReceiver().getConnectedChatRoom() != null ? c.getReceiver().getConnectedChatRoom().getId() : null);
                    if (c.getSender().getConnectedChatRoom() == null && c.getChatRoom() != null) {
                        c.getSender().setConnectedChatRoom(c.getChatRoom());
                        c.getSender().setConnectedStatus(ConnectedStatus.ONLINE);
                        conversationUserService.saveUser(c.getSender());
                    }

                    if (c.getReceiver().getConnectedChatRoom() != null &&
                            c.getReceiver().getConnectedChatRoom().getId().equals(
                                    c.getSender().getConnectedChatRoom().getId())) {
                        log.error("Sending to receiver " + c.getReceiver().getEmail());
                        messagingTemplate.convertAndSendToUser(chatMessagePayload.getReceiverEmail(), "/queue/messages", cmr);
                    } else {
                        log.error("Sending to sender" + c.getSender().getEmail());
                        messagingTemplate.convertAndSendToUser(chatMessagePayload.getSenderEmail(), "/queue/messages", cmr);
                    }

                    return cmr;
                });

    }

    @Override
    public List<ChatMessageResponse> getMessages(Long chatRoomId) {
        return chatMessageRepository.findAllByChatRoomId(chatRoomId)
                .stream().map(chatMessageMapper::fromModelToResponse)
                .toList();
    }
}
