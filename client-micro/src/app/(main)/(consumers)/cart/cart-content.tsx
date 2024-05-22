"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardTitle } from "@/components/ui/card";
import { useCartForUser } from "@/context/cart-context";
import { Session } from "next-auth";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import noImg from "../../../../../public/noImage.jpg";
import Link from "next/link";
import { Trash2 } from "lucide-react";
import { useToast } from "@/components/ui/use-toast";
import { ToastAction } from "@/components/ui/toast";
import CheckoutDrawer from "./checkout-drawer";

interface Props {
  user: NonNullable<Session["user"]>;
}

export default function CartContent({ user }: Props) {
  const {
    usersCart,
    usersCartTotalPrice,
    removeFromCartForUser,
    clearCartForUser,
    addToCartForUser,
  } = useCartForUser(user.id);
  const router = useRouter();

  const [isMounted, setIsMounted] = useState(false);

  const { toast } = useToast();

  useEffect(() => {
    setIsMounted(true);
  }, []);

  if (!isMounted) return null;

  if (usersCart.total === 0) {
    return (
      <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 pb-14 ">
        <h1 className="text-4xl tracking-tighter font-bold text-center mt-10">
          Your cart is empty
        </h1>
        <div className="w-full mx-auto flex items-center justify-center mt-10">
          <Button
            className="text-lg"
            onClick={() => router.push("/trainings/approved")}
          >
            See the trainigs!
          </Button>
        </div>
      </section>
    );
  }

  return (
    <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14 max-w-[1000px] mx-auto ">
      <div className="grid md:grid-cols-3 gap-12 w-full">
        <div className="md:col-span-2 space-y-4 w-full">
          {usersCart.trainings.map(
            ({ id, title, images, price, exercises }, index) => (
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
                <div>
                  <Button
                    variant="destructive"
                    onClick={() => removeFromCartForUser({ id })}
                  >
                    <Trash2 />
                  </Button>
                </div>
              </div>
            ),
          )}
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
                        ${usersCartTotalPrice}
                      </h3>
                    </div>
                  </div>
                  <Button
                    variant="destructive"
                    onClick={() => {
                      const old = usersCart.trainings;
                      clearCartForUser();
                      toast({
                        title: "Removed",
                        description: "All items removed from cart!",
                        variant: "destructive",
                        action: (
                          <ToastAction
                            altText="Undo"
                            onClick={() => old.forEach(addToCartForUser)}
                          >
                            Undo
                          </ToastAction>
                        ),
                      });
                    }}
                  >
                    <Trash2 /> Clear All
                  </Button>
                </div>
                <div className=" mt-6 w-full">
                  <CheckoutDrawer
                    userId={user.id}
                    totalPrice={usersCartTotalPrice}
                    trainings={usersCart.trainings}
                    token={user.token}
                    clearCartForUser={clearCartForUser}
                  />
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </section>
  );
}
