"use client";

import { memo, useState } from "react";
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Home, Menu } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Session } from "next-auth";
import { LinkNav, linksEqual } from "@/components/nav/links";
import Link from "next/link";
import { MenuBarMenuNav } from "@/components/nav/menu-bar-menu-nav";
import { isDeepEqual } from "@/lib/utils";
import { AccordionBarMenuNav } from "@/components/nav/accordion-bar-menu-nav";
import { ModeToggle } from "@/components/common/theme-switch";
import { AvatarImage } from "@radix-ui/react-avatar";
import { Avatar } from "@/components/ui/avatar";

interface Props {
  authUser: Session["user"];
  postsLinks: LinkNav[];
  exercisesLinks: LinkNav[];
  trainingsLinks: LinkNav[];
  ordersLinks: LinkNav[];
  isAdminOrTrainer: boolean;
  isUser: boolean;
  isTrainer: boolean;
  isAdmin: boolean;
}

const BurgerNav = memo<Props>(
  ({
    authUser,
    postsLinks,
    exercisesLinks,
    trainingsLinks,
    ordersLinks,
    isAdminOrTrainer,
    isUser,
    isTrainer,
    isAdmin,
  }: Props) => {
    const [sheetOpen, setSheetOpen] = useState(false);

    return (
      <Sheet open={sheetOpen} onOpenChange={setSheetOpen} modal={true}>
        <SheetTrigger asChild>
          <Button className="sticky top-4 left-6 z-50">
            <Menu />
          </Button>
        </SheetTrigger>
        <SheetContent
          side={"left"}
          className="w-[75%] z-[100] min-h-[100vh] "
          closeClassNames="h-8 w-8"
        >
          <ScrollArea className="pt-4 px-4  h-full min-h-[100vh] flex flex-col gap-6">
            <div className="mb-8">
              <Link
                href="/"
                className="font-bold hover:underline flex items-center justify-start gap-2 text-xl"
              >
                <SheetClose className="flex items-center justify-start gap-2 h-full w-full">
                  <Home className="h-8 w-8" /> Home
                </SheetClose>
              </Link>
            </div>
            {isUser && (
              <div>
                <Link
                  href="/posts/approved"
                  className="font-bold hover:underline text-lg"
                >
                  Posts
                </Link>
              </div>
            )}
            <AccordionBarMenuNav
              title={"Posts"}
              render={!isUser}
              links={postsLinks}
              authUser={authUser}
              setSheetOpen={setSheetOpen}
            />
            <AccordionBarMenuNav
              title={"Exercises"}
              render={isAdminOrTrainer}
              links={exercisesLinks}
              authUser={authUser}
              setSheetOpen={setSheetOpen}
            />
            {isUser && (
              <div className="mt-4">
                <Link
                  href="/trainings/approved"
                  className="font-bold hover:underline text-lg"
                >
                  Trainings
                </Link>
              </div>
            )}
            <AccordionBarMenuNav
              title={"Trainings"}
              render={!isUser}
              links={trainingsLinks}
              authUser={authUser}
              setSheetOpen={setSheetOpen}
            />
            {!isAdmin && (
              <div className="mt-4">
                <Link
                  href={`/users/${authUser?.id}/orders`}
                  className="font-bold hover:underline text-lg"
                >
                  Your Orders
                </Link>
              </div>
            )}
            <AccordionBarMenuNav
              title={"Orders"}
              render={isAdmin}
              links={ordersLinks}
              authUser={authUser}
              setSheetOpen={setSheetOpen}
            />
            {authUser && (
              <Link
                href={`/users/${authUser?.id}`}
                className="text-lg hover:underline font-bold block mt-12"
              >
                <SheetClose className="flex items-center justify-start gap-2 h-full w-full">
                  <p> Your Profile</p>
                  {authUser?.image && (
                    <Avatar>
                      <AvatarImage
                        alt="User profile image"
                        className="rounded-full object-cover aspect-square"
                        src={authUser?.image}
                      />
                    </Avatar>
                  )}
                </SheetClose>
              </Link>
            )}
            {isAdmin && (
              <div className="mt-8">
                <Link
                  href="/admin"
                  className="font-bold hover:underline text-lg "
                >
                  <SheetClose>Admin</SheetClose>
                </Link>
              </div>
            )}
          </ScrollArea>
        </SheetContent>
      </Sheet>
    );
  },
  (prevProps, nextProps) =>
    isDeepEqual(prevProps.authUser, nextProps.authUser) &&
    linksEqual(prevProps.postsLinks, nextProps.postsLinks) &&
    linksEqual(prevProps.exercisesLinks, nextProps.exercisesLinks) &&
    linksEqual(prevProps.trainingsLinks, nextProps.trainingsLinks) &&
    linksEqual(prevProps.ordersLinks, nextProps.ordersLinks) &&
    prevProps.isAdminOrTrainer === nextProps.isAdminOrTrainer &&
    prevProps.isUser === nextProps.isUser &&
    prevProps.isTrainer === nextProps.isTrainer &&
    prevProps.isAdmin === nextProps.isAdmin,
);

BurgerNav.displayName = "BurgerNav";

export { BurgerNav };
