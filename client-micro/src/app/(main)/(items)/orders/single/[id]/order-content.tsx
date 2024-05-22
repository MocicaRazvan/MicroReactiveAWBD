"use client";

import {
  CustomEntityModel,
  OrderResponse,
  PageableResponse,
  TrainingResponse,
} from "@/types/dto";
import { OrderContentWrapperProps } from "./order-content-wrapper";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  notFound,
  usePathname,
  useRouter,
  useSearchParams,
} from "next/navigation";
import { useCallback, useEffect, useMemo, useState } from "react";
import Loader from "@/components/ui/spinner";
import Image from "next/image";
import noImg from "../../../../../../../public/noImage.jpg";
import Link from "next/link";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import PayDrawer from "./pay-drawer";
import useWindowSize from "react-use/lib/useWindowSize";
import Confetti from "react-confetti";
import { Code } from "lucide-react";
import { getCSSVariableValue, roundToDecimalPlaces } from "@/lib/utils";
import { useSearchParam } from "react-use";

interface OrderContentProps extends Omit<OrderContentWrapperProps, "authUser"> {
  orderResponse: OrderResponse;
  isOwner: boolean;
  isAdmin: boolean;
  token: string;
  refetch: () => void;
}

export default function OrderContent({
  isAdmin,
  isOwner,
  orderId,
  orderResponse,
  token,
  refetch,
}: OrderContentProps) {
  console.log(orderResponse);
  const [showConfetti, setShowConfetti] = useState(false);
  const pathname = usePathname();
  const router = useRouter();
  const payed = useSearchParams().get("payed");
  const { messages, error } = useFetchStream<
    PageableResponse<CustomEntityModel<TrainingResponse>>
  >({
    path: "/trainings/byIds",
    method: "PATCH",
    authToken: true,
    body: {
      page: 0,
      size: orderResponse.trainings.length,
    },
    arrayQueryParam: { ids: orderResponse.trainings.map(String) },
  });

  console.log(messages);
  const payCallback = useCallback(() => {
    refetch();
    router.push(pathname + "?payed=true");
  }, [router, pathname, refetch]);

  const trainings = useMemo(
    () => messages?.map(({ content: { content } }) => content) || [],
    [messages],
  );
  const totalPrice = useMemo(
    () => trainings?.reduce((acc, { price }) => acc + price, 0) || 0,
    [trainings],
  );

  useEffect(() => {
    if (payed === "true") {
      setShowConfetti(true);
      setTimeout(() => {
        setShowConfetti(false);
      }, 6500);
    }
  }, [payed, refetch]);

  console.log(trainings);

  if (trainings.length === 0) {
    return null;
  }

  if (error?.status) {
    notFound();
  }

  return (
    <>
      {showConfetti && (
        <Confetti
          colors={["#fff"]}
          className="mx-auto w-[90vw] h-[100vh] invert dark:invert-0"
          recycle={false}
        />
      )}
      <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14 max-w-[900px] mx-auto ">
        <div className="grid md:grid-cols-2 gap-12 w-full">
          <div className=" space-y-4 w-full">
            {trainings.map(({ id, title, images, price, exercises }, index) => (
              <div
                key={id}
                className="border rounded-lg px-2 py-4 flex items-center justify-between shadow-lg"
              >
                <Image
                  src={images[0] || noImg}
                  width={150}
                  height={150}
                  className="rounded-lg overflow-hidden w-36 h-36 object-cover"
                  alt={title}
                />
                <div className="ml-12 flex-1 space-y-2">
                  <Link
                    href={`/trainings/single/${id}?exercises=${exercises.join(
                      ",",
                    )}`}
                    className="text-2xl tracking-tighter hover:underline"
                  >
                    {title}
                  </Link>
                  <p className="text-lg tracking-tight font-bold">${price}</p>
                </div>
              </div>
            ))}
          </div>
          <div className="col-span-1 md:sticky top-20 right-0 self-start w-full">
            <div className="grid items-start gap-4 md:gap-8 w-full">
              <Card className="p-4 w-full py-4">
                <div className="grid items-start gap-2 w-full">
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="flex items-center gap-2">
                        <h3 className="font-semibold text-sm">Subtotal</h3>
                      </div>
                      <div className="flex items-center gap-2">
                        <h3 className="font-semibold text-lg">
                          ${roundToDecimalPlaces(totalPrice, 2)}
                        </h3>
                      </div>
                    </div>
                    <Badge
                      className="w-30 h-15 text-lg text-center"
                      variant={orderResponse.payed ? "default" : "destructive"}
                    >
                      {orderResponse.payed ? "Payed" : "Not Payed"}
                    </Badge>
                  </div>
                  {isOwner && !orderResponse.payed && (
                    <div className=" mt-6 w-full">
                      <PayDrawer
                        orderId={orderResponse.id}
                        token={token}
                        totalPrice={totalPrice}
                        callback={payCallback}
                      />
                    </div>
                  )}
                </div>
              </Card>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
