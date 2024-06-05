import type { Metadata } from "next";
import { Inter as FontSans } from "next/font/google";
import "./globals.css";
import { Providers } from "@/providers/session-provider";
import { cn } from "@/lib/utils";
import { ThemeProvider } from "@/providers/theme-provider";
import Nav from "@/components/nav/nav";
import { CartContext, CartProvider } from "@/context/cart-context";
import { Toaster } from "@/components/ui/toaster";
import LennisProvder from "@/providers/lennis-provider";
import Footer from "@/components/common/footer";
import { StompProvider } from "@/providers/stomp-provider";
import { ChatMessageNotificationProvider } from "@/context/chat-message-notification-context";
import { getServerSession } from "next-auth";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";
import { ChatProvider } from "@/context/chat-context";
import SessionWrapper from "@/app/session-wrapper";

const fontSans = FontSans({
  subsets: ["latin"],
  variable: "--font-sans",
});

export const meta: Metadata = {
  title: "Wellness",
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const spring = process.env.NEXT_PUBLIC_SPRING_CLIENT!;
  const session = await getServerSession(authOptions);
  const isUser = !!(session && session.user && true);

  return (
    <html lang="en" suppressHydrationWarning>
      <body
        className={cn(
          "min-h-screen bg-background font-sans antialiased ",
          fontSans.variable,
        )}
      >
        <ThemeProvider
          attribute="class"
          defaultTheme="system"
          enableSystem
          disableTransitionOnChange
        >
          <CartProvider>
            <Providers>
              <LennisProvder>
                <div className="max-w-[1700px] flex-col items-center justify-center w-full mx-auto">
                  {/* <Nav /> */}
                  {/*{!isUser ? (*/}
                  {/*  children*/}
                  {/*) : (*/}
                  {/*  <StompProvider*/}
                  {/*    url={spring + "/ws/ws-service"}*/}
                  {/*    authUser={session.user!}*/}
                  {/*  >*/}
                  {/*    <ChatProvider authUser={session.user!}>*/}
                  {/*      <ChatMessageNotificationProvider*/}
                  {/*        authUser={session.user!}*/}
                  {/*      >*/}
                  {/*        {children}*/}
                  {/*      </ChatMessageNotificationProvider>*/}
                  {/*    </ChatProvider>*/}
                  {/*  </StompProvider>*/}
                  {/*)}*/}

                  <SessionWrapper>{children}</SessionWrapper>
                  <Footer />
                </div>
                <Toaster />
              </LennisProvder>
            </Providers>
          </CartProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
