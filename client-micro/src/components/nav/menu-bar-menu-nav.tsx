"use client";
import { Role } from "@/types/fetch-utils";
import { Session } from "next-auth";
import { memo } from "react";
import { isDeepEqual } from "@/lib/utils";
import {
  Menubar,
  MenubarContent,
  MenubarMenu,
  MenubarTrigger,
} from "@/components/ui/menubar";
import { MenuBarItemsNav } from "@/components/nav/menu-bar-items-nav";
import { LinkNav } from "@/components/nav/links";

export interface LinksProps {
  links: LinkNav[];
  authUser: Session["user"];
}

interface MenubarMenuNavProps extends LinksProps {
  title: string;
  render: boolean;
  isDropdown?: boolean;
}

const MenuBarMenuNav = memo(
  ({
    links,
    authUser,
    title,
    render,
    isDropdown = false,
  }: MenubarMenuNavProps) => {
    if (!authUser || !render) return null;
    return (
      <Menubar>
        <MenubarMenu>
          <MenubarTrigger className="cursor-pointer">{title}</MenubarTrigger>
          <MenubarContent>
            <MenuBarItemsNav
              links={links}
              authUser={authUser}
              isDropdown={isDropdown}
            />
          </MenubarContent>
        </MenubarMenu>
      </Menubar>
    );
  },
  (prevProps, nextProps) =>
    isDeepEqual(prevProps.authUser, nextProps.authUser) &&
    prevProps.links.every((link, i) => isDeepEqual(link, nextProps.links[i])) &&
    prevProps.title === nextProps.title,
);

MenuBarMenuNav.displayName = "MenuBarMenuNav";

export { MenuBarMenuNav };
