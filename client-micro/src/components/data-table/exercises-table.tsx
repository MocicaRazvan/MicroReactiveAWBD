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
  ExerciseResponse,
  PageInfo,
  PageableResponse,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown, MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Suspense, useEffect, useMemo, useState } from "react";
import { useSession } from "next-auth/react";
import AlertDialogApproveExercise from "@/components/dialogs/exercises/approve-exercise";
import SortingButton from "@/components/common/sorting-button";
import { SortDirection } from "@/types/fetch-utils";
import { makeSortFetchParams } from "@/lib/utils";
import { ExtraTableProps } from "@/types/tables";
import { format, parseISO } from "date-fns";
import { useTable } from "@/hoooks/useTable";

type Props = ExtraTableProps;

export default function ExercisesTable({ path, title, forWhom }: Props) {
  const router = useRouter();
  const session = useSession();
  // const [filter, setFilter] = useState<TableFilter>({
  //   key: "title",
  //   value: "",
  //   placeholder: "Search by title",
  // });
  // const [sort, setSort] = useState<
  //   Record<"title" | "id" | "createdAt", SortDirection>
  // >({
  //   title: "none",
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
  //
  // const [data, setData] = useState<ExerciseResponse[]>();
  // const { messages, error } = useFetchStream<
  //   PageableResponse<CustomEntityModel<ExerciseResponse>>,
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
  //   queryParams: { title: filter.value },
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

  const {
    sort,
    setSort,
    data,
    setData,
    pageInfo,
    setPageInfo,
    filter,
    setFilter,
  } = useTable<ExerciseResponse, BaseError>({
    sortKeys: ["title", "id", "createdAt"],
    path,
  });

  const columns: ColumnDef<ExerciseResponse>[] = useMemo(() => {
    const baseColumns: ColumnDef<ExerciseResponse>[] = [
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
                {!(forWhom === "trainer") && (
                  <DropdownMenuItem asChild>
                    <Link href={`/users/${row.original.userId}`}>
                      View owner
                    </Link>
                  </DropdownMenuItem>
                )}
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  className="cursor-pointer"
                  onClick={() =>
                    router.push(`/exercises/single/${row.original.id}`)
                  }
                >
                  View exercise
                </DropdownMenuItem>
                {forWhom === "trainer" && (
                  <>
                    <DropdownMenuItem asChild>
                      <Link
                        href={`/exercises/single/${row.original.id}/update/`}
                        className={"cursor-pointer"}
                      >
                        Update exercise
                      </Link>
                    </DropdownMenuItem>

                    <DropdownMenuSeparator />
                  </>
                )}
                {session?.data?.user?.role === "ROLE_ADMIN" &&
                  session?.data?.user?.token &&
                  !row.original.approved && (
                    <DropdownMenuItem
                      asChild
                      onClick={(e) => {
                        e.stopPropagation();
                      }}
                      className="mt-5 py-2"
                    >
                      <AlertDialogApproveExercise
                        exercise={row.original}
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
    router,
    session?.data?.user?.role,
    session?.data?.user?.token,
    sort,
  ]);

  return (
    <div className="px-6 pb-10">
      <h1 className="text-4xl tracking-tighter font-bold text-center mt-8">
        {title}
      </h1>
      <Suspense fallback={<Loader className="w-full" />}>
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
