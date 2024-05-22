"use client";

import SheetNav from "@/app/admin/sheet-nav";
import { ModeToggle } from "@/components/common/theme-switch";
import { NavProfile } from "@/components/nav/nav-profile";
import { useSession } from "next-auth/react";
import { useState } from "react";
import { Home } from "lucide-react";
import Link from "next/link";

export const AdminNav = () => {
  const session = useSession();
  const [showProfile, setShowProfile] = useState(false);
  const authUser = session?.data?.user;

  if (!authUser || authUser.role !== "ROLE_ADMIN") {
    return null;
  }
  return (
    <nav
      className="min-h-10 md:flex items-center justify-between px-4 py-2 border-b sticky top-0 bg-opacity-60 z-[99]
    w-full border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 flex-wrap 2xl:border-l 2xl:border-r"
    >
      <div className="hidden md:flex items-center justify-between w-full">
        <div className="flex items-center justify-center gap-4">
          <Link
            href="/"
            className="font-bold hover:underline flex items-center justify-center gap-4 mr-4"
          >
            <Home /> Go to main site
          </Link>
          {["users", "posts", "exercises", "trainings", "orders"].map(
            (link) => (
              <Link
                key={link}
                className="font-bold hover:underline capitalize"
                href={`/admin/${link}`}
              >
                {link}
              </Link>
            ),
          )}
        </div>
        <div className="flex items-center justify-center gap-4">
          <NavProfile
            authUser={authUser}
            setShowProfile={setShowProfile}
            showProfile={showProfile}
          />
          <ModeToggle />
        </div>
      </div>
      <div className="md:hidden flex items-center">
        <SheetNav />
        <div className="w-full flex items-center justify-end">
          <ModeToggle />
        </div>
      </div>
    </nav>
  );
};
//className="font-bold hover:underline"