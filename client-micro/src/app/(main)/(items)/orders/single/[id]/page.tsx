import { getServerSession } from "next-auth";
import { redirect } from "next/navigation";
import OrderContentWrapper from "./order-content-wrapper";
import { Suspense } from "react";
import Loader from "@/components/ui/spinner";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function OrderPage({
  params: { id },
}: {
  params: { id: string };
}) {
  const session = await getServerSession(authOptions);

  if (!session?.user) {
    redirect("/auth/signin");
  }

  return (
    <div className="overflow-hidden">
      <Suspense
        fallback={
          <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
            <Loader className="w-full" />
          </section>
        }
      >
        <OrderContentWrapper authUser={session.user} orderId={id} />
      </Suspense>
    </div>
  );
}
