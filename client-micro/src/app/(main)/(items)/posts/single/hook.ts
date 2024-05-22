import { useEffect, useState } from "react";
import { PostResponse, ResponseWithUserDtoEntity } from "@/types/dto";
import { useParams, useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import useFetchStream from "@/hoooks/useFetchStream";
import { BaseError } from "@/types/responses";

export function useGetPost() {
  const [postState, setPostState] = useState<PostResponse | null>(null);
  const { id } = useParams();
  const session = useSession();
  const router = useRouter();

  const authUser = session.data?.user;
  const { messages, error } = useFetchStream<
    ResponseWithUserDtoEntity<PostResponse>,
    BaseError
  >({ path: `/posts/withUser/${id}`, method: "GET", authToken: true });

  console.log(messages);

  const post = messages[0]?.model?.content;
  const user = messages[0]?.user;
  useEffect(() => {
    if (messages.length > 0) {
      setPostState(messages[0]?.model?.content);
    }
  }, [JSON.stringify(messages)]);

  return {
    postState,
    setPostState,
    messages,
    error,
    authUser,
    post,
    user,
    router,
    session,
  };
}
