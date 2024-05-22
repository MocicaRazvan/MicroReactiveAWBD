import {
  ChevronLeftIcon,
  ChevronRightIcon,
  DoubleArrowLeftIcon,
  DoubleArrowRightIcon,
} from "@radix-ui/react-icons";
import { Table } from "@tanstack/react-table";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Dispatch, SetStateAction, memo } from "react";
import { PageInfo } from "@/types/dto";
import { isDeepEqual } from "@/lib/utils";

interface DataTablePaginationProps {
  pageInfo: PageInfo;
  setPageInfo: Dispatch<SetStateAction<PageInfo>>;
  sizeOptions?: number[];
}

export const DataTablePagination = memo(function DataTablePagination({
  pageInfo,
  setPageInfo,
  sizeOptions = [5, 10, 20, 30, 40],
}: DataTablePaginationProps) {
  return (
    <div className="flex items-center justify-end px-2 mt-2">
      <div className="flex items-center space-x-6 lg:space-x-8">
        <div className="flex items-center space-x-2">
          <p className="text-sm font-medium">Page Size</p>
          <Select
            value={`${pageInfo.pageSize}`}
            onValueChange={(value) => {
              setPageInfo((prev) => ({
                ...prev,
                pageSize: +value,
                currentPage: 0,
              }));
            }}
          >
            <SelectTrigger className="h-8 w-[70px]">
              <SelectValue placeholder={pageInfo.pageSize} />
            </SelectTrigger>
            <SelectContent side="top">
              {sizeOptions.map((pageSize) => (
                <SelectItem key={pageSize} value={`${pageSize}`}>
                  {pageSize}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="flex w-[100px] items-center justify-center text-sm font-medium">
          Page {pageInfo.currentPage + 1} of {pageInfo.totalPages}
        </div>
        <div className="flex items-center space-x-2">
          <Button
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            onClick={() => setPageInfo((prev) => ({ ...prev, currentPage: 0 }))}
            disabled={pageInfo.currentPage === 0}
          >
            <span className="sr-only">Go to first page</span>
            <DoubleArrowLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() =>
              setPageInfo((prev) => ({
                ...prev,
                currentPage: prev.currentPage - 1,
              }))
            }
            disabled={pageInfo.currentPage === 0}
          >
            <span className="sr-only">Go to previous page</span>
            <ChevronLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() =>
              setPageInfo((prev) => ({
                ...prev,
                currentPage: prev.currentPage + 1,
              }))
            }
            disabled={pageInfo.currentPage === pageInfo.totalPages - 1}
          >
            <span className="sr-only">Go to next page</span>
            <ChevronRightIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            onClick={() =>
              setPageInfo((prev) => ({
                ...prev,
                currentPage: prev.totalPages - 1,
              }))
            }
            disabled={pageInfo.currentPage === pageInfo.totalPages - 1}
          >
            <span className="sr-only">Go to last page</span>
            <DoubleArrowRightIcon className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}, areEqual);

function areEqual(
  prevProps: DataTablePaginationProps,
  nextProps: DataTablePaginationProps,
) {
  return (
    isDeepEqual(prevProps.pageInfo, nextProps.pageInfo) &&
    prevProps.setPageInfo === nextProps.setPageInfo
  );
}
