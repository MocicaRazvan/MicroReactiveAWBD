import OrdersTable from "@/components/data-table/orders-table";
import Loader from "@/components/ui/spinner";
import { Suspense } from "react";

export default function UsersOrders({
  params: { id },
}: {
  params: { id: string };
}) {
  return (
    <Suspense
      fallback={
        <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14 ">
          <Loader className="w-full" />
        </section>
      }
    >
      <div className="w-full">
        <OrdersTable
          path={`/orders/user/${id}`}
          title="Your Orders"
          forWhom="trainer"
        />
      </div>
    </Suspense>
  );
}
