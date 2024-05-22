import { Dispatch, Fragment, memo, SetStateAction, useCallback } from "react";
import { LinksProps } from "@/components/nav/menu-bar-menu-nav";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { MenuBarItemsNav } from "@/components/nav/menu-bar-items-nav";
import { Role } from "@/types/fetch-utils";
import { shouldRenderLink } from "@/components/nav/links";
import Link from "next/link";
import { SheetClose } from "@/components/ui/sheet";

interface Props extends LinksProps {
  title: string;
  render: boolean;
  setSheetOpen: Dispatch<SetStateAction<boolean>>;
}

const AccordionBarMenuNav = memo<Props>(
  ({ links, authUser, title, render, setSheetOpen }: Props) => {
    const shouldRenderNavLink = useCallback(
      (linkRole: Role) =>
        authUser ? shouldRenderLink(authUser, linkRole) : false,
      [authUser],
    );
    if (!authUser || !render) return null;
    return (
      <Accordion type={"single"} collapsible className="w-full">
        <AccordionItem value={"item-1"}>
          <AccordionTrigger className="text-xl font-bold">
            {title}
          </AccordionTrigger>
          <AccordionContent className="flex flex-col items-center justify-center gap-2">
            {links.map(({ text, href, role }) => {
              return (
                shouldRenderNavLink(role) && (
                  <SheetClose
                    key={href}
                    className="w-full flex items-center justify-start px-4 py-2 border rounded group"
                  >
                    <Link
                      href={href}
                      className={
                        "text-start w-full text-lg group-hover:underline"
                      }
                      onClick={() => setSheetOpen(false)}
                    >
                      {text}
                    </Link>
                  </SheetClose>
                )
              );
            })}
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    );
  },
  (prevProps, nextProps) => true,
);

AccordionBarMenuNav.displayName = "AccordionBarMenuNav";

export { AccordionBarMenuNav };
