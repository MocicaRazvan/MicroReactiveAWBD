"use client";

import { DataTable } from "@/components/data-table/data-table";
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
import { PostResponse } from "@/types/dto";
import { BaseError } from "@/types/responses";
import { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { Suspense, useMemo } from "react";
import { useSession } from "next-auth/react";
import AlertDialogApprovePost from "@/components/dialogs/posts/approve-post";
import SortingButton from "@/components/common/sorting-button";
import { ExtraTableProps } from "@/types/tables";
import { format, parseISO } from "date-fns";
import { useTable } from "@/hoooks/useTable";

type Props = ExtraTableProps;

export default function PostsTable({ path, title, forWhom }: Props) {
  const router = useRouter();
  const session = useSession();
  const { userId } = useParams();
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
  } = useTable<PostResponse, BaseError>({
    sortKeys: ["title", "id", "createdAt"],
    path,
  });
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
        accessorKey: "createdAt",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"createdAt"} />
        ),
        cell: ({ row }) => (
          <p>{format(parseISO(row.original.createdAt), "dd/MM/yyyy")}</p>
        ),
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
                    router.push(`/posts/single/${row.original.id}`)
                  }
                >
                  View post
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                {!(forWhom === "trainer") && (
                  <DropdownMenuItem asChild>
                    <Link href={`/users/${row.original.userId}`}>
                      View owner
                    </Link>
                  </DropdownMenuItem>
                )}
                <DropdownMenuItem asChild>
                  <Link
                    className="cursor-pointer"
                    href={`/trainer/user/${row.original.userId}/posts/update/${row.original.id}`}
                  >
                    Update post
                  </Link>
                </DropdownMenuItem>
                {forWhom === "trainer" &&
                  session?.data?.user?.id === String(row.original.userId) && (
                    <DropdownMenuItem asChild>
                      <Link
                        className="cursor-pointer"
                        href={`/trainer/user/${row.original.userId}/posts/update/${row.original.id}`}
                      >
                        Update post
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
                      <AlertDialogApprovePost
                        post={row.original}
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
    ],
    [
      forWhom,
      isAdmin,
      router,
      session?.data?.user?.id,
      session?.data?.user?.token,
      sort,
    ],
  );

  return (
    <div className="px-6 pb-10">
      <h1 className="text-4xl tracking-tighter font-bold text-center mt-8">
        {title}
      </h1>
      <Suspense fallback={<Loader />}>
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
