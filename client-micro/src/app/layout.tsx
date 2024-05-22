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

const fontSans = FontSans({
  subsets: ["latin"],
  variable: "--font-sans",
});

export const meta: Metadata = {
  title: "Wellness",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body
        className={cn(
          "min-h-screen bg-background font-sans antialiased ",
          fontSans.variable
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
                  {children}
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
