"use client";

import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
  VisibilityState,
  ColumnFiltersState,
} from "@tanstack/react-table";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

import { Button } from "@/components/ui/button";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { PageInfo } from "@/types/dto";

import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { DataTablePagination } from "./data-table-pagination";
import { Input } from "../ui/input";

export interface Pagination {
  pageIndex: number;
  pageSize: number;
}
export interface TableFilter {
  key: string;
  value: string;
  placeholder: string;
}

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];

  pageInfo: PageInfo;
  setPageInfo: Dispatch<SetStateAction<PageInfo>>;
  filter?: TableFilter;
  setFilter?: Dispatch<SetStateAction<TableFilter>>;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  pageInfo,
  setPageInfo,
  filter,
  setFilter,
}: DataTableProps<TData, TValue>) {
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);

  useEffect(() => {
    if (
      filter?.value !== undefined &&
      setFilter &&
      columnFilters[0]?.value &&
      filter?.value !== columnFilters[0]?.value
    ) {
      setFilter((prev) => ({
        ...prev,
        value: columnFilters[0].value as string,
      }));
    }
    if (columnFilters.length === 0 && filter?.value && setFilter) {
      setFilter((prev) => ({
        ...prev,
        value: "",
      }));
    }
  }, [columnFilters, filter?.value, setFilter]);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    // getPaginationRowModel: getPaginationRowModel(),
    manualPagination: true,
    autoResetPageIndex: true,
    manualFiltering: true,
    enableRowSelection: false,
    enableMultiRowSelection: false,
    onColumnVisibilityChange: setColumnVisibility,
    onColumnFiltersChange: setColumnFilters,
    state: {
      columnVisibility,
      columnFilters,
    },
  });
  return (
    <div>
      <div className="flex items-center py-4">
        {filter && setFilter && (
          <Input
            placeholder={filter.placeholder}
            value={
              (table.getColumn(filter.key)?.getFilterValue() as string) ?? ""
              // filter.value
            }
            onChange={(event) =>
              table.getColumn(filter.key)?.setFilterValue(event.target.value)
            }
            className="max-w-sm"
          />
        )}

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" className="ml-auto">
              Columns
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            {table
              .getAllColumns()
              .filter((column) => column.getCanHide())
              .map((column) => {
                return (
                  <DropdownMenuCheckboxItem
                    key={column.id}
                    className="capitalize"
                    checked={column.getIsVisible()}
                    onCheckedChange={(value) =>
                      column.toggleVisibility(!!value)
                    }
                  >
                    {column.id}
                  </DropdownMenuCheckboxItem>
                );
              })}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && "selected"}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      {/* <div className="flex items-center justify-end space-x-2 py-4">
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          Previous
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
        >
          Next
        </Button>
      </div> */}
      <DataTablePagination
        // table={table}
        // totalPages={totalPages}
        // totalElements={totalElements}
        // setPagination={setPagination}
        // pagination={pagination}
        pageInfo={pageInfo}
        setPageInfo={setPageInfo}
      />
    </div>
  );
}
