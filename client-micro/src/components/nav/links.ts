import { Role } from "@/types/fetch-utils";
import { Session } from "next-auth";
import { isDeepEqual } from "@/lib/utils";

export interface LinkNav {
  text: string;
  href: string;
  role: Role;
}

type createLinks = (authUser: NonNullable<Session["user"]>) => LinkNav[];
export const createPostsLinks: createLinks = ({ id }): LinkNav[] => [
  {
    text: "Approved Posts",
    href: "/posts/approved",
    role: "ROLE_TRAINER",
  },
  {
    text: "All Posts",
    href: "/admin/posts",
    role: "ROLE_ADMIN",
  },
  {
    text: "Your Posts",
    href: `/trainer/user/${id}/posts`,
    role: "ROLE_TRAINER",
  },
  {
    text: "Create Post",
    href: `/trainer/posts/create`,
    role: "ROLE_TRAINER",
  },
];

export const createExercisesLinks: createLinks = ({ id }): LinkNav[] => [
  {
    text: "Your Exercises",
    href: `/trainer/user/${id}/exercises`,
    role: "ROLE_TRAINER",
  },
  {
    text: "Create Exercise",
    href: `/trainer/exercises/create`,
    role: "ROLE_TRAINER",
  },
  {
    text: "All Exercises",
    href: "/admin/exercises",
    role: "ROLE_ADMIN",
  },
];

export const createTrainingsLinks: createLinks = ({ id }): LinkNav[] => [
  {
    text: "Trainings Approved",
    href: "/trainings/approved",
    role: "ROLE_TRAINER",
  },
  {
    text: "Your Trainings",
    href: `/trainer/user/${id}/trainings`,
    role: "ROLE_TRAINER",
  },
  {
    text: "Create Training",
    href: `/trainer/trainings/create`,
    role: "ROLE_TRAINER",
  },
  {
    text: "All Trainings",
    href: "/admin/trainings",
    role: "ROLE_ADMIN",
  },
];

export const createOrdersLinks: createLinks = ({ id }): LinkNav[] => [
  {
    text: "Your Orders",
    href: `/users/${id}/orders`,
    role: "ROLE_USER",
  },
  {
    text: "All Orders",
    href: "/admin/orders",
    role: "ROLE_ADMIN",
  },
];

export const linkFactory = (authUser: Session["user"], f: createLinks) =>
  authUser ? f(authUser) : [];

export const linksEqual = (a: LinkNav[], b: LinkNav[]) =>
  a.length === b.length && a.every((link, i) => isDeepEqual(link, b[i]));

export const shouldRenderLink = (
  authUser: NonNullable<Session["user"]>,
  linkRole: Role,
) => {
  switch (authUser.role) {
    case "ROLE_ADMIN":
      return true;
    case "ROLE_TRAINER":
      return linkRole !== "ROLE_ADMIN";
    case "ROLE_USER":
      return linkRole === "ROLE_USER";
    default:
      return false;
  }
};
