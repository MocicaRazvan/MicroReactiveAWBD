import Nav from "@/components/nav/nav";
import { ReactNode } from "react";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <div>
      <Nav />
      {children}
    </div>
  );
}
