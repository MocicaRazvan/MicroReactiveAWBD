import Nav from "@/components/nav/nav";
import { ReactNode } from "react";
import { StompProvider } from "@/providers/stomp-provider";
import { getServerSession } from "next-auth";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";
import { ChatMessageNotificationProvider } from "@/context/chat-message-notification-context";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  const spring = process.env.NEXT_PUBLIC_SPRING_CLIENT!;
  const session = await getServerSession(authOptions);
  const isUser = !!(session && session.user && true);
  return (
    <div>
      <Nav />
      {children}
      {/*{!isUser ? (*/}
      {/*  children*/}
      {/*) : (*/}
      {/*  <StompProvider url={spring + "/ws/ws-service"} authUser={session.user!}>*/}
      {/*    <ChatMessageNotificationProvider authUser={session.user!}>*/}
      {/*      {children}*/}
      {/*    </ChatMessageNotificationProvider>*/}
      {/*  </StompProvider>*/}
      {/*)}*/}
    </div>
  );
}
