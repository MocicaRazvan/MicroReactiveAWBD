import { cn } from "@/lib/utils";
import { ThumbsDown, ThumbsUp } from "lucide-react";

interface Props {
  react: (type: "like" | "dislike") => Promise<void>;
  likes: number[];
  dislikes: number[];
  isLiked: boolean;
  isDisliked: boolean;
}

export default function LikesDislikes({
  react,
  likes,
  dislikes,
  isLiked,
  isDisliked,
}: Props) {
  return (
    <>
      <div
        className=" flex items-center justify-center gap-2 cursor-pointer"
        onClick={() => react("like")}
      >
        <ThumbsUp size={24} className={cn(isLiked && "text-green-600")} />
        <p>{likes.length}</p>
      </div>
      <div
        className=" flex items-center justify-center gap-2 cursor-pointer"
        onClick={() => react("dislike")}
      >
        <ThumbsDown
          size={24}
          className={cn(isDisliked && "text-destructive")}
        />
        <p>{dislikes.length}</p>
      </div>
    </>
  );
}
