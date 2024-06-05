"use client";

import { ReactNode } from "react";

import { useSession } from "next-auth/react";
import { StompProvider } from "@/providers/stomp-provider";
import { ChatProvider } from "@/context/chat-context";
import { ChatMessageNotificationProvider } from "@/context/chat-message-notification-context";

export default function SessionWrapper({ children }: { children: ReactNode }) {
  const spring = process.env.NEXT_PUBLIC_SPRING_CLIENT!;
  const session = useSession();
  const isUser = !!(session && session.data?.user && true);
  return !isUser ? (
    children
  ) : (
    <StompProvider
      url={spring + "/ws/ws-service"}
      authUser={session.data?.user!}
    >
      <ChatProvider authUser={session.data?.user!}>
        <ChatMessageNotificationProvider authUser={session.data?.user!}>
          {children}
        </ChatMessageNotificationProvider>
      </ChatProvider>
    </StompProvider>
  );
}
