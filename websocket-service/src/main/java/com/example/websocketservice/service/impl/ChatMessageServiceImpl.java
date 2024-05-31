package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;
import com.example.websocketservice.exceptions.MoreThenOneChatRoom;
import com.example.websocketservice.exceptions.NoChatRoom;
import com.example.websocketservice.mappers.ChatMessageMapper;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.ChatMessageRepository;
import com.example.websocketservice.service.ChatMessageService;
import com.example.websocketservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatMessageResponse sendMessage(ChatMessagePayload chatMessagePayload) {
        Set<String> emails = Set.of(chatMessagePayload.getSenderEmail(), chatMessagePayload.getReceiverEmail());
        List<ChatRoom> rooms = chatRoomService.getRoomsByUsers(
                Set.of(chatMessagePayload.getSenderEmail(), chatMessagePayload.getReceiverEmail())
        ).stream().filter(r -> r.getId().equals(chatMessagePayload.getChatRoomId())).toList();
        if (rooms.isEmpty()) {
            throw new NoChatRoom(emails);
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
                    ChatMessageResponse cmr = chatMessageMapper.fromModelToResponse(c);
                    messagingTemplate.convertAndSendToUser(chatMessagePayload.getReceiverEmail(), "/queue/messages", cmr);
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
