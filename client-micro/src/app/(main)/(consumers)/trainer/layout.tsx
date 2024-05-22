import { getServerSession } from "next-auth";
import { ReactNode } from "react";

import { redirect } from "next/navigation";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function TrainerLayout({
  children,
}: {
  children: ReactNode;
}) {
  const session = await getServerSession(authOptions);
  if (!session?.user) {
    return null;
  }

  if (
    session?.user?.role !== "ROLE_TRAINER" &&
    session?.user?.role !== "ROLE_ADMIN"
  ) {
    redirect("/auth/signin");
  }
  return <div>{children}</div>;
}
