"use client";

import { ChatRoomResponse, ConversationUserResponse } from "@/types/dto";
import { Session } from "next-auth";
import { Suspense, useEffect } from "react";
import LoadingSpinner from "@/components/common/loading-spinner";
import ChatMainContent from "@/app/(main)/chat/main-content";
import useFetchStream from "@/hoooks/useFetchStream";

interface ChatMainContentWrapperProps {
  authUser: NonNullable<Session["user"]>;
}

export default function ChatMainContentWrapper({
  authUser,
}: ChatMainContentWrapperProps) {
  const {
    messages: connectedUsers,
    error: uError,
    isFinished: uIsFinished,
  } = useFetchStream<ConversationUserResponse[]>({
    path: "/ws-http/getConnectedUsers",
    acceptHeader: "application/json",
    useAbortController: false,
    authToken: true,
  });

  const {
    messages: chatRooms,
    error: rError,
    isFinished: rIsFinished,
    refetch: refetchChatRooms,
  } = useFetchStream<ChatRoomResponse[]>({
    path: `/ws-http/chatRooms/${authUser.email}`,
    acceptHeader: "application/json",
    useAbortController: false,
    authToken: true,
  });

  if (uError || rError) {
    console.error("Error fetching messages:", uError || rError);
    return <div>Error loading messages.</div>;
  }

  if (!uIsFinished || !rIsFinished) return <LoadingSpinner />;

  // console.log("connectedUsers", connectedUsers);
  // console.log("chatRooms", chatRooms);
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <ChatMainContent
        initialConnectedUsers={connectedUsers[0]}
        initialChatRooms={chatRooms[0]}
        authUser={authUser}
      />
    </Suspense>
  );
}
