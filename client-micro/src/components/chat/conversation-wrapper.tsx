"use client";

import { useState } from "react";
import {
  ChatMessageResponse,
  ConversationUserBase,
  ConversationUserResponse,
} from "@/types/dto";
import useFetchStream from "@/hoooks/useFetchStream";
import Conversation from "@/components/chat/conversation";
import LoadingSpinner from "@/components/common/loading-spinner";

interface ConversationWrapperProps {
  chatRoomId: number;
  sender: ConversationUserResponse;
  receiver: ConversationUserResponse;
}

export default function ConversationWrapper({
  chatRoomId,
  sender,
  receiver,
}: ConversationWrapperProps) {
  const { messages, error, isFinished } = useFetchStream<ChatMessageResponse[]>(
    {
      path: "/ws-http/messages/" + chatRoomId,
      acceptHeader: "application/json",
    },
  );
  if (error) {
    console.error("Error fetching messages:", error);
    return <div>Error loading messages.</div>;
  }
  if (!isFinished) return <LoadingSpinner />;

  // console.log("messages", messages);

  return (
    <div className="min-h-[1000px]  w-full rounded-lg border-2 ">
      <Conversation
        chatRoomId={chatRoomId}
        initialMessages={messages[0]}
        sender={sender}
        receiver={receiver}
      />
    </div>
  );
}
