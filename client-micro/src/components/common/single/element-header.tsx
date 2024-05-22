"use client";
import LikesDislikes from "@/components/common/single/likes-dislikes";
import { Approve } from "@/types/dto";
import { cn } from "@/lib/utils";

interface Props<T extends Approve> {
  elementState: T;
  react?: (type: "like" | "dislike") => Promise<void>;
  isLiked?: boolean;
  isDisliked?: boolean;
}

export default function ElementHeader<T extends Approve>({
  elementState,
  react,
  isLiked,
  isDisliked,
}: Props<T>) {
  const showLikes = react && isLiked !== undefined && isDisliked !== undefined;
  return (
    <div className="w-3/4 mx-auto flex flex-col md:flex-row items-center justify-center gap-4 md:gap-20 mb-2">
      {elementState?.approved ? (
        showLikes ? (
          <div className=" w-[250px] flex items-center justify-center gap-4 order-1 md:order-0">
            <LikesDislikes
              react={react}
              likes={elementState?.userLikes || []}
              dislikes={elementState?.userDislikes || []}
              isLiked={isLiked || false}
              isDisliked={isDisliked || false}
            />
          </div>
        ) : null
      ) : (
        <h2 className="text-2xl font-bold text-center md:text-start text-destructive w-[250px] order-1 md:order-0">
          Not Approved
        </h2>
      )}
      <div className="flex-1 flex items-center justify-center order-0 md:order-1">
        <h1
          className={cn(
            "text-6xl tracking-tighter font-bold text-center",
            showLikes && "md:translate-x-[-125px] ",
          )}
        >
          {elementState?.title}
        </h1>
      </div>
    </div>
  );
}
