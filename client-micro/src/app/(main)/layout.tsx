import Nav from "@/components/nav/nav";
import { ReactNode } from "react";
import { StompProvider } from "@/providers/stomp-provider";

export default async function AuthLayout({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <div>
      <Nav />
      <StompProvider url={"http://localhost:8080/ws/ws-service"}>
        {children}
      </StompProvider>
    </div>
  );
}
