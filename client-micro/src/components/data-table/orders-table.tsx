"use client";

import { DataTable } from "@/components/data-table/data-table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  OrderResponse,
  PageInfo,
  PageableResponse,
  ExerciseResponse,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Suspense, useEffect, useMemo, useState } from "react";
import { useSession } from "next-auth/react";
import SortingButton from "@/components/common/sorting-button";
import { SortDirection } from "@/types/fetch-utils";
import { makeSortFetchParams } from "@/lib/utils";
import { ExtraTableProps } from "@/types/tables";
import { parseISO, format } from "date-fns";
import { useTable } from "@/hoooks/useTable";

type Props = ExtraTableProps;

export default function OrdersTable({ path, title, forWhom }: Props) {
  const router = useRouter();
  const session = useSession();
  const isAdmin = session?.data?.user?.role === "ROLE_ADMIN";

  // const [sort, setSort] = useState<Record<"id" | "createdAt", SortDirection>>({
  //   id: "none",
  //   createdAt: "none",
  // });
  //
  // const [pageInfo, setPageInfo] = useState<PageInfo>({
  //   currentPage: 0,
  //   totalPages: 1,
  //   totalElements: 10,
  //   pageSize: 10,
  // });
  // const [data, setData] = useState<OrderResponse[]>();
  //
  // const { messages, error } = useFetchStream<
  //   PageableResponse<CustomEntityModel<OrderResponse>>,
  //   BaseError
  // >({
  //   path,
  //   method: "PATCH",
  //   authToken: true,
  //   body: {
  //     page: pageInfo.currentPage,
  //     size: pageInfo.pageSize,
  //     sortingCriteria: makeSortFetchParams(sort),
  //   },
  //   cache: "no-cache",
  // });
  //
  // useEffect(() => {
  //   if (messages && messages.length > 0 && messages[0].pageInfo) {
  //     setPageInfo((prev) => ({
  //       ...prev,
  //       totalPages: messages[0].pageInfo.totalPages,
  //       totalElements: messages[0].pageInfo.totalElements,
  //     }));
  //   }
  // }, [JSON.stringify(messages)]);
  //
  // useEffect(() => {
  //   if (messages && messages.length > 0) {
  //     setData(messages.map(({ content }) => content.content));
  //   }
  // }, [JSON.stringify(messages)]);
  //
  // console.log(data);

  const {
    sort,
    setSort,
    data,
    setData,
    pageInfo,
    setPageInfo,
    filter,
    setFilter,
  } = useTable<OrderResponse, BaseError>({
    sortKeys: ["id", "createdAt"],
    path,
  });

  const columns: ColumnDef<OrderResponse>[] = useMemo(() => {
    const baseColumns: ColumnDef<OrderResponse>[] = [
      {
        accessorKey: "id",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"id"} />
        ),
      },
      {
        accessorKey: "createdAt",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"createdAt"} />
        ),
        cell: ({ row }) => (
          <p>{format(parseISO(row.original.createdAt), "dd/MM/yyyy")}</p>
        ),
      },
      {
        accessorKey: "trainings",
        header: () => <div className="text-left">Trainings Number</div>,
        cell: ({ row }) => <p>{row.original.trainings.length}</p>,
      },
      {
        accessorKey: "shippingAddress",
        header: () => <div className="text-left">BillingAddress</div>,
        cell: ({ row }) => <p>{row.original.shippingAddress}</p>,
      },
      {
        accessorKey: "payed",
        header: () => <div className="text-left">Payed</div>,
        cell: ({ row }) => (
          <Badge variant={row.original.payed ? "default" : "destructive"}>
            {row.original.payed ? "Yes" : "No"}
          </Badge>
        ),
      },
      {
        accessorKey: "userId",
        header: "UserId",
        id: "userId",
        cell: ({ row }) => (
          <Link
            className="hover:underline font-bold"
            href={`/users/${row.original.userId}`}
          >
            {row.original.userId}
          </Link>
        ),
      },
      {
        id: "actions",
        cell: ({ row }) => {
          return (
            <DropdownMenu modal>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                  <span className="sr-only">Open menu</span>
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>Actions</DropdownMenuLabel>
                <DropdownMenuItem
                  className="cursor-pointer"
                  onClick={() =>
                    router.push(`/orders/single/${row.original.id}`)
                  }
                >
                  View order
                </DropdownMenuItem>
                {!(forWhom === "trainer") && (
                  <DropdownMenuItem asChild>
                    <Link href={`/users/${row.original.userId}`}>
                      View owner
                    </Link>
                  </DropdownMenuItem>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          );
        },
      },
    ];

    return forWhom === "trainer"
      ? baseColumns.filter((column) => column.id !== "userId")
      : baseColumns;
  }, [forWhom, router, sort]);

  return (
    <div className="px-6 pb-10">
      <h1 className="text-4xl tracking-tighter font-bold text-center mt-8">
        {title}
      </h1>
      <Suspense
        fallback={
          <div className=" w-full h-full flex items-center justify-center">
            <Loader className="w-full" />
          </div>
        }
      >
        <DataTable
          columns={columns}
          data={data || []}
          pageInfo={pageInfo}
          setPageInfo={setPageInfo}
        />
      </Suspense>
    </div>
  );
}
