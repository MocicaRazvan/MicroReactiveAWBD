import { getServerSession } from "next-auth";
import { ReactNode } from "react";

import { redirect } from "next/navigation";
import SheetNav from "./sheet-nav";
import { authOptions } from "../api/auth/[...nextauth]/auth-options";
import { AdminNav } from "@/app/admin/admin-nav";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  const session = await getServerSession(authOptions);

  if (!session?.user || session?.user?.role !== "ROLE_ADMIN") {
    redirect("/auth/signin");
  }
  return (
    <div>
      <AdminNav />
      {children}
    </div>
  );
}
