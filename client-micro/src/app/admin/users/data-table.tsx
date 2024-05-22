// "use client";

// import {
//   ColumnDef,
//   flexRender,
//   getCoreRowModel,
//   useReactTable,
//   getPaginationRowModel,
//   VisibilityState,
// } from "@tanstack/react-table";

// import {
//   Table,
//   TableBody,
//   TableCell,
//   TableHead,
//   TableHeader,
//   TableRow,
// } from "@/components/ui/table";

// import { Button } from "@/components/ui/button";
// import { Dispatch, SetStateAction, useState } from "react";
// import { DataTablePagination } from "@/components/ui/data-table-pagination";
// import { PageInfo } from "@/types/dto";

// import {
//   DropdownMenu,
//   DropdownMenuCheckboxItem,
//   DropdownMenuContent,
//   DropdownMenuTrigger,
// } from "@/components/ui/dropdown-menu";

// export interface Pagination {
//   pageIndex: number;
//   pageSize: number;
// }

// interface DataTableProps<TData, TValue> {
//   columns: ColumnDef<TData, TValue>[];
//   data: TData[];
//   //   pagination: Pagination;
//   //   setPagination: Dispatch<SetStateAction<Pagination>>;
//   //   totalPages: number;
//   //   totalElements: number;
//   pageInfo: PageInfo;
//   setPageInfo: Dispatch<SetStateAction<PageInfo>>;
// }

// export function DataTable<TData, TValue>({
//   columns,
//   data,
//   pageInfo,
//   setPageInfo,
// }: //   pagination,
// //   setPagination,
// //   totalPages,
// //   totalElements,
// DataTableProps<TData, TValue>) {
//   const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
//   const table = useReactTable({
//     data,
//     columns,
//     getCoreRowModel: getCoreRowModel(),
//     // getPaginationRowModel: getPaginationRowModel(),
//     manualPagination: true,
//     autoResetPageIndex: true,
//     onColumnVisibilityChange: setColumnVisibility,
//     state: {
//       columnVisibility,
//     },
//   });

//   return (
//     <div>
//       <div className="flex items-center py-4">
//         <DropdownMenu>
//           <DropdownMenuTrigger asChild>
//             <Button variant="outline" className="ml-auto">
//               Columns
//             </Button>
//           </DropdownMenuTrigger>
//           <DropdownMenuContent align="end">
//             {table
//               .getAllColumns()
//               .filter((column) => column.getCanHide())
//               .map((column) => {
//                 return (
//                   <DropdownMenuCheckboxItem
//                     key={column.id}
//                     className="capitalize"
//                     checked={column.getIsVisible()}
//                     onCheckedChange={(value) =>
//                       column.toggleVisibility(!!value)
//                     }
//                   >
//                     {column.id}
//                   </DropdownMenuCheckboxItem>
//                 );
//               })}
//           </DropdownMenuContent>
//         </DropdownMenu>
//       </div>
//       <div className="rounded-md border">
//         <Table>
//           <TableHeader>
//             {table.getHeaderGroups().map((headerGroup) => (
//               <TableRow key={headerGroup.id}>
//                 {headerGroup.headers.map((header) => {
//                   return (
//                     <TableHead key={header.id}>
//                       {header.isPlaceholder
//                         ? null
//                         : flexRender(
//                             header.column.columnDef.header,
//                             header.getContext()
//                           )}
//                     </TableHead>
//                   );
//                 })}
//               </TableRow>
//             ))}
//           </TableHeader>
//           <TableBody>
//             {table.getRowModel().rows?.length ? (
//               table.getRowModel().rows.map((row) => (
//                 <TableRow
//                   key={row.id}
//                   data-state={row.getIsSelected() && "selected"}
//                 >
//                   {row.getVisibleCells().map((cell) => (
//                     <TableCell key={cell.id}>
//                       {flexRender(
//                         cell.column.columnDef.cell,
//                         cell.getContext()
//                       )}
//                     </TableCell>
//                   ))}
//                 </TableRow>
//               ))
//             ) : (
//               <TableRow>
//                 <TableCell
//                   colSpan={columns.length}
//                   className="h-24 text-center"
//                 >
//                   No results.
//                 </TableCell>
//               </TableRow>
//             )}
//           </TableBody>
//         </Table>
//       </div>

//       <DataTablePagination
//         table={table}
//         pageInfo={pageInfo}
//         setPageInfo={setPageInfo}
//       />
//     </div>
//   );
// }
