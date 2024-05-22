"use client";
import {
  CustomEntityModel,
  PageInfo,
  PageableResponse,
  UserDto,
  ExerciseResponse,
} from "@/types/dto";
import { DataTable, Pagination } from "@/components/data-table/data-table";
import { Suspense, useEffect, useMemo, useState } from "react";

import { Badge } from "@/components/ui/badge";
import { ColumnDef, Sorting } from "@tanstack/react-table";
import { ArrowUpDown, MoreHorizontal } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { AuthProvider, Role, SortDirection } from "@/types/fetch-utils";
import useFetchStream from "@/hoooks/useFetchStream";
import { BaseError } from "@/types/responses";
import Loader from "@/components/ui/spinner";
import { TableFilter } from "@/components/data-table/data-table";
import { useRouter } from "next/navigation";
import { makeSortFetchParams } from "@/lib/utils";
import SortingButton from "@/components/common/sorting-button";
import Link from "next/link";
import { useTable } from "@/hoooks/useTable";

export default function Page() {
  const router = useRouter();
  const [role, setRole] = useState<Role | "">("");
  const [provider, setProvider] = useState<AuthProvider | "">("");
  // const [filter, setFilter] = useState<TableFilter>({
  //   key: "email",
  //   value: "",
  //   placeholder: "Search by email",
  // });
  // const [role, setRole] = useState<Role | "">("");
  // // params  email:value
  // const [sort, setSort] = useState<
  //   Record<"email" | "firstName" | "lastName", SortDirection>
  // >({ email: "none", firstName: "none", lastName: "none" });
  //
  // const [pageInfo, setPageInfo] = useState<PageInfo>({
  //   currentPage: 0,
  //   totalPages: 1,
  //   totalElements: 10,
  //   pageSize: 10,
  // });
  //
  // const { messages, error } = useFetchStream<
  //   PageableResponse<CustomEntityModel<UserDto>>,
  //   BaseError
  // >({
  //   path: "/users",
  //   method: "PATCH",
  //   authToken: true,
  //   body: {
  //     page: pageInfo.currentPage,
  //     size: pageInfo.pageSize,
  //     sortingCriteria: makeSortFetchParams(sort),
  //   },
  //   queryParams: { email: filter.value },
  //   arrayQueryParam: { roles: [role] },
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
  const {
    sort,
    setSort,
    data,
    setData,
    pageInfo,
    setPageInfo,
    filter,
    setFilter,
    messages,
  } = useTable<UserDto, BaseError>({
    sortKeys: ["email", "firstName", "lastName"],
    path: "/users",
    arrayQueryParam: { roles: [role], providers: [provider] },
    filterKey: "email",
    filterPlaceholder: "Search by email",
  });
  const columns: ColumnDef<UserDto>[] = useMemo(
    () => [
      {
        accessorKey: "email",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"email"} />
        ),
        cell: ({ row }) => <span>{row.getValue("email")}</span>,
      },
      {
        accessorKey: "firstName",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"firstName"} />
        ),
      },
      {
        accessorKey: "lastName",
        header: () => (
          <SortingButton sort={sort} setSort={setSort} field={"lastName"} />
        ),
      },
      {
        accessorKey: "role",
        header: () => (
          <Button
            variant="outline"
            className="w-28"
            onClick={() => {
              setRole((prev) =>
                prev === ""
                  ? "ROLE_ADMIN"
                  : prev === "ROLE_ADMIN"
                    ? "ROLE_TRAINER"
                    : prev === "ROLE_TRAINER"
                      ? "ROLE_USER"
                      : "",
              );
            }}
          >
            {role === ""
              ? "All Roles"
              : role === "ROLE_ADMIN"
                ? "Admins"
                : role === "ROLE_TRAINER"
                  ? "Trainers"
                  : "Users"}
          </Button>
        ),
        cell: ({ row }) => (
          <Badge
            variant={
              row.getValue("role") === "ROLE_ADMIN"
                ? "destructive"
                : row.getValue("role") === "ROLE_TRAINER"
                  ? "default"
                  : "secondary"
            }
          >
            {row.getValue("role")}
          </Badge>
        ),
      },
      {
        accessorKey: "provider",
        header: () => (
          <Button
            variant="outline"
            className="w-28"
            onClick={() => {
              setProvider((prev) =>
                prev === ""
                  ? "LOCAL"
                  : prev === "LOCAL"
                    ? "GOOGLE"
                    : prev === "GOOGLE"
                      ? "GITHUB"
                      : "",
              );
            }}
          >
            {provider === ""
              ? "All Providers"
              : provider === "LOCAL"
                ? "Local"
                : provider === "GITHUB"
                  ? "Github"
                  : "Google"}
          </Button>
        ),
        cell: ({ row }) => (
          <p className="font-bold ">{row.getValue("provider")}</p>
        ),
      },
      {
        id: "actions",
        cell: ({ row }) => {
          const user = row.original;

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
                <DropdownMenuItem
                  className="cursor-pointer"
                  onClick={() => navigator.clipboard.writeText(user.email)}
                >
                  Copy User Email
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  className="cursor-pointer"
                  onClick={() => router.push(`/users/${user.id}`)}
                >
                  View customer
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild className="cursor-pointer">
                  <Link
                    href={`/admin/users/${user.id}/orders`}
                    className="cursor-pointer"
                  >
                    View orders
                  </Link>
                </DropdownMenuItem>
                {user.role === "ROLE_TRAINER" && (
                  <>
                    <DropdownMenuSeparator />

                    <DropdownMenuItem asChild className="cursor-pointer">
                      <Link
                        href={`/admin/users/${user.id}/posts`}
                        className="cursor-pointer"
                      >
                        View posts
                      </Link>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem asChild className="cursor-pointer">
                      <Link
                        href={`/admin/users/${user.id}/trainings`}
                        className="cursor-pointer"
                      >
                        View trainings
                      </Link>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem asChild className="cursor-pointer">
                      <Link
                        href={`/admin/users/${user.id}/exercises`}
                        className="cursor-pointer"
                      >
                        View exercises
                      </Link>
                    </DropdownMenuItem>
                  </>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          );
        },
      },
    ],
    [sort, setSort, role, provider, router],
  );

  console.log({ provider });
  if (!messages || !data) return null;
  return (
    <div className="px-6 pb-10">
      <h1 className="text-4xl tracking-tighter font-bold text-center mt-8">
        Users
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
