"use client";

import {
  CustomEntityModel,
  PageInfo,
  PageableResponse,
  TitleBodyUser,
} from "@/types/dto";
import ItemCard from "./single/item-card";
import {
  Dispatch,
  ReactNode,
  SetStateAction,
  useEffect,
  useMemo,
  useState,
} from "react";
import { DataTablePagination } from "../data-table/data-table-pagination";
import {
  cn,
  makeSortFetchParams,
  makeSortString,
  parseSortString,
} from "@/lib/utils";
import { Input } from "../ui/input";
import SortingButton from "./sorting-button";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useDebounce } from "../ui/multiple-selector";
import { SortDirection } from "@/types/fetch-utils";
import useFetchStream from "@/hoooks/useFetchStream";
import Loader from "../ui/spinner";

interface GridListProps<T extends TitleBodyUser> {
  onItemClick: (item: T) => void;
  sortingOptions: string[];
  sizeOptions?: number[];
  path: string;
  passExtraContent?: (item: T) => ReactNode;
  passExtraHeader?: (item: T) => ReactNode;
}

export default function GridList<T extends TitleBodyUser>({
  onItemClick,
  sizeOptions,
  sortingOptions,
  path,
  passExtraContent,
  passExtraHeader,
}: GridListProps<T>) {
  const pathname = usePathname();
  const router = useRouter();
  const currentSearchParams = useSearchParams();
  const titleFilter = currentSearchParams.get("title") || "";
  const currentPage = parseInt(
    currentSearchParams.get("currentPage") || "0",
    10,
  );
  const pageSize = parseInt(currentSearchParams.get("pageSize") || "6", 10);

  const sortString = currentSearchParams.get("sort");
  const sortQ = sortString
    ? parseSortString(sortString, sortingOptions)
    : ({ title: "none", createdAt: "none" } as Record<
        "title" | "createdAt",
        SortDirection
      >);

  const { messages, error, isFinished } = useFetchStream<
    PageableResponse<CustomEntityModel<T>>
  >({
    path,
    method: "PATCH",
    authToken: true,
    body: {
      page: currentPage,
      size: pageSize,
      sortingCriteria: makeSortFetchParams(sortQ),
    },
    queryParams: { title: titleFilter },
  });

  const initialSortString = currentSearchParams.get("sort");
  const initialSort = initialSortString
    ? parseSortString(initialSortString, ["title", "createdAt"])
    : ({ title: "none", createdAt: "none" } as Record<
        "title" | "createdAt",
        SortDirection
      >);

  const [sort, setSort] =
    useState<Record<(typeof sortingOptions)[number], SortDirection>>(
      initialSort,
    );

  const [pageInfo, setPageInfo] = useState<PageInfo>({
    currentPage,
    totalPages: 1,
    totalElements: 6,
    pageSize,
  });

  const [filter, setFilter] = useState<Record<"title", string>>({
    title: titleFilter,
  });
  const debouncedFilter = useDebounce(filter, 500);

  const items = useMemo(
    () => messages?.map((m) => m.content.content) || [],
    [JSON.stringify(messages)],
  );

  useEffect(() => {
    if (messages && messages.length > 0 && messages[0].pageInfo) {
      setPageInfo((prev) => ({
        ...prev,
        totalPages: messages[0].pageInfo.totalPages,
        totalElements: messages[0].pageInfo.totalElements,
      }));
    }
  }, [JSON.stringify(messages)]);

  useEffect(() => {
    const updatedSearchParams = new URLSearchParams(
      currentSearchParams.toString(),
    );
    updatedSearchParams.set("title", debouncedFilter.title);
    updatedSearchParams.set("currentPage", pageInfo.currentPage.toString());
    updatedSearchParams.set("pageSize", pageInfo.pageSize.toString());
    updatedSearchParams.set("sort", makeSortString(makeSortFetchParams(sort)));

    const newSearchString = updatedSearchParams.toString();
    if (newSearchString !== currentSearchParams.toString()) {
      router.replace(`${pathname}?${newSearchString}`, { scroll: false });
    }
  }, [
    debouncedFilter,
    pageInfo.currentPage,
    pageInfo.pageSize,
    router,
    pathname,
    currentSearchParams,
    sort,
  ]);

  useEffect(() => {
    setPageInfo((prev) => ({
      ...prev,
      currentPage: 0,
    }));
  }, [JSON.stringify(sort), JSON.stringify(debouncedFilter)]);

  console.log(isFinished);

  return (
    <div className="w-full ">
      <div className="my-10 w-full flex items-center justify-start flex-wrap gap-10">
        <Input
          className="w-[200px]"
          placeholder="Search..."
          value={filter.title}
          onChange={(e) => setFilter({ title: e.target.value })}
          autoFocus
        />
        <div className="flex items-center justify-center ml-12 gap-2">
          {sortingOptions?.length > 0 &&
            sortingOptions?.map((option) => (
              <div key={option}>
                <SortingButton sort={sort} setSort={setSort} field={option} />
              </div>
            ))}
        </div>
      </div>
      <div className="w-full mt-10">
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 lg:gap-8">
          {items.length === 0 && isFinished && (
            <div className="w-full items-center justify-center md:col-span-2 lg:col-span-3">
              <h2 className="text-4xl tracking-tighter font-bold ">
                No results where found!
              </h2>
            </div>
          )}
          {items.length === 0 && !isFinished && (
            <section className="mt-10 w-full md:col-span-2 lg:col-span-3 ">
              <Loader className="w-full" />
            </section>
          )}
          {items.map((item, i) => (
            <div key={item.title + i + item.body.substring(1)}>
              <ItemCard
                item={item}
                onClick={() => onItemClick(item)}
                generateExtraContent={passExtraContent}
                generateExtraHeader={passExtraHeader}
              />
            </div>
          ))}
          {!isFinished && items.length > 0 && (
            <div className="w-full flex flex-col items-center justify-center">
              <Loader className="w-full" />
              <p>Getting more...</p>
            </div>
          )}
        </div>
        <div className={cn("mt-6", items.length === 0 && "hidden")}>
          <DataTablePagination
            pageInfo={pageInfo}
            setPageInfo={setPageInfo}
            sizeOptions={sizeOptions}
          />
        </div>
      </div>
    </div>
  );
}
