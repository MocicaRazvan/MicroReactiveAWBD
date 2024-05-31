import ChatMainContent from "@/app/(main)/chat/main-content";
import { ChatRoomResponse, ConversationUserResponse } from "@/types/dto";
import { getServerSession } from "next-auth";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";
import ChatMainContentWrapper from "@/app/(main)/chat/main-content-wrapper";
import { Suspense } from "react";
import LoadingSpinner from "@/components/common/loading-spinner";

export default async function ChatPage() {
  const session = await getServerSession(authOptions);
  if (!session?.user) {
    return null;
  }

  return (
    <div>
      {/*<ChatMainContent*/}
      {/*  initialConnectedUsers={connectedUsers}*/}
      {/*  initialChatRooms={chatRooms}*/}
      {/*  authUser={session.user}*/}
      {/*/>*/}
      <Suspense fallback={<LoadingSpinner />}>
        <ChatMainContentWrapper authUser={session.user} />
      </Suspense>
    </div>
  );
}
