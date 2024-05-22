"use client";

import GridList from "@/components/common/grid-list";
import { Button } from "@/components/ui/button";
import { ToastAction } from "@/components/ui/toast";
import { useToast } from "@/components/ui/use-toast";
import { useCartForUser } from "@/context/cart-context";
import { TrainingResponse } from "@/types/dto";
import { PlusSquareIcon } from "lucide-react";
import { useRouter } from "next/navigation";
import { useCallback } from "react";

interface Props {
  userId: string;
}

export default function TrainingsList({ userId }: Props) {
  const router = useRouter();
  const { addToCartForUser, isInCartForUser, removeFromCartForUser } =
    useCartForUser(userId);
  const { toast } = useToast();

  const extraContent = useCallback(
    (training: TrainingResponse) => (
      <div className="flex items-center justify-center px-2">
        {!isInCartForUser({ id: training.id }) ? (
          <Button
            onClick={() => {
              addToCartForUser(training);
              toast({
                title: training.title,
                description: "Added to cart",

                action: (
                  <ToastAction
                    altText="Remvoe"
                    onClick={() => removeFromCartForUser({ id: training.id })}
                  >
                    Remove Item
                  </ToastAction>
                ),
              });
            }}
          >
            <PlusSquareIcon className="mr-2" /> Add To Cart
          </Button>
        ) : (
          <Button onClick={() => router.push("/cart")}>
            {" "}
            Finish The Order
          </Button>
        )}
      </div>
    ),
    [isInCartForUser, addToCartForUser, toast, removeFromCartForUser, router],
  );

  const extraHeader = useCallback(
    (item: TrainingResponse) => (
      <span className="font-bold">${item.price}</span>
    ),
    [],
  );
  return (
    <GridList<TrainingResponse>
      onItemClick={({ id, exercises }) => {
        router.push(`/trainings/single/${id}?exercises=` + exercises.join(`,`));
      }}
      sizeOptions={[6, 12, 18]}
      path="/trainings/approved"
      sortingOptions={["title", "createdAt", "price"]}
      passExtraContent={extraContent}
      passExtraHeader={extraHeader}
    />
  );
}
