"use client";
import {
  ActivationState,
  useStompClient,
  useSubscription,
} from "react-stomp-hooks";
import { useCallback, useEffect, useState } from "react";
import {
  ChatRoomResponse,
  ConversationUserPayload,
  ConversationUserResponse,
} from "@/types/dto";
import UserList from "@/app/(main)/chat/user-list";
import { Session } from "next-auth";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import ConversationWrapper from "@/components/chat/conversation-wrapper";
import { ChatRoom } from "@/components/chat/chat-room";
import { useChatNotification } from "@/context/chat-message-notification-context";
import { parseQueryParamAsInt } from "@/lib/utils";
import { fetchStream } from "@/hoooks/fetchStream";

// import { useChatContext } from "@/context/chat-context";

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

  const searchParams = useSearchParams();
  const chatId = searchParams.get("chatId");
  console.log("chatId", chatId);
  const email = searchParams.get("email");

  const { getNotificationState, removeBySender } = useChatNotification();

  // const [convUsers, setConvUsers] = useState<ConversationUserResponse[]>(
  //   initialConnectedUsers,
  // );
  const [chatRooms, setChatRooms] =
    useState<ChatRoomResponse[]>(initialChatRooms);

  const [activeRoom, setActiveRoom] = useState<ChatRoomResponse | null>(null);
  // const { activeChatId, setActiveChatId } = useChatContext();
  const [activeRoomId, setActiveRoomId] = useState<number | null>(
    chatId ? parseQueryParamAsInt(chatId, null) : null,
    // activeChatId,
    // null,
  );

  const [initialChatId, setInitialChatId] = useState<number | null>(null);

  useEffect(() => {
    const initialId = searchParams.get("chatId");
    if (initialId) {
      setInitialChatId(parseQueryParamAsInt(initialId, null));
    }
  }, []);

  console.log("initialChatId", initialChatId);

  useEffect(() => {
    // setActiveChatId(activeRoomId);
    if (activeRoomId) {
      const room = chatRooms.find((room) => room.id === activeRoomId);
      if (room) {
        const users = [
          ...room.users.filter(({ email }) => email !== authUser.email),
          {
            ...room.users.find(({ email }) => email === authUser.email),
            connectedChatRoom: room,
          },
        ] as ConversationUserResponse[];

        console.log(
          "rue",
          room.users.map((user) => user.connectedChatRoom?.id),
        );
        setActiveRoom({ ...room, users });
      } else {
        setActiveRoom(null);
        setActiveRoomId(null);
      }
    } else {
      setActiveRoom(null);
    }
  }, [activeRoomId, JSON.stringify(chatRooms)]);

  useEffect(() => {
    const params = new URLSearchParams(searchParams.toString());

    if (activeRoomId) {
      params.set("chatId", activeRoomId.toString());
    } else {
      if (params.has("chatId")) {
        params.delete("chatId");
      }
    }
    window.history.pushState(null, "", `?${params.toString()}`);
  }, [activeRoomId, searchParams]);

  useSubscription(`/user/${authUser.email}/chatRooms`, (message) => {
    // console.log("chat rooms", message);
    const newMessage = JSON.parse(message.body);
    console.log("chat rooms", newMessage);
    setChatRooms((prev) => {
      const filteredRooms = prev.filter((room) => room.id !== newMessage.id);
      return [...filteredRooms, newMessage];
    });
  });
  useSubscription(`/user/${authUser.email}/chatRooms/delete`, (message) => {
    // console.log("chat rooms", message);
    const newMessage = JSON.parse(message.body);
    console.log("chat rooms delete", newMessage);
    setChatRooms((prev) => prev.filter((room) => room.id !== newMessage.id));
  });

  const [clientInitialized, setClientInitialized] = useState(false);

  useEffect(() => {
    console.log("USE: useEffect triggered");
    ////

    if (initialChatId !== null && stompClient?.connected) {
      const validId = initialChatRooms.find(
        (room) => room.id === initialChatId,
      );
      console.log("USE: initialChatId", initialChatId);
      console.log("USE: validId", validId);
      if (validId) {
        stompClient.publish({
          destination: "/app/changeRoom",
          body: JSON.stringify({
            chatId: initialChatId,
            userEmail: authUser.email,
          }),
        });
      }
    }
    ///

    if (!clientInitialized && stompClient?.connected) {
      console.log("USE: Connecting user:", authUser.email);
      stompClient.publish({
        destination: `/app/connectUser/${authUser.email}`,
        body: JSON.stringify({
          email: authUser.email,
        }),
      });
      setClientInitialized(true);
    }

    return () => {
      console.log("USE: Cleanup called");
    };
  }, [
    authUser.email,
    stompClient?.connected,
    clientInitialized,
    initialChatId,
    JSON.stringify(initialChatRooms),
  ]);

  console.log("USE:activeId", activeRoomId);

  useEffect(() => {
    console.log("USE2: useEffect triggered " + chatId);
    if (stompClient && stompClient.connected && chatId) {
      const roomId = parseQueryParamAsInt(chatId, null);
      const room = chatRooms.find((r) => r.id === roomId);

      if (room) {
        console.log("USE2: active ", activeRoomId);
        console.log("USE2: room ", room.id);
        setActiveRoomId(room.id);
        // setActiveChatId(room.id);
        if (activeRoomId !== room.id) {
          stompClient.publish({
            destination: "/app/changeRoom",
            body: JSON.stringify({
              chatId: room.id,
              userEmail: authUser.email,
            }),
          });
          const otherUser = room.users.find(
            (user) => user.email !== authUser.email,
          );
          if (otherUser) {
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
        }
      }
    }
  }, [
    authUser.email,
    chatId,
    JSON.stringify(chatRooms),
    removeBySender,
    // setActiveChatId,
    !!stompClient?.connected,
    activeRoomId,
  ]);

  const handleRoomDelete = useCallback(
    async (chatRoomId: number) => {
      if (!authUser?.email || !authUser?.token) return;
      const { messages, error, isFinished } = await fetchStream({
        path: "/ws-http/chatRooms",
        method: "DELETE",
        body: { chatRoomId, senderEmail: authUser.email },
        acceptHeader: "application/json",
        token: authUser.token,
      });
      if (error) {
        console.error("Error deleting chat room:", error);
        return;
      } else {
        console.log("Chat room deleted");
        setChatRooms((prev) => prev.filter((room) => room.id !== chatRoomId));
        router.push("/chat");
      }
    },
    [authUser?.email, router, authUser?.token],
  );

  return (
    <div className="px-10 min-h-[1000px] pt-10 ">
      <h1 className="text-4xl font-bold tracking-tighter mb-10 text-center">
        Chat
      </h1>
      {/*<h1>Connected Users</h1>*/}
      {/*<UserList users={convUsers} authUser={authUser} />*/}
      <div className="flex md:flex-row flex-col justify-center items-start w-full gap-6 h-full">
        <div className="flex-1 md:flex-1 h-full border-2 p-4 rounded-md py-6  ">
          <h1 className="font-bold text-xl tracking-tighter text-center h-[40px] ">
            Chat rooms
          </h1>
          <hr className="my-2" />
          <ChatRoom
            chatRooms={chatRooms}
            setActiveRoomId={setActiveRoomId}
            // setActiveRoomId={setActiveChatId}
            activeRoom={activeRoom}
            authUser={authUser}
            handleRoomDelete={handleRoomDelete}
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
