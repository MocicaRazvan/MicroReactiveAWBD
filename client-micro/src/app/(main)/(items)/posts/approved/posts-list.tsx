"use client";

import GridList from "@/components/common/grid-list";
import { PostResponse } from "@/types/dto";
import { useRouter } from "next/navigation";

export default function PostsList() {
  const router = useRouter();
  return (
    <GridList<PostResponse>
      onItemClick={({ id }) => {
        router.push(`/posts/single/${id}`);
      }}
      sizeOptions={[6, 12, 18]}
      path="/posts/approved"
      sortingOptions={["title", "createdAt"]}
    />
  );
}
