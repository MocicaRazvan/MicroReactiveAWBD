"use client";

import { useCartForUser } from "@/context/cart-context";
import { Button } from "../ui/button";
import { ShoppingCartIcon, Trash2 } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import { TrainingResponse } from "@/types/dto";
import { Fragment } from "react";
import { ScrollArea } from "../ui/scroll-area";
import { cn } from "@/lib/utils";
import { useRouter } from "next/navigation";
import { useToast } from "../ui/use-toast";
import { ToastAction } from "../ui/toast";
import Link from "next/link";

interface Props {
  userId: string;
}

export default function CartPop({ userId }: Props) {
  const {
    usersCart,
    removeFromCartForUser,
    usersCartTotalPrice,
    addToCartForUser,
  } = useCartForUser(userId);
  const router = useRouter();
  const { toast } = useToast();

  return (
    <DropdownMenu modal={false}>
      <DropdownMenuTrigger asChild>
        <div className="relative">
          <Button variant="outline" disabled={usersCart.total === 0}>
            <ShoppingCartIcon />
          </Button>
          {usersCart.total > 0 && (
            <div className="absolute top-[-2px] right-[-10px] rounded-full w-7 h-7 bg-destructive flex items-center justify-center">
              <p>{usersCart.total}</p>
            </div>
          )}
        </div>
      </DropdownMenuTrigger>
      {usersCart.total > 0 && (
        <DropdownMenuContent className="w-52">
          <DropdownMenuGroup>
            <ScrollArea
              className={cn(
                "w-full px-1",
                usersCart.total < 5
                  ? `h-[calc(${usersCart.total}rem+3.5rem)]`
                  : "h-60",
              )}
            >
              {usersCart.trainings.map((training, index) => (
                <Fragment key={training.id + index}>
                  <DropdownMenuItem onClick={(e) => e.preventDefault()}>
                    <div className="py-1 h-10 flex items-center justify-between w-full">
                      <div>
                        <Link
                          href={`/trainings/single/${
                            training.id
                          }?exercises=${training.exercises.join(`,`)}`}
                          className="hover:underline"
                        >
                          <p className="text-lg mb-1">
                            {training.title.length > 6
                              ? training.title.substring(0, 6) + "..."
                              : training.title}
                          </p>
                        </Link>
                        <p className="font-bold">${training.price}</p>
                      </div>
                      <Button
                        variant="destructive"
                        className="py-1 px-2"
                        onClick={() => {
                          removeFromCartForUser({ id: training.id });
                          toast({
                            title: training.title,
                            description: "Removed From Cart",
                            variant: "destructive",
                            action: (
                              <ToastAction
                                altText="Undo"
                                onClick={() => addToCartForUser(training)}
                              >
                                Undo
                              </ToastAction>
                            ),
                          });
                        }}
                      >
                        <Trash2 className="w-4 h-16" />
                      </Button>
                    </div>
                  </DropdownMenuItem>

                  <DropdownMenuSeparator />
                </Fragment>
              ))}
              <div className="h-12 mt-2 px-1 py-2 flex items-center justify-between">
                <p className="font-bold text-center">
                  Total: <span className="ml-1">${usersCartTotalPrice}</span>
                </p>
                <Button
                  variant="default"
                  className="py-1 px-2"
                  onClick={() => {
                    router.push("/cart");
                  }}
                >
                  Order
                </Button>
              </div>
            </ScrollArea>
          </DropdownMenuGroup>
        </DropdownMenuContent>
      )}
    </DropdownMenu>
  );
}
