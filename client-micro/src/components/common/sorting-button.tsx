"use client";

import { Button } from "@/components/ui/button";
import { SortDirection } from "@/types/fetch-utils";
import { ArrowDown, ArrowUp } from "lucide-react";
import { Dispatch, SetStateAction, useCallback } from "react";

interface Props<T extends string | number | symbol> {
  sort: Record<T, SortDirection>;
  setSort: Dispatch<SetStateAction<Record<T, SortDirection>>>;
  field: T;
}
export default function SortingButton<T extends string | number | symbol>({
  sort,
  setSort,
  field,
}: Props<T>) {
  const handleSortChange = useCallback(() => {
    setSort((prev) => ({
      ...prev,
      [field]:
        prev[field] === "asc"
          ? "desc"
          : prev[field] === "desc"
          ? "none"
          : "asc",
    }));
  }, [setSort, field]);

  return (
    <Button onClick={() => handleSortChange()} variant="outline">
      <p className="capitalize">{field.toString()}</p>
      {sort[field] === "asc" ? (
        <ArrowUp size={16} />
      ) : sort[field] === "desc" ? (
        <ArrowDown size={16} />
      ) : (
        ""
      )}
    </Button>
  );
}
