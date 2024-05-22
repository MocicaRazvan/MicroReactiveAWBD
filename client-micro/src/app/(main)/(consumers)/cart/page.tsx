import { getServerSession } from "next-auth";
import { notFound } from "next/navigation";
import CartContent from "./cart-content";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function CartPage() {
  const session = await getServerSession(authOptions);

  if (!session?.user) {
    return notFound();
  }

  return <CartContent user={session.user} />;
}
