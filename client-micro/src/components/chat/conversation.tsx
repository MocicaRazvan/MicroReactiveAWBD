"use client";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  ChatMessageResponse,
  ConversationUserBase,
  ConversationUserResponse,
} from "@/types/dto";
import { memo, useEffect, useRef, useState } from "react";
import { isDeepEqual } from "@/lib/utils";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { compareAsc, format, parseISO } from "date-fns";
import { ScrollArea } from "@/components/ui/scroll-area";
import ChatMessageForm from "@/components/forms/chat-message-form";
import { useChatNotification } from "@/context/chat-message-notification-context";

interface ConversationProps {
  sender: ConversationUserResponse;
  receiver: ConversationUserResponse;
  initialMessages: ChatMessageResponse[];
  chatRoomId: number;
}

export default function Conversation({
  initialMessages,
  sender,
  receiver,
  chatRoomId,
}: ConversationProps) {
  // console.log("initialMessages", initialMessages);
  const [chatMessages, setChatMessages] =
    useState<ChatMessageResponse[]>(initialMessages);
  // console.log("chatm leng", chatMessages.length);
  const chatContainerRef = useRef<HTMLDivElement>(null);
  const areInTheSameChat =
    sender.connectedChatRoom?.id &&
    receiver.connectedChatRoom?.id &&
    sender.connectedChatRoom?.id === receiver.connectedChatRoom?.id;

  console.log("Same chat", areInTheSameChat);
  console.log("Same chat rec", receiver.connectedChatRoom?.id);
  console.log("Same chat send", sender.connectedChatRoom?.id);
  console.log("Same chat send email", sender.email);

  const updateMessages = (
    messages: ChatMessageResponse[],
    newMessage: ChatMessageResponse,
  ): ChatMessageResponse[] => {
    if (!newMessage.timestamp) {
      console.error("New message has invalid timestamp:", newMessage);
      return messages;
    }

    const validMessages = messages.filter((m) => m.timestamp);
    if (validMessages.length !== messages.length) {
      console.warn(
        "Some messages have invalid timestamps and were filtered out",
      );
    }

    return [...validMessages, newMessage].sort((a, b) => {
      const dateA = parseISO(a.timestamp);
      const dateB = parseISO(b.timestamp);
      if (!dateA || !dateB) {
        console.error("Invalid date encountered during sorting:", a, b);
      }
      return compareAsc(dateA, dateB);
    });
  };

  useEffect(() => {
    setChatMessages(initialMessages);
  }, [JSON.stringify(initialMessages)]);

  useSubscription(`/user/${sender.email}/queue/messages`, (message) => {
    const newMessage = JSON.parse(message.body);
    console.log("sender queue");
    setChatMessages((prev) => updateMessages(prev, newMessage));
  });
  useSubscription(`/user/${receiver.email}/queue/messages`, (message) => {
    const newMessage = JSON.parse(message.body) as ChatMessageResponse;
    console.log("receiver queue");
    console.log("rec email", receiver.email);
    console.log("sender email", sender.email);
    console.log("newmsg rec", newMessage.receiver.email);
    console.log("newmsg send", newMessage.sender.email);
    if (
      newMessage.receiver.email === receiver.email &&
      newMessage.sender.email === sender.email
    ) {
      setChatMessages((prev) => updateMessages(prev, newMessage));
    }
  });

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop =
        chatContainerRef.current.scrollHeight;
    }
  }, [JSON.stringify(chatMessages)]);

  return (
    <div className="w-full h-full p-2 ">
      <ScrollArea
        className="w-full h-[600px] relative"
        viewportRef={chatContainerRef}
      >
        {areInTheSameChat && (
          <div className=" absolute top-0 right-0 bg-opacity-60 z-[1] w-full border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <p className="text-center text-sm font-bold">
              User is currently in this chat
            </p>
          </div>
        )}
        <div className="flex flex-col h-full ">
          <div className="flex-1 grid w-full p-6 gap-6 flex-col ">
            <div className="grid gap-2">
              {chatMessages.length > 0 &&
                chatMessages.map((chatMessage) => (
                  <div key={chatMessage.id}>
                    <ChatMessageItem
                      chatMessage={chatMessage}
                      sender={sender}
                      receiver={receiver}
                    />
                  </div>
                ))}
            </div>
          </div>
        </div>
      </ScrollArea>
      <div className="flex-1 ">
        <ChatMessageForm
          chatRoomId={chatRoomId}
          sender={sender}
          receiver={receiver}
        />
      </div>
    </div>
  );
}

interface ChatMessageProps {
  chatMessage: ChatMessageResponse;
  sender: ConversationUserBase;
  receiver: ConversationUserBase;
}

const ChatMessageItem = memo(
  ({ chatMessage, sender, receiver }: ChatMessageProps) => {
    const isSender = chatMessage.sender?.email === sender.email;
    const formatDate = (d: string) => format(parseISO(d), "dd-MM-yy HH:mm:ss");

    return isSender ? (
      <div className="flex flex-row-reverse gap-2 items-end">
        <div className="rounded-lg bg-gray-100 dark:bg-gray-900 p-4 max-w-[75%] backdrop-blur">
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {formatDate(chatMessage.timestamp)}
          </p>
          {/*<p className="text-sm font-medium">You, {sender.email}</p>*/}
          <div
            className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
            dangerouslySetInnerHTML={{ __html: chatMessage.content ?? "" }}
          />
        </div>
        {/*<img*/}
        {/*  alt="Avatar"*/}
        {/*  className="w-10 h-10 rounded-full border"*/}
        {/*  height="40"*/}
        {/*  src="/placeholder.svg"*/}
        {/*  style={{*/}
        {/*    aspectRatio: "40/40",*/}
        {/*    objectFit: "cover",*/}
        {/*  }}*/}
        {/*  width="40"*/}
        {/*/>*/}
      </div>
    ) : (
      <div className="flex gap-2 items-end ">
        {/*<img*/}
        {/*  alt="Avatar"*/}
        {/*  className="w-10 h-10 rounded-full border"*/}
        {/*  height="40"*/}
        {/*  src="/placeholder.svg"*/}
        {/*  style={{*/}
        {/*    aspectRatio: "40/40",*/}
        {/*    objectFit: "cover",*/}
        {/*  }}*/}
        {/*  width="40"*/}
        {/*/>*/}
        <div className="rounded-lg bg-gray-600 dark:bg-white p-4 max-w-[75%] backdrop-blur dark:text-black text-white ">
          <p className="text-sm text-gray-100 dark:text-gray-950 ">
            {formatDate(chatMessage.timestamp)}
          </p>
          {/*<p className="text-sm font-medium">Other {receiver.email}</p>*/}
          <div
            className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap text-white dark:text-black"
            dangerouslySetInnerHTML={{ __html: chatMessage.content ?? "" }}
          />
        </div>
      </div>
    );
  },
  (prev, next) =>
    isDeepEqual(prev, next) &&
    isDeepEqual(prev.chatMessage, next.chatMessage) &&
    isDeepEqual(prev.sender, next.sender) &&
    isDeepEqual(prev.receiver, next.receiver),
);

ChatMessageItem.displayName = "ChatMessageItem";
