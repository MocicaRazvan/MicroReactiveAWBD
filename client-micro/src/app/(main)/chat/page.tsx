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
      <Suspense fallback={<LoadingSpinner />}>
        <ChatMainContentWrapper authUser={session.user} />
      </Suspense>
    </div>
  );
}
