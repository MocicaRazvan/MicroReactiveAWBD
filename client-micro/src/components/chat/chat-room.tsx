"use client";

import { ChatRoomResponse, ConversationUserResponse } from "@/types/dto";
import { Dispatch, memo, SetStateAction, useCallback, useEffect } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { Session } from "next-auth";
import { cn, isDeepEqual, parseQueryParamAsInt } from "@/lib/utils";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useStompClient } from "react-stomp-hooks";
import { useChatNotification } from "@/context/chat-message-notification-context";

interface BaseProps {
  activeRoom: ChatRoomResponse | null;
  authUser: NonNullable<Session["user"]>;
}

interface ChatRoomProps extends BaseProps {
  chatRooms: ChatRoomResponse[];
  setActiveRoomId: Dispatch<SetStateAction<number | null>>;
  // setActiveRoomId: (id: number) => void;
}

export const ChatRoom = memo(
  ({ chatRooms, setActiveRoomId, authUser, activeRoom }: ChatRoomProps) => {
    const pathName = usePathname();
    // const router = useRouter();
    const stompClient = useStompClient();
    const { removeBySender } = useChatNotification();
    const searchParams = useSearchParams();
    // const chatId = searchParams.get("chatId");

    const clearNotifications = useCallback(
      (otherUser: ConversationUserResponse) => {
        if (stompClient && stompClient.connected) {
          stompClient.publish({
            destination:
              "/app/chatMessageNotification/deleteAllByReceiverEmailSenderEmail",
            body: JSON.stringify({
              receiverEmail: authUser.email,
              senderEmail: otherUser.email,
            }),
          });
          removeBySender({
            stompClient,
            senderEmail: otherUser.email,
            receiverEmail: authUser.email,
          });
        }
      },
      [authUser.email, removeBySender, stompClient],
    );

    const callback = useCallback(
      (room: ChatRoomResponse, otherUser: ConversationUserResponse) => {
        const params = new URLSearchParams(searchParams.toString());
        params.set("chatId", room.id.toString());
        window.history.pushState(null, "", `?${params.toString()}`);

        // if (stompClient && stompClient.connected) {
        //  setActiveRoomId(room.id);
        // router.push(`${pathName}?chatId=${room.id}`);
        // stompClient.publish({
        //   destination: "/app/changeRoom",
        //   body: JSON.stringify({
        //     chatId: room.id,
        //      userEmail: authUser.email,
        //    }),
        //  });
        //   stompClient.publish({
        //     destination:
        //       "/app/chatMessageNotification/deleteAllByReceiverEmailSenderEmail",
        //     body: JSON.stringify({
        //       receiverEmail: authUser.email,
        //       senderEmail: otherUser.email,
        //     }),
        //   });
        //   removeBySender({
        //     stompClient,
        //     senderEmail: otherUser.email,
        //     receiverEmail: authUser.email,
        //   });
        // clearNotifications(otherUser);
        //}
      },
      [
        // stompClient,
        // setActiveRoomId,
        // // router,
        // pathName,
        // authUser.email,
        // clearNotifications,
        searchParams,
      ],
    );

    // useEffect(() => {
    //   if (parseQueryParamAsInt(chatId, null)) {
    //     const room = chatRooms.find(
    //       (room) => room.id === parseQueryParamAsInt(chatId, null),
    //     );
    //     if (room) {
    //       const otherUser = room.users.find(
    //         ({ email }) => email !== authUser.email,
    //       );
    //       if (otherUser) {
    //         clearNotifications(otherUser);
    //       }
    //     }
    //   }
    // }, [
    //   authUser.email,
    //   callback,
    //   chatId,
    //   JSON.stringify(chatRooms),
    //   clearNotifications,
    // ]);

    // console.log("active room", activeRoom);
    console.log("chat rooms", chatRooms);

    const fakeRooms = Array.from(
      { length: 20 },
      (_, i) =>
        chatRooms.map((room) => ({ ...room, id: room.id + i })).flat()[0],
    );

    // console.log("chat rooms", chatRooms);
    return (
      <ScrollArea className="w-full h-[calc(1000px-1rem-100px)]  space-y-4">
        <div className="w-full h-full space-y-4 pr-4 pb-6 ">
          {chatRooms.map((room) => (
            <div key={room.id} className="w-full h-full ">
              <ChatRoomItem
                authUser={authUser}
                callback={callback}
                activeRoom={activeRoom}
                room={room}
              />
            </div>
          ))}{" "}
        </div>
      </ScrollArea>
    );
  },
  (prevProps, nextProps) =>
    JSON.stringify(prevProps.chatRooms) ===
      JSON.stringify(nextProps.chatRooms) &&
    isDeepEqual(prevProps.authUser, nextProps.authUser) &&
    isDeepEqual(prevProps.activeRoom, nextProps.activeRoom),
);

ChatRoom.displayName = "ChatRoom";

interface ChatRoomItemProps extends BaseProps {
  callback: (
    room: ChatRoomResponse,
    otherUser: ConversationUserResponse,
  ) => void;
  room: ChatRoomResponse;
}

const ChatRoomItem = memo(
  ({ authUser, callback, activeRoom, room }: ChatRoomItemProps) => {
    const otherUser = room.users.find(({ email }) => email !== authUser.email);
    const isActive = activeRoom?.id === room.id;
    const { getByReference } = useChatNotification();
    const notifications = getByReference(room.id);

    if (!otherUser) return null;

    return (
      <div
        className={cn(
          "cursor-pointer p-3 rounded-md space-x-4 transition-all hover:bg-accent hover:text-accent-foreground hover:scale-[1.01]",
          isActive && "bg-accent text-accent-foreground hover:scale-100",
        )}
        onClick={() => {
          callback(room, otherUser);
        }}
      >
        <div className="flex flex-col items-between justify-center gap-2">
          <div className="flex items-center justify-between gap-2">
            <p className=" font-bold">{otherUser.email}</p>
            <div
              className={cn(
                "w-5 h-5 rounded-full",
                otherUser.connectedStatus === "ONLINE"
                  ? "bg-success"
                  : "bg-destructive",
              )}
            />
          </div>
          <div>
            <p
              className={cn(
                "font-bold hidden",
                notifications.length > 0 && "inline",
              )}
            >
              {notifications.length > 0 && notifications.length} unread messages
            </p>
          </div>
        </div>
      </div>
    );
  },
  (prevProps, nextProps) => isDeepEqual(prevProps, nextProps),
);

ChatRoomItem.displayName = "ChatRoomItem";
