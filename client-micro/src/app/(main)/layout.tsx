import Nav from "@/components/nav/nav";
import { ReactNode } from "react";
import { StompProvider } from "@/providers/stomp-provider";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  const spring = process.env.NEXT_PUBLIC_SPRING_CLIENT!;
  return (
    <div>
      <Nav />
      <StompProvider url={spring + "/ws/ws-service"}>{children}</StompProvider>
    </div>
  );
}
