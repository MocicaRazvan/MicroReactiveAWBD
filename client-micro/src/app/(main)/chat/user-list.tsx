"use client";
import { ConversationUserResponse } from "@/types/dto";
import { memo } from "react";
import { cn, isDeepEqual } from "@/lib/utils";
import { useStompClient } from "react-stomp-hooks";
import { Session } from "next-auth";

interface UserListProps {
  users: ConversationUserResponse[];
  authUser: NonNullable<Session["user"]>;
}

interface UserItemProps {
  user: ConversationUserResponse;
  authUser: NonNullable<Session["user"]>;
}

export default function UserList({ users, authUser }: UserListProps) {
  console.log("users", users);
  return users.map((user) => (
    <UserItem key={user.email} user={user} authUser={authUser} />
  ));
}

const UserItem = memo(
  ({ user, authUser }: UserItemProps) => {
    const stompClient = useStompClient();
    return (
      <div
        className={cn(
          user.email !== authUser.email && "cursor-pointer",
          "border",
        )}
        onClick={() => {
          if (user.email !== authUser.email) {
            console.log("creating chat room");
            stompClient?.publish({
              destination: "/app/addChatRoom",
              body: JSON.stringify({
                users: [
                  { email: user.email, connectedStatus: user.connectedStatus },
                  {
                    email: authUser.email,
                    connectedStatus: "ONLINE",
                  },
                ],
              }),
            });
          }
        }}
      >
        {user.email} {user.connectedStatus}{" "}
        {user.connectedChatRoom?.id || "no room"}
      </div>
    );
  },
  (prev, next) => isDeepEqual(prev, next),
);

UserItem.displayName = "UserItem";
