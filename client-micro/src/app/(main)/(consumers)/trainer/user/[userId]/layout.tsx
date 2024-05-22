import { getServerSession } from "next-auth";
import { ReactNode } from "react";

import { redirect } from "next/navigation";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function AuthLayout({
  children,
  params: { userId },
}: {
  children: ReactNode;
  params: { userId: string };
}) {
  const session = await getServerSession(authOptions);
  if (!session?.user) {
    return null;
  }

  if (
    parseInt(session?.user?.id) !== parseInt(userId) &&
    !(session?.user?.role === "ROLE_ADMIN")
  ) {
    redirect("/auth/signin");
  }
  return <div>{children}</div>;
}
