"use client";
import {
  ActivationState,
  useStompClient,
  useSubscription,
} from "react-stomp-hooks";
import { useEffect, useState } from "react";
import {
  ChatRoomResponse,
  ConversationUserPayload,
  ConversationUserResponse,
} from "@/types/dto";
import UserList from "@/app/(main)/chat/user-list";
import { Session } from "next-auth";
import { usePathname, useRouter } from "next/navigation";
import ConversationWrapper from "@/components/chat/conversation-wrapper";
import ChatRoom from "@/components/chat/chat-room";

interface ChatMainContentProps {
  initialConnectedUsers: ConversationUserResponse[];
  initialChatRooms: ChatRoomResponse[];
  authUser: NonNullable<Session["user"]>;
}

export default function ChatMainContent({
  initialConnectedUsers,
  initialChatRooms,
  authUser,
}: ChatMainContentProps) {
  const stompClient = useStompClient();
  const router = useRouter();
  const pathName = usePathname();

  const [convUsers, setConvUsers] = useState<ConversationUserResponse[]>(
    initialConnectedUsers,
  );
  const [chatRooms, setChatRooms] =
    useState<ChatRoomResponse[]>(initialChatRooms);

  const [activeRoom, setActiveRoom] = useState<ChatRoomResponse | null>(null);
  const [activeRoomId, setActiveRoomId] = useState<number | null>(null);

  useEffect(() => {
    if (activeRoomId) {
      setActiveRoom(chatRooms.find((room) => room.id === activeRoomId) || null);
    } else {
      setActiveRoom(null);
    }
  }, [activeRoomId, JSON.stringify(chatRooms)]);

  useEffect(() => {
    setChatRooms(initialChatRooms);
  }, [initialChatRooms]);

  useEffect(() => {
    setConvUsers(initialConnectedUsers);
  }, [initialConnectedUsers]);

  useSubscription("/chat/connected", (message) => {
    // console.log("conv user message", message);
    const newMessage = JSON.parse(message.body);

    setConvUsers((prev) => {
      const filteredUsers = prev.filter(
        (user) => user.email !== newMessage.email,
      );
      return newMessage.connectedStatus === "ONLINE"
        ? [...filteredUsers, newMessage]
        : filteredUsers;
    });
  });

  useSubscription(`/user/${authUser.email}/chatRooms`, (message) => {
    // console.log("chat rooms", message);
    const newMessage = JSON.parse(message.body);
    console.log("chat rooms", newMessage);
    setChatRooms((prev) => {
      const filteredRooms = prev.filter((room) => room.id !== newMessage.id);
      return [...filteredRooms, newMessage];
    });
  });

  // console.log("convUsers", convUsers);

  useEffect(() => {
    const handleBeforeUnload = () => {
      if (stompClient && authUser && stompClient.connected) {
        stompClient.publish({
          destination: `/app/disconnectUser/${authUser.email}`,
          body: JSON.stringify({
            email: authUser.email,
          }),
        });
        stompClient.publish({
          destination: "/app/changeRoom",
          body: JSON.stringify({
            chatId: null,
            userEmail: authUser.email,
          }),
        });
      }
    };

    if (stompClient && authUser) {
      const body: ConversationUserPayload = {
        email: authUser.email,
        connectedStatus: "ONLINE",
      };
      stompClient.publish({
        destination: `/app/connectUser/${authUser.email}`,
        body: JSON.stringify(body),
      });

      stompClient.publish({
        destination: "/app/changeRoom",
        body: JSON.stringify({
          chatId: null,
          userEmail: authUser.email,
        }),
      });
    }

    // sometimes on back button cleanup is not called
    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
      handleBeforeUnload();
    };
  }, [JSON.stringify(authUser), stompClient]);

  return (
    <div className="px-10 min-h-[1000px] ">
      <h1>Connected Users</h1>
      <UserList users={convUsers} authUser={authUser} />
      <div className="flex md:flex-row flex-col justify-center items-start w-full gap-6 h-full">
        <div className="flex-1 md:flex-1 h-full border-2 p-4 rounded-md py-6  ">
          <h1
            className="font-bold text-xl tracking-tighter text-center h-[40px] cursor-pointer"
            onClick={() => setActiveRoom(null)}
          >
            Chat rooms
          </h1>
          <hr className="my-2" />
          <ChatRoom
            chatRooms={chatRooms}
            setActiveRoomId={setActiveRoomId}
            activeRoom={activeRoom}
            authUser={authUser}
          />
        </div>

        <div className=" flex-1 md:flex-[3_3_0%]">
          {activeRoom ? (
            <ConversationWrapper
              chatRoomId={activeRoom.id}
              sender={
                activeRoom.users.find(
                  ({ email }) => email === authUser.email,
                ) as ConversationUserResponse
              }
              receiver={
                activeRoom.users.find(
                  ({ email }) => email !== authUser.email,
                ) as ConversationUserResponse
              }
            />
          ) : (
            <div className="flex items-center justify-center h-full min-h-[500px] w-full ">
              <h1 className="text-4xl font-bold tracking-tighter">
                Select a chat room
              </h1>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
