import { ModeToggle } from "@/components/common/theme-switch";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Home, Menu } from "lucide-react";
import Link from "next/link";

export default function SheetNav() {
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button className="sticky top-4 left-6 z-50">
          <Menu />
        </Button>
      </SheetTrigger>
      <SheetContent
        className=" w-[75%] z-[100] min-h-[100vh] "
        side={"left"}
        closeClassNames="h-8 w-8"
      >
        <SheetHeader>
          <SheetTitle className="text-center text-2xl">Admin</SheetTitle>
        </SheetHeader>
        <div className="flex flex-col items-center justify-center w-full mt-20 gap-4 sm:gap-7 text-lg">
          <Button asChild variant="outline" className="sm:text-lg">
            <Link href="/">
              <SheetClose className="flex items-center justify-start gap-2">
                <Home className="inline-block mr-2" /> <p>Go to main site</p>
              </SheetClose>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/users">
              <SheetClose className="flex items-center justify-start gap-2">
                Users
              </SheetClose>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/posts">
              <SheetClose className="flex items-center justify-start gap-2">
                Posts{" "}
              </SheetClose>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/exercises">
              <SheetClose className="flex items-center justify-start gap-2">
                Exercises
              </SheetClose>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/trainings">
              <SheetClose className="flex items-center justify-start gap-2">
                Trainings
              </SheetClose>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/orders">
              <SheetClose className="flex items-center justify-start gap-2">
                Orders
              </SheetClose>
            </Link>
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
}
