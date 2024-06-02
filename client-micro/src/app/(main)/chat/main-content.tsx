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
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import ConversationWrapper from "@/components/chat/conversation-wrapper";
import { ChatRoom } from "@/components/chat/chat-room";
import { useChatNotification } from "@/context/chat-message-notification-context";
import { parseQueryParamAsInt } from "@/lib/utils";

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
  // const router = useRouter();
  // const pathName = usePathname();
  const searchParams = useSearchParams();
  const chatId = searchParams.get("chatId");
  console.log("chatId", chatId);
  const email = searchParams.get("email");

  const { getNotificationState, removeBySender } = useChatNotification();

  // const notificationState = getNotificationState();
  // console.log("notification state", notificationState);

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
        setActiveRoom(room);
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

  // useEffect(() => {
  //   if (activeChatId) {
  //     setActiveRoom(chatRooms.find((room) => room.id === activeChatId) || null);
  //   } else {
  //     setActiveRoom(null);
  //   }
  // }, [activeChatId, JSON.stringify(chatRooms)]);

  // useEffect(() => {
  //   if (email && email !== authUser.email) {
  //     const existingRoom = chatRooms.find((room) =>
  //       room.users.some((user) => user.email === email),
  //     );
  //     if (existingRoom) {
  //       setActiveRoomId(existingRoom.id);
  //       router.replace(`${pathName}?chatId=${existingRoom.id}`);
  //     }
  //   }
  // }, [authUser.email, chatId, JSON.stringify(chatRooms), email, pathName]);

  // useEffect(() => {
  //   setChatRooms(initialChatRooms);
  // }, [initialChatRooms]);
  //
  // useEffect(() => {
  //   setConvUsers(initialConnectedUsers);
  // }, [initialConnectedUsers]);

  // useSubscription("/chat/connected", (message) => {
  //   // console.log("conv user message", message);
  //   const newMessage = JSON.parse(message.body);
  //
  //   setConvUsers((prev) => {
  //     const filteredUsers = prev.filter(
  //       (user) => user.email !== newMessage.email,
  //     );
  //     return newMessage.connectedStatus === "ONLINE"
  //       ? [...filteredUsers, newMessage]
  //       : filteredUsers;
  //   });
  // });

  useSubscription(`/user/${authUser.email}/chatRooms`, (message) => {
    // console.log("chat rooms", message);
    const newMessage = JSON.parse(message.body);
    console.log("chat rooms", newMessage);
    setChatRooms((prev) => {
      const filteredRooms = prev.filter((room) => room.id !== newMessage.id);
      return [...filteredRooms, newMessage];
    });
  });

  const [clientInitialized, setClientInitialized] = useState(false);

  useEffect(() => {
    console.log("USE: useEffect triggered");

    const handleBeforeUnload = () => {
      console.log("USE: handleBeforeUnload called");
      if (stompClient?.connected) {
        console.log("USE: Disconnecting user:", authUser.email);
        stompClient.publish({
          destination: `/app/disconnectUser/${authUser.email}`,
          body: JSON.stringify({
            email: authUser.email,
          }),
        });
      }
    };

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

    // window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      console.log("USE: Cleanup called");
      // window.removeEventListener("beforeunload", handleBeforeUnload);
      // handleBeforeUnload();
    };
  }, [
    authUser.email,
    stompClient?.connected,
    clientInitialized,
    initialChatId,
    JSON.stringify(initialChatRooms),
  ]);

  // // saftey measure
  // useEffect(() => {
  //   ////
  //
  //   if (initialChatId !== null && stompClient?.connected && clientInitialized) {
  //     const validId = initialChatRooms.find(
  //       (room) => room.id === initialChatId,
  //     );
  //     console.log("USE _: initialChatId", initialChatId);
  //     console.log("USE _: validId", validId);
  //     if (validId) {
  //       stompClient.publish({
  //         destination: "/app/changeRoom",
  //         body: JSON.stringify({
  //           chatId: initialChatId,
  //           userEmail: authUser.email,
  //         }),
  //       });
  //     }
  //   }
  //   ///
  // }, [
  //   authUser.email,
  //   initialChatId,
  //   stompClient?.connected,
  //   clientInitialized,
  // ]);

  console.log("USE:activeId", activeRoomId);

  // useEffect(() => {
  //   const handleBeforeUnload = () => {
  //     if (stompClient && authUser && stompClient.connected) {
  //       stompClient.publish({
  //         destination: `/app/disconnectUser/${authUser.email}`,
  //         body: JSON.stringify({
  //           email: authUser.email,
  //         }),
  //       });
  //       // redundant
  //       // stompClient.publish({
  //       //   destination: "/app/changeRoom",
  //       //   body: JSON.stringify({
  //       //     chatId: null,
  //       //     userEmail: authUser.email,
  //       //   }),
  //       // });
  //       // console.log("handleBeforeUnload");
  //       // setActiveChatId(null);
  //     }
  //   };
  //
  //   if (stompClient && authUser) {
  //     // const body: ConversationUserPayload = {
  //     //   email: authUser.email,
  //     //   connectedStatus: "ONLINE",
  //     //   connectedChatRoomId: activeChatId ? activeChatId : undefined,
  //     // };
  //     stompClient.publish({
  //       destination: `/app/connectUser/${authUser.email}`,
  //       // body: JSON.stringify(body),
  //     });
  //     //
  //     // if (!activeChatId) {
  //     //   stompClient.publish({
  //     //     destination: "/app/changeRoom",
  //     //     body: JSON.stringify({
  //     //       chatId: null,
  //     //       // parseQueryParamAsInt(chatId, null),
  //     //       userEmail: authUser.email,
  //     //     }),
  //     //   });
  //     // }
  //   }
  //
  //   // sometimes on back button cleanup is not called
  //   window.addEventListener("beforeunload", handleBeforeUnload);
  //
  //   return () => {
  //     window.removeEventListener("beforeunload", handleBeforeUnload);
  //     handleBeforeUnload();
  //   };
  // }, [JSON.stringify(authUser), stompClient]);

  // console.log("activeChatId", activeChatId);

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
          //
          //     const otherUser = room.users.find(
          //       (user) => user.email !== authUser.email,
          //     );
          //     if (otherUser) {
          //       stompClient.publish({
          //         destination:
          //           "/app/chatMessageNotification/deleteAllByReceiverEmailSenderEmail",
          //         body: JSON.stringify({
          //           receiverEmail: authUser.email,
          //           senderEmail: otherUser.email,
          //         }),
          //       });
          //       removeBySender({
          //         stompClient,
          //         senderEmail: otherUser.email,
          //         receiverEmail: authUser.email,
          //       });
          //     }
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

  return (
    <div className="px-10 min-h-[1000px] pt-10 ">
      <h1 className="text-4xl font-bold tracking-tighter mb-10 text-center">
        Chat
      </h1>
      {/*<h1>Connected Users</h1>*/}
      {/*<UserList users={convUsers} authUser={authUser} />*/}
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
            // setActiveRoomId={setActiveChatId}
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
