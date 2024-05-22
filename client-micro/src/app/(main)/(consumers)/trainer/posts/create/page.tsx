import PostForm from "@/components/forms/post-form";
import Loader from "@/components/ui/spinner";
import { Suspense } from "react";

export default function CreatePost() {
  return (
    <main className="flex items-center justify-center px-6 py-10">
      <Suspense fallback={<Loader />}>
        <PostForm path="/posts/create" method="POST" />
      </Suspense>
    </main>
  );
}
