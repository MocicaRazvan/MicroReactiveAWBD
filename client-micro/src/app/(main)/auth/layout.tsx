import { getServerSession } from "next-auth";
import { ReactNode } from "react";

import { redirect } from "next/navigation";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  const session = await getServerSession(authOptions);

  console.log(session);

  // if (session?.user) {
  //   redirect(`/users/${session.user.id}`);
  // }
  return <div>{children}</div>;
}
