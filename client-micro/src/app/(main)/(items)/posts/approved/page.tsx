import { Suspense } from "react";
import Loader from "@/components/ui/spinner";
import PostsList from "./posts-list";

export default function PostsApprovedPage() {
  return (
    <section className="w-full min-h-[calc(100vh-4rem)] transition-all py-5 px-4 max-w-[1250px] mx-auto ">
      <h1 className="text-3xl font-bold tracking-tight">Posts</h1>
      <p className="text-gray-500 dark:text-gray-400">
        Made with love by our trainers and approved by our team.
      </p>
      <Suspense
        fallback={
          <section className="w-full min-h-[calc(100vh-4rem)] transition-all py-5 px-4 max-w-[1250px] mx-auto ">
            <Loader className="w-full" />
          </section>
        }
      >
        <PostsList />
      </Suspense>
    </section>
  );
}
