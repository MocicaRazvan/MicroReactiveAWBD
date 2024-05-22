import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";
import UserProfile from "@/components/common/user-profile";
import Link from "next/link";
import { Session } from "next-auth";
import { Dispatch, SetStateAction } from "react";

interface Props {
  authUser: NonNullable<Session["user"]>;
  setShowProfile: Dispatch<SetStateAction<boolean>>;
  showProfile: boolean;
}

export const NavProfile = ({
  authUser,
  setShowProfile,
  showProfile,
}: Props) => {
  return (
    <div className="flex items-center justify-center gap-2 w-full md:w-fit flex-wrap">
      <div className="flex items-center justify-center">
        {authUser?.image ? (
          <Avatar
            className="cursor-pointer"
            onClick={() => setShowProfile((prev) => !prev)}
          >
            <AvatarImage src={authUser?.image} alt={authUser?.email} />
          </Avatar>
        ) : (
          <Link href={`/users/${authUser?.id}`} className="hover:underline">
            {authUser.email}
          </Link>
        )}
      </div>
      <div
        className={cn(
          "transition-all ease-in-out duration-300 absolute right-0  z-50",
          "transform origin-top bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 top-[100%] overflow-hidden shadow-2xl",
          {
            "max-h-0": !showProfile,
            "max-h-[500px] ": showProfile,
          },
        )}
      >
        {<UserProfile user={authUser} />}
      </div>
      <Link href="/auth/signout" className="font-bold ml-3 hover:underline">
        Sign Out
      </Link>
    </div>
  );
};
