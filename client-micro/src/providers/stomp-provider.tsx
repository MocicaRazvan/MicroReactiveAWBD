"use client";

import { StompSessionProvider } from "react-stomp-hooks";
import * as React from "react";
import { useSession } from "next-auth/react";
import { Session } from "next-auth";

export const StompProvider = ({
  children,
  url,
  authUser,
}: {
  children: React.ReactNode;
  url: string;
  authUser: NonNullable<Session["user"]>;
}) => {
  const headers = {
    Authorization: `Bearer ${authUser.token}`,
  };
  return (
    <StompSessionProvider
      url={url + `?authToken=${authUser.token}`}
      connectHeaders={headers}
      debug={(str) => console.log(str)}
    >
      {children}
    </StompSessionProvider>
  );
};
