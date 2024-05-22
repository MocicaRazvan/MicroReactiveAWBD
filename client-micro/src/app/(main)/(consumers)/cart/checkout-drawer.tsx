"use client";

import { Button } from "@/components/ui/button";

import {
  Drawer,
  DrawerTrigger,
  DrawerContent,
  DrawerTitle,
  DrawerHeader,
  DrawerDescription,
} from "@/components/ui/drawer";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { fetchStream } from "@/hoooks/fetchStream";
import {
  CustomEntityModel,
  OrderResponse,
  TrainingResponse,
} from "@/types/dto";
import { CheckoutType, createCheckoutSchema } from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { useForm } from "react-hook-form";
import { IdType } from "@/context/cart-context";
import { toast } from "@/components/ui/use-toast";
import { ToastAction } from "@/components/ui/toast";

interface Props {
  userId: string;
  totalPrice: number;
  trainings: TrainingResponse[];
  token: string;
  clearCartForUser: () => void;
}

export default function CheckoutDrawer({
  userId,
  totalPrice,
  trainings,
  token,
  clearCartForUser,
}: Props) {
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const form = useForm<CheckoutType>({
    resolver: zodResolver(createCheckoutSchema(totalPrice)),
    defaultValues: {
      userConfirmedPrice: 0,
      billingAddress: "",
    },
  });

  const onSubmit = useCallback(
    async ({ billingAddress }: CheckoutType) => {
      setIsLoading(true);

      try {
        const { messages, error } = await fetchStream<
          CustomEntityModel<OrderResponse>
        >({
          path: "/orders",
          method: "POST",
          token,
          body: {
            shippingAddress: billingAddress,
            payed: false,
            trainings: trainings.map(({ id }) => id),
          },
        });
        if (error?.message || error?.status) {
          setErrorMsg("Something went wrong, please try again.");
        } else {
          clearCartForUser();
          toast({
            title: "Order",
            description: "Order has been placed successfully",
            variant: "success",
            action: (
              <ToastAction
                altText={"View Order"}
                onClick={() =>
                  router.push(`/orders/single/${messages[0].content.id}`)
                }
              >
                View Order
              </ToastAction>
            ),
          });
          router.push(`/users/${userId}/orders`);
        }
      } catch (error) {
        console.log(error);
      } finally {
        setIsLoading(false);
      }
    },
    [router, token, trainings, userId],
  );

  return (
    <Drawer open={isDrawerOpen}>
      <DrawerTrigger asChild>
        <Button
          className="w-full"
          size="lg"
          onClick={() => setIsDrawerOpen(true)}
        >
          Proceed to checkout
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-sm pb-10">
          <DrawerHeader>
            <DrawerTitle>Checkout</DrawerTitle>
            <DrawerDescription>
              Please confirm your order and proceed to payment.
            </DrawerDescription>
          </DrawerHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="userConfirmedPrice"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      Confirm Price:
                      <span className=" ml-2 font-bold">{totalPrice}</span>
                    </FormLabel>
                    <FormControl>
                      <Input placeholder="0" {...field} type="number" />
                    </FormControl>
                    <FormDescription>
                      Please confirm the price of your order, to submit.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="billingAddress"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Billing Address</FormLabel>
                    <FormControl>
                      <Input placeholder="Str. Marcel Nr.12" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="flex items-center justify-center mt-4 flex-col gap-4">
                {errorMsg && (
                  <p className="font-medium text-destructive">{errorMsg}</p>
                )}
                {!isLoading ? (
                  <Button type="submit">Place Order</Button>
                ) : (
                  <Button disabled>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Please wait
                  </Button>
                )}
              </div>
            </form>
          </Form>
        </div>
      </DrawerContent>
    </Drawer>
  );
}
