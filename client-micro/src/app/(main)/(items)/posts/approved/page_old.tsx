"use client";

import SortingButton from "@/components/common/sorting-button";
import { DataTable, TableFilter } from "@/components/data-table/data-table";
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
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { SortDirection } from "@/types/fetch-utils";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown, MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { title } from "process";
import { Suspense, useEffect, useMemo, useState } from "react";

export default function ApprovedPosts() {
  const router = useRouter();
  const [filter, setFilter] = useState<TableFilter>({
    key: "title",
    value: "",
    placeholder: "Search by title",
  });
  const [sort, setSort] = useState<Record<"title" | "id", SortDirection>>({
    title: "asc",
    id: "asc",
  });

  const [pageInfo, setPageInfo] = useState<PageInfo>({
    currentPage: 0,
    totalPages: 1,
    totalElements: 10,
    pageSize: 10,
  });

  const { messages, error } = useFetchStream<
    PageableResponse<CustomEntityModel<PostResponse>>,
    BaseError
  >({
    path: "/posts/approved",
    method: "PATCH",
    authToken: true,
    body: {
      page: pageInfo.currentPage,
      size: pageInfo.pageSize,
      sortingCriteria: sort,
    },
    queryParams: { title: filter.value },
  });

  useEffect(() => {
    if (messages && messages.length > 0 && messages[0].pageInfo) {
      setPageInfo((prev) => ({
        ...prev,
        totalPages: messages[0].pageInfo.totalPages,
        totalElements: messages[0].pageInfo.totalElements,
      }));
    }
  }, [JSON.stringify(messages)]);

  const data = useMemo(
    () => messages.map(({ content }) => content.content),
    [messages]
  );

  const columns: ColumnDef<PostResponse>[] = useMemo(
    () => [
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
        accessorKey: "userId",
        header: "UserId",
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
        id: "actions",
        cell: ({ row }) => {
          return (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                  <span className="sr-only">Open menu</span>
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>Actions</DropdownMenuLabel>
                <DropdownMenuItem asChild>
                  <Link href={`/users/${row.original.userId}`}>View owner</Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  className="cursor-pointer"
                  onClick={() =>
                    router.push(`/posts/single/${row.original.id}`)
                  }
                >
                  View post
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          );
        },
      },
    ],
    [router, sort]
  );

  return (
    <div className="px-6 pb-10">
      <h1 className="text-4xl tracking-tighter font-bold text-center mt-8">
        Approved Posts
      </h1>
      <Suspense fallback={<Loader />}>
        <DataTable
          columns={columns}
          data={data}
          pageInfo={pageInfo}
          setPageInfo={setPageInfo}
          filter={filter}
          setFilter={setFilter}
        />
      </Suspense>
    </div>
  );
}
