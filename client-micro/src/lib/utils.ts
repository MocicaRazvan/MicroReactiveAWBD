import { SortDirection, sortDirections } from "@/types/fetch-utils";
import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function isDeepEqual<T>(obj1: T, obj2: T): boolean {
  if (
    typeof obj1 !== "object" ||
    typeof obj2 !== "object" ||
    obj1 === null ||
    obj2 === null
  ) {
    return obj1 === obj2;
  }

  if (obj1 instanceof Date && obj2 instanceof Date) {
    return obj1.getTime() === obj2.getTime();
  }

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) {
    return false;
  }

  for (const key of keys1) {
    const val1 = (obj1 as any)[key];
    const val2 = (obj2 as any)[key];

    const areObjects = typeof val1 === "object" && typeof val2 === "object";
    if (
      (areObjects && !isDeepEqual(val1, val2)) ||
      (!areObjects && val1 !== val2)
    ) {
      return false;
    }
  }

  return true;
}

export function makeSortFetchParams(
  sort: Record<string | number | symbol, string>,
) {
  return Object.fromEntries(
    Object.entries(sort).filter(([_, value]) => value !== "none"),
  );
}

export function makeSortString(sort: Record<string | number | symbol, string>) {
  return Object.entries(sort)
    .map(([key, value]) => `${key}:${value}`)
    .join(",");
}

export function parseSortString<T extends string>(
  sortStr: string,
  validKeys: T[],
): Record<T, SortDirection> {
  return sortStr
    .split(",")
    .map((part) => part.split(":") as [T, SortDirection])
    .filter(
      ([key, value]) =>
        validKeys.includes(key) && sortDirections.includes(value),
    )
    .reduce<Record<T, SortDirection>>(
      (acc, [key, value]) => {
        acc[key] = value;
        return acc;
      },
      {} as Record<T, SortDirection>,
    );
}

export function parseAndValidateNumbers(
  input: string | null,
  errorMessage: string,
  callback: () => never,
): number[] {
  if (!input) {
    callback();
  }
  return input.split(",").map((item) => {
    const number = Number(item);
    if (Number.isNaN(number)) callback();
    return number;
  });
}

export const parseQueryParamAsInt = (
  paramValue: string | null,
  defaultValue: number,
): number => {
  const parsedValue = parseInt(paramValue || "", 10);
  return isNaN(parsedValue) ? defaultValue : parsedValue;
};

export function getCSSVariableValue(variableName: string) {
  const style = getComputedStyle(document.documentElement);
  const isDarkMode = document.documentElement.classList.contains("dark");
  const modePrefix = isDarkMode ? "dark-" : "";
  return style.getPropertyValue(`--${modePrefix}${variableName}`).trim();
}

export function roundToDecimalPlaces(value: number, decimalPlaces: number) {
  return Math.round(value * 10 ** decimalPlaces) / 10 ** decimalPlaces;
}
