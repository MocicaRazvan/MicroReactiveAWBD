"use client";

import useFetchStream from "@/hoooks/useFetchStream";
import { CustomEntityModel, OrderResponse } from "@/types/dto";
import { Session } from "next-auth";
import { notFound } from "next/navigation";
import { Suspense, useMemo } from "react";
import OrderContent from "./order-content";
import Loader from "@/components/ui/spinner";

export interface OrderContentWrapperProps {
  authUser: NonNullable<Session["user"]>;
  orderId: string;
}

export default function OrderContentWrapper({
  authUser,
  orderId,
}: OrderContentWrapperProps) {
  const { messages, error, refetch } = useFetchStream<
    CustomEntityModel<OrderResponse>
  >({
    path: `/orders/${orderId}`,
    method: "GET",
    authToken: true,
  });

  const isAdmin = useMemo(
    () => authUser.role === "ROLE_ADMIN",
    [authUser.role],
  );
  const isOwner = useMemo(
    () => messages[0]?.content?.userId === parseInt(authUser.id),
    [messages, authUser.id],
  );
  const orderResponse = useMemo(() => messages[0]?.content, [messages]);

  if (!orderResponse) return <Loader className="w-full" />;

  if (error?.status || (!isAdmin && !isOwner)) {
    notFound();
  }

  return (
    <Suspense
      fallback={
        <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
          <Loader className="w-full" />
        </section>
      }
    >
      <OrderContent
        orderId={orderId}
        isAdmin={isAdmin}
        isOwner={isOwner}
        orderResponse={orderResponse}
        token={authUser.token}
        refetch={refetch}
      />
    </Suspense>
  );
}
