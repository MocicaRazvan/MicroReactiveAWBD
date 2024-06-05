"use client";

import { ReactNode, useEffect, useState } from "react";
import { useSession } from "next-auth/react";
import { StompProvider } from "@/providers/stomp-provider";
import { ChatProvider } from "@/context/chat-context";
import { ChatMessageNotificationProvider } from "@/context/chat-message-notification-context";
import LoadingSpinner from "@/components/common/loading-spinner";
import { Session } from "next-auth";

export default function SessionWrapper({ children }: { children: ReactNode }) {
  const spring = process.env.NEXT_PUBLIC_SPRING_CLIENT!;
  const { data: session, status } = useSession();
  const [authUser, setAuthUser] = useState<Session["user"]>(undefined);

  useEffect(() => {
    if (status === "authenticated" && session?.user) {
      setAuthUser(session.user);
    } else {
      setAuthUser(null);
    }
  }, [status, session]);

  if (status === "loading") return <LoadingSpinner />;

  // return !authUser ? (
  //   <>{children}</>
  // ) : (
  return (
    <StompProvider url={spring + "/ws/ws-service"} authUser={authUser}>
      <ChatProvider authUser={authUser}>
        <ChatMessageNotificationProvider authUser={authUser}>
          {children}
        </ChatMessageNotificationProvider>
      </ChatProvider>
    </StompProvider>
  );
  // );
}
