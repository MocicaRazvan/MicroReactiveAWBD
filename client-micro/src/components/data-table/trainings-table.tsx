"use client";
import { DataTable, TableFilter } from "@/components/data-table/data-table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  PageInfo,
  PageableResponse,
  PostResponse,
  TrainingResponse,
  ExerciseResponse,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown, MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { Suspense, useEffect, useMemo, useState } from "react";
import { useSession } from "next-auth/react";
import AlertDialogApprovePost from "@/components/dialogs/posts/approve-post";
import SortingButton from "@/components/common/sorting-button";
import { SortDirection } from "@/types/fetch-utils";
import { makeSortFetchParams } from "@/lib/utils";
import AlertDialogApproveTraining from "@/components/dialogs/trainings/approve-training";
import { ExtraTableProps } from "@/types/tables";
import { format, parseISO } from "date-fns";
import { useTable } from "@/hoooks/useTable";

type Props = ExtraTableProps;

export default function TrainingsTable({ path, title, forWhom }: Props) {
  const router = useRouter();
  const session = useSession();
  const isAdmin = session?.data?.user?.role === "ROLE_ADMIN";

  const {
    sort,
    setSort,
    data,
    setData,
    pageInfo,
    setPageInfo,
    filter,
    setFilter,
  } = useTable<TrainingResponse, BaseError>({
    sortKeys: ["title", "id", "createdAt"],
    path,
  });

  console.log(data);

  const columns: ColumnDef<TrainingResponse>[] = useMemo(() => {
    const baseColumns: ColumnDef<TrainingResponse>[] = [
      {
        accessorKey: "id",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"id"} />
        ),
      },
      {
        accessorKey: "title",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"title"} />
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
        accessorKey: "UserLikes",
        header: () => <div className="text-left">#UserLikes</div>,
        cell: ({ row }) => <p>{row.original.userLikes.length}</p>,
      },
      {
        accessorKey: "UserDislikes",
        header: () => <div className="text-left">#UserDislikes</div>,
        cell: ({ row }) => <p>{row.original.userDislikes.length}</p>,
      },

      {
        accessorKey: "approved",
        header: () => <div className="text-left">Approved</div>,
        cell: ({ row }) => (
          <Badge variant={row.original.approved ? "default" : "destructive"}>
            {row.original.approved ? "Yes" : "No"}
          </Badge>
        ),
      },
      {
        accessorKey: "price",
        header: () => <div className="text-left">Price</div>,
        cell: ({ row }) => <p className="font-bold">{row.original.price}</p>,
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
                    router.push(
                      `/trainings/single/${row.original.id}?exercises=` +
                        row.original.exercises.join(`,`),
                    )
                  }
                >
                  View trainings
                </DropdownMenuItem>
                {!(forWhom === "trainer") && (
                  <DropdownMenuItem asChild>
                    <Link href={`/users/${row.original.userId}`}>
                      View owner
                    </Link>
                  </DropdownMenuItem>
                )}
                {forWhom === "trainer" && (
                  <DropdownMenuItem asChild>
                    <Link
                      href={
                        `/trainings/single/${row.original.id}/update/?exercises=` +
                        row.original.exercises.join(`,`)
                      }
                    >
                      Update Training
                    </Link>
                  </DropdownMenuItem>
                )}

                <div className="h-1" />
                {isAdmin &&
                  session?.data?.user?.token &&
                  !row.original.approved && (
                    <DropdownMenuItem
                      asChild
                      onClick={(e) => {
                        e.stopPropagation();
                      }}
                      className="mt-5 py-2"
                    >
                      <AlertDialogApproveTraining
                        training={row.original}
                        token={session?.data?.user?.token}
                        callBack={() => {
                          setData((prev) =>
                            !prev
                              ? prev
                              : prev.map((p) =>
                                  p.id === row.original.id
                                    ? { ...p, approved: true }
                                    : p,
                                ),
                          );
                        }}
                      />
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
  }, [
    forWhom,
    isAdmin,
    router,
    session?.data?.user?.token,
    setData,
    setSort,
    sort,
  ]);

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
          filter={filter}
          setFilter={setFilter}
        />
      </Suspense>
    </div>
  );
}
