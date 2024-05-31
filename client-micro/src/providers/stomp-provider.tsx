"use client";

import { StompSessionProvider } from "react-stomp-hooks";
import * as React from "react";

export const StompProvider = ({
  children,
  url,
}: {
  children: React.ReactNode;
  url: string;
}) => {
  return (
    <StompSessionProvider url={url} debug={(str) => console.log(str)}>
      {children}
    </StompSessionProvider>
  );
};
