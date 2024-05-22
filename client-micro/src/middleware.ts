import { withAuth } from "next-auth/middleware";
import { NextRequest } from "next/server";

export default withAuth({
  callbacks: {
    authorized: ({ req }) => {
      const sessionToken = req.cookies.get("next-auth.session-token");
      console.log(sessionToken);
      if (req.nextUrl.pathname === "/") {
        return true;
      }
      return !!sessionToken;
    },
  },
  pages: {
    signIn: "/auth/signin",
    signOut: "/auth/signout",
  },
});

export const config = {
  matcher: ["/((?!api|auth|home|_next/static|_next/image.*\\.png$).*)", "/"],
};
