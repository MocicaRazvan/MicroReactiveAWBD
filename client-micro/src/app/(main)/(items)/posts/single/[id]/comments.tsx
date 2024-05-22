"use client";

import LikesDislikes from "@/components/common/single/likes-dislikes";
import { Button } from "@/components/ui/button";
import Loader from "@/components/ui/spinner";
import { fetchStream } from "@/hoooks/fetchStream";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CommentResponse,
  CustomEntityModel,
  PageableResponse,
  ResponseWithUserDtoEntity,
} from "@/types/dto";
import { useSession } from "next-auth/react";
import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import CommentAcc from "../comment-accordion";
import { Trash, Trash2 } from "lucide-react";
import AlertDialogDeleteComment from "@/components/dialogs/comments/delete-comment";

interface Props {
  postId: number;
}

export default function PostComments({ postId }: Props) {
  const pageSize = 10;
  const [comments, setComments] = useState<
    ResponseWithUserDtoEntity<CommentResponse>[]
  >([]);
  const [totalComments, setTotalComments] = useState<number>(0);
  const [page, setPage] = useState<number>(0);
  const session = useSession();

  const { messages, error, isFinished, refetch } = useFetchStream<
    PageableResponse<ResponseWithUserDtoEntity<CommentResponse>>
  >({
    path: `/comments/withUser/byPost/${postId}`,
    method: "PATCH",
    authToken: true,
    body: {
      page,
      size: pageSize,
      sortingCriteria: { createdAt: "desc" },
    },
  });

  const refetchComments = useCallback(() => {
    setComments([]);
    setPage(0);
    setTotalComments(0);
    refetch();
  }, [refetch]);

  useEffect(() => {
    if (messages.length > 0) {
      setTotalComments(messages[0].pageInfo.totalElements);
      setComments((prev) =>
        [
          ...prev,
          ...messages.reduce<ResponseWithUserDtoEntity<CommentResponse>[]>(
            (acc, { content }) =>
              !prev.find((c) => c.model.content.id === content.model.content.id)
                ? [...acc, content]
                : acc,
            [],
          ),
        ].sort(
          (a, b) =>
            new Date(b.model.content.createdAt).getTime() -
            new Date(a.model.content.createdAt).getTime(),
        ),
      );
    }
  }, [JSON.stringify(messages)]);

  const isMore = pageSize * (page + 1) < totalComments;

  const react = useCallback(
    (commentId: number) => async (type: "like" | "dislike") => {
      if (!session.data?.user?.token) return;
      try {
        const resp = await fetchStream<CustomEntityModel<CommentResponse>>({
          path: `/comments/${type}/${commentId}`,
          method: "PATCH",
          token: session.data?.user?.token,
        });

        if (resp.error?.status) {
          console.log(resp.error);
          return;
        }
        const newComment = resp.messages[0]?.content;
        console.log(newComment);

        setComments((prev) =>
          prev.map((c) =>
            c.model.content.id === newComment.id
              ? {
                  ...c,
                  model: {
                    ...c.model,
                    content: {
                      ...c.model.content,
                      userLikes: newComment.userLikes,
                      userDislikes: newComment.userDislikes,
                    },
                  },
                }
              : c,
          ),
        );
      } catch (error) {
        console.log(error);
      }
    },

    [session.data?.user?.token],
  );

  const deleteCommentCallback = useCallback((commentId: number) => {
    setComments((prev) => prev.filter((c) => c.model.content.id !== commentId));
    setTotalComments((prev) => --prev);
  }, []);

  return (
    <div className=" mb-40 flex items-center justify-center flex-col gap-4 transition-all max-w-3xl w-full  mx-auto mt-20">
      <div className="mb-10 w-full">
        <CommentAcc
          postId={postId}
          token={session.data?.user?.token ?? ""}
          refetch={refetchComments}
        />
      </div>
      {comments.map(({ model: { content }, user: { email, id: userId } }) => (
        <div key={content.id} className="w-full border rounded-lg px-4 py-6">
          <div className="flex-col items-center">
            <div className="flex w-full items-center justify-between px-2">
              <div className="flex items-center justify-start gap-4">
                <h3 className="font-bold text-lg">{content.title}</h3>
                <LikesDislikes
                  react={react(content.id)}
                  likes={content.userLikes || []}
                  dislikes={content.userDislikes || []}
                  isLiked={content.userLikes.includes(
                    parseInt(session.data?.user?.id ?? ""),
                  )}
                  isDisliked={content.userDislikes.includes(
                    parseInt(session.data?.user?.id ?? ""),
                  )}
                />
              </div>
              {(parseInt(session.data?.user?.id ?? "") === userId ||
                session.data?.user?.role === "ROLE_ADMIN") && (
                <AlertDialogDeleteComment
                  anchor={
                    <div className="cursor-pointer rounded-xl border border-transparent hover:border-muted p-2">
                      <Trash2 />
                    </div>
                  }
                  callBack={() => deleteCommentCallback(content.id)}
                  token={session.data?.user?.token ?? ""}
                  comment={content}
                />
              )}
            </div>
            <Link
              href={`/users/${userId}`}
              className="text-sm italic cursor-pointer hover:underline"
            >
              Made by: {email}
            </Link>

            <div className="mt-5 px-5">
              <div
                className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
                dangerouslySetInnerHTML={{ __html: content?.body ?? "" }}
              />
            </div>
          </div>
        </div>
      ))}
      {!isFinished && <Loader />}
      {isMore && (
        <Button onClick={() => setPage((prev) => ++prev)}>Load More</Button>
      )}
    </div>
  );
}
