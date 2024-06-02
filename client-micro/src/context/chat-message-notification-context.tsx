"use client";

import { ReactNode, useCallback } from "react";
import { useSession } from "next-auth/react";
import {
  NotificationTemplateProvider,
  useNotificationTemplate,
} from "@/context/chat-message-notification-template-context";
import {
  ChatMessageNotificationResponse,
  ChatMessageNotificationType,
  ChatRoomResponse,
} from "@/types/dto";
import { useStompClient } from "react-stomp-hooks";
import { Session } from "next-auth";
import { Client } from "@stomp/stompjs";

interface ChatMessageNotificationProviderProps {
  children: ReactNode;
  authUser: NonNullable<Session["user"]>;
}

export function ChatMessageNotificationProvider({
  children,
  authUser,
}: ChatMessageNotificationProviderProps) {
  return (
    <NotificationTemplateProvider<
      ChatRoomResponse,
      ChatMessageNotificationType,
      ChatMessageNotificationResponse
    >
      notificationName={"chatMessageNotification"}
      authUser={authUser}
    >
      {children}
    </NotificationTemplateProvider>
  );
}

interface PayloadStomp {
  payload: ChatMessageNotificationResponse;
  stompClient: Client;
}

export const useChatNotification = () => {
  const {
    removeNotification,
    removeByType,
    removeBySender,
    clearNotifications,
    ...rest
  } = useNotificationTemplate<
    ChatRoomResponse,
    ChatMessageNotificationType,
    ChatMessageNotificationResponse
  >();

  const removeNotificationChat = useCallback(
    (p: PayloadStomp) =>
      removeNotification({
        notificationName: "chatMessageNotification",
        ...p,
      }),
    [removeNotification],
  );

  const removeByTypeChat = useCallback(
    (p: {
      type: ChatMessageNotificationType;
      stompClient: Client;
      receiverEmail: string;
    }) =>
      removeByType({
        notificationName: "chatMessageNotification",
        ...p,
      }),
    [removeByType],
  );

  const removeBySenderChat = useCallback(
    (p: { stompClient: Client; senderEmail: string; receiverEmail: string }) =>
      removeBySender({
        notificationName: "chatMessageNotification",
        ...p,
      }),
    [removeBySender],
  );

  const clearNotificationsChat = useCallback(
    (p: { stompClient: Client; receiverEmail: string }) =>
      clearNotifications({
        notificationName: "chatMessageNotification",
        ...p,
      }),
    [clearNotifications],
  );

  return {
    removeNotification: removeNotificationChat,
    removeByType: removeByTypeChat,
    removeBySender: removeBySenderChat,
    clearNotifications: clearNotificationsChat,
    ...rest,
  };
};
