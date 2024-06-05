import { SortDirection } from "@/types/fetch-utils";
import { useCallback, useEffect, useMemo, useState } from "react";
import { TableFilter } from "@/components/data-table/data-table";
import { CustomEntityModel, PageableResponse, PageInfo } from "@/types/dto";
import useFetchStream, { UseFetchStreamProps } from "@/hoooks/useFetchStream";
import { BaseError } from "@/types/responses";
import {
  makeSortFetchParams,
  makeSortString,
  parseQueryParamAsInt,
} from "@/lib/utils";
import { usePathname, useRouter, useSearchParams } from "next/navigation";

interface UseTableParams<DataType, E extends BaseError = BaseError> {
  sortKeys: string[];
  path: string;
  filterKey?: string;
  filterPlaceholder?: string;
  fetchProps?: UseFetchStreamProps;
  arrayQueryParam?: object;
}

export function useTable<DataType, E extends BaseError>({
  sortKeys,
  path,
  filterKey = "title",
  filterPlaceholder = "Search by title",
  fetchProps,
  arrayQueryParam = {},
}: UseTableParams<DataType, E>) {
  const pathname = usePathname();
  const router = useRouter();
  const currentSearchParams = useSearchParams();

  const [filter, setFilter] = useState<TableFilter>({
    key: filterKey,
    value: currentSearchParams.get(filterKey) || "",
    placeholder: filterPlaceholder,
  });

  const isValidSortDirection = useCallback(
    (direction: string): direction is SortDirection => {
      return ["asc", "desc", "none"].includes(direction);
    },
    [],
  );

  const [sort, setSort] = useState<
    Record<(typeof sortKeys)[number], SortDirection>
  >(
    sortKeys.reduce(
      (acc, key) => ({
        ...acc,
        [key]: isValidSortDirection(currentSearchParams.get(key) || "")
          ? (currentSearchParams.get(key) as SortDirection)
          : "none",
      }),
      {} as Record<(typeof sortKeys)[number], SortDirection>,
    ),
  );
  console.log({ sort });
  const [pageInfo, setPageInfo] = useState<PageInfo>({
    currentPage: parseQueryParamAsInt(
      currentSearchParams.get("currentPage"),
      0,
    ),
    totalPages: 1,
    totalElements: 10,
    pageSize: parseQueryParamAsInt(currentSearchParams.get("pageSize"), 10),
  });

  const fetchParams = useMemo(
    () =>
      fetchProps || {
        path,
        method: "PATCH",
        authToken: true,
        body: {
          page: pageInfo.currentPage,
          size: pageInfo.pageSize,
          sortingCriteria: makeSortFetchParams(sort),
        },
        queryParams: { [filter.key]: filter.value },
        arrayQueryParam,
      },
    [
      arrayQueryParam,
      fetchProps,
      filter.key,
      filter.value,
      pageInfo.currentPage,
      pageInfo.pageSize,
      path,
      sort,
    ],
  );

  const [data, setData] = useState<DataType[]>();
  const { messages, error, isFinished } = useFetchStream<
    PageableResponse<CustomEntityModel<DataType>>,
    BaseError
  >(fetchParams as UseFetchStreamProps);

  console.log({ messages, error, isFinished });

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
    if (messages && messages.length > 0) {
      setData(messages.map(({ content }) => content.content));
    }
  }, [JSON.stringify(messages)]);

  useEffect(() => {
    setPageInfo((prev) => ({
      ...prev,
      currentPage: 0,
    }));
  }, [filter.value, JSON.stringify(sort)]);

  useEffect(() => {
    const updatedSearchParams = new URLSearchParams(
      currentSearchParams.toString(),
    );

    updatedSearchParams.set("currentPage", pageInfo.currentPage.toString());
    updatedSearchParams.set("pageSize", pageInfo.pageSize.toString());
    updatedSearchParams.set("sort", makeSortString(makeSortFetchParams(sort)));
    updatedSearchParams.set(filter.key, filter.value);

    const newSearchString = updatedSearchParams.toString();

    if (newSearchString !== currentSearchParams.toString()) {
      router.replace(`${pathname}?${newSearchString}`, { scroll: false });
    }
  }, [
    currentSearchParams,
    filter.key,
    filter.value,
    pageInfo.currentPage,
    pageInfo.pageSize,
    pathname,
    router,
    sort,
  ]);
  console.log({ fetchParams });

  return {
    filter,
    setFilter,
    sort,
    setSort,
    pageInfo,
    setPageInfo,
    data,
    setData,
    error,
    messages,
    isFinished,
  };
}
