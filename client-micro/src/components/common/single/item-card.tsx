import { cn } from "@/lib/utils";
import Image from "next/image";
import noImg from "../../../../public/noImage.jpg";
import { ReactNode, useMemo } from "react";
import { TitleBodyUser } from "@/types/dto";
import { format, parseISO } from "date-fns";

interface Props<T extends TitleBodyUser> {
  item: T;
  onClick?: () => void;
  generateExtraContent?: (item: T) => ReactNode;
  generateExtraHeader?: (item: T) => ReactNode;
}

export default function ItemCard<T extends TitleBodyUser>({
  item,
  onClick,
  generateExtraContent,
  generateExtraHeader,
}: Props<T>) {
  const body = useMemo(
    () =>
      new DOMParser().parseFromString(item.body, "text/html").documentElement
        .textContent,
    [item.body],
  );
  return (
    <div
      className={cn(
        "flex flex-col items-start gap-2 border rounded-xl p-4 w-full  hover:shadow-lg transition-all duration-300 shadow-foreground hover:shadow-foreground/40 hover:scale-[1.025]",
      )}
    >
      <Image
        alt="Clothing"
        className={cn(
          "rounded-lg object-cover w-full",
          onClick && "cursor-pointer",
        )}
        height="250"
        src={item.images?.[0] || noImg}
        style={{
          aspectRatio: "400/250",
          objectFit: "cover",
        }}
        onClick={() => onClick && onClick()}
        width="400"
      />
      <div className="flex flex-col gap-1 mt-1 w-full">
        <div className="flex items-center w-full justify-between">
          <h2 className="text-lg font-semibold tracking-tight">{item.title}</h2>
          {generateExtraHeader && generateExtraHeader(item)}
          <p>{format(parseISO(item.createdAt), "dd/MM/yyyy")}</p>
        </div>

        <p className="text-sm text-gray-500 dark:text-gray-400 h-36 mt-2">
          {body && body.length > 300 ? body.slice(0, 300) + "..." : body}
        </p>
        {generateExtraContent && (
          <div className=" w-full">{generateExtraContent(item)}</div>
        )}
      </div>
    </div>
  );
}
