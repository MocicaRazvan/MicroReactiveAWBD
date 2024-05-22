"use client";
import PostForm from "@/components/forms/post-form";
import { Option } from "@/components/ui/multiple-selector";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  PostResponse,
  ResponseWithUserDtoEntity,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useSession } from "next-auth/react";
import { notFound, useParams } from "next/navigation";
import { Suspense, useMemo } from "react";

export default function CreatePost() {
  const session = useSession();
  const { postId, userId } = useParams();
  const { messages, error } = useFetchStream<
    CustomEntityModel<PostResponse>,
    BaseError
  >({ path: `/posts/${postId}`, method: "GET", authToken: true });

  const post = messages[0]?.content;

  const tagsOptions: Option[] = useMemo(
    () => post?.tags.map((tag) => ({ label: tag, value: tag })),
    [post?.tags],
  );

  if (!session?.data?.user) return null;
  if (
    parseInt(session?.data?.user?.id) !== parseInt(userId as string) ||
    error?.status
  ) {
    notFound();
  }

  if (!post) {
    return (
      <main className="flex items-center justify-center px-6 py-10">
        <Loader />
      </main>
    );
  }

  return (
    <main className="flex items-center justify-center px-6 py-10">
      <Suspense fallback={<Loader />}>
        <PostForm
          body={post.body}
          images={post.images}
          tags={tagsOptions}
          title={post.title}
          path={`/posts/update/${postId}`}
          method="PUT"
          submitText="Update Post"
          header={`Update Post: ${post.title}`}
        />
      </Suspense>
    </main>
  );
}
