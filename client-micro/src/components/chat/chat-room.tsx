"use client";

import { ChatRoomResponse, ConversationUserResponse } from "@/types/dto";
import { Dispatch, memo, SetStateAction, useCallback } from "react";
import { usePathname, useRouter } from "next/navigation";
import { Session } from "next-auth";
import { cn, isDeepEqual } from "@/lib/utils";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useStompClient } from "react-stomp-hooks";

interface BaseProps {
  activeRoom: ChatRoomResponse | null;
  authUser: NonNullable<Session["user"]>;
}

interface ChatRoomProps extends BaseProps {
  chatRooms: ChatRoomResponse[];
  setActiveRoomId: Dispatch<SetStateAction<number | null>>;
}

export default function ChatRoom({
  chatRooms,
  setActiveRoomId,
  authUser,
  activeRoom,
}: ChatRoomProps) {
  const pathName = usePathname();
  const router = useRouter();
  const stompClient = useStompClient();

  const callback = useCallback(
    (room: ChatRoomResponse, otherUser: ConversationUserResponse) => {
      if (stompClient && stompClient.connected) {
        setActiveRoomId(room.id);
        // aici bagi active room la backend
        // router.push(pathName + "?roomId=" + room.id);
        stompClient.publish({
          destination: "/app/changeRoom",
          body: JSON.stringify({
            chatId: room.id,
            userEmail: authUser.email,
          }),
        });
        stompClient.publish({
          destination:
            "/app/chatMessageNotification/deleteAllByReceiverEmailSenderEmail",
          body: JSON.stringify({
            receiverEmail: authUser.email,
            senderEmail: otherUser.email,
          }),
        });
      }
    },
    [stompClient, setActiveRoomId, authUser.email],
  );

  // console.log("active room", activeRoom);
  console.log("chat rooms", chatRooms);

  const fakeRooms = Array.from(
    { length: 20 },
    (_, i) => chatRooms.map((room) => ({ ...room, id: room.id + i })).flat()[0],
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
}

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
        <div className="flex items-center justify-between">
          <p className=" font-bold">
            {otherUser.email} room:{" "}
            {otherUser.connectedChatRoom?.id || "no room"}
          </p>
          <div
            className={cn(
              "w-5 h-5 rounded-full",
              otherUser.connectedStatus === "ONLINE"
                ? "bg-success"
                : "bg-destructive",
            )}
          />
        </div>
      </div>
    );
  },
  (prevProps, nextProps) => isDeepEqual(prevProps, nextProps),
);

ChatRoomItem.displayName = "ChatRoomItem";
