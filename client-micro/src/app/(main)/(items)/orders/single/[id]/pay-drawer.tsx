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
import {
  CheckoutType,
  ConfirmPriceType,
  createCheckoutSchema,
  createConfirmPriceSchema,
} from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { useForm } from "react-hook-form";
import { roundToDecimalPlaces } from "@/lib/utils";

interface Props {
  totalPrice: number;
  orderId: number;
  token: string;
  callback?: () => void;
}

export default function PayDrawer({
  totalPrice,
  orderId,
  callback,
  token,
}: Props) {
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const form = useForm<ConfirmPriceType>({
    resolver: zodResolver(
      createConfirmPriceSchema(roundToDecimalPlaces(totalPrice, 2)),
    ),
    defaultValues: {
      userConfirmedPrice: 0,
    },
  });

  const onSubmit = useCallback(
    async ({ userConfirmedPrice }: ConfirmPriceType) => {
      setIsLoading(true);

      try {
        if (userConfirmedPrice !== roundToDecimalPlaces(totalPrice, 2)) {
          setErrorMsg("Please confirm the price of your order.");
          setIsLoading(false);
          return;
        }
        const { messages, error } = await fetchStream<
          CustomEntityModel<OrderResponse>
        >({
          path: "/orders/pay/" + orderId,
          method: "PATCH",
          token,
          body: {
            price: userConfirmedPrice,
          },
        });
        console.log(messages, error);
        if (error?.message || error?.status) {
          setErrorMsg("Something went wrong, please try again.");
        } else {
          console.log("here");
          callback?.();
          setIsDrawerOpen(false);
        }
      } catch (error) {
        console.log(error);
      } finally {
        setIsLoading(false);
      }
    },
    [callback, token, orderId],
  );

  return (
    <Drawer open={isDrawerOpen}>
      <DrawerTrigger asChild>
        <Button
          className="w-full"
          size="lg"
          onClick={() => setIsDrawerOpen(true)}
        >
          Proceed to pay the order
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-sm pb-10">
          <DrawerHeader>
            <DrawerTitle>Pay</DrawerTitle>
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
                      <span className=" ml-2 font-bold">
                        {roundToDecimalPlaces(totalPrice, 2)}
                      </span>
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

              <div className="flex items-center justify-center mt-4 flex-col gap-4">
                {errorMsg && (
                  <p className="font-medium text-destructive">{errorMsg}</p>
                )}
                {!isLoading ? (
                  <Button type="submit">Pay Order</Button>
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
