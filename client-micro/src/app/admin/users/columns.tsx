// "use client";
// import { Badge } from "@/components/ui/badge";
// import { UserDto } from "@/types/dto";
// import { ColumnDef } from "@tanstack/react-table";
// import { MoreHorizontal } from "lucide-react";

// import { Button } from "@/components/ui/button";
// import {
//   DropdownMenu,
//   DropdownMenuContent,
//   DropdownMenuItem,
//   DropdownMenuLabel,
//   DropdownMenuSeparator,
//   DropdownMenuTrigger,
// } from "@/components/ui/dropdown-menu";

// import { Checkbox } from "@/components/ui/checkbox";

// export const columns: ColumnDef<UserDto>[] = [
//   {
//     accessorKey: "email",
//     header: () => <div className="text-right">Email</div>,
//   },
//   {
//     accessorKey: "firstName",
//     header: "First Name",
//   },
//   {
//     accessorKey: "lastName",
//     header: "Last Name",
//   },
//   {
//     accessorKey: "role",
//     header: ()=><div className="text-center">Role</div>,
//     cell: ({ row }) => (
//       <Badge
//         variant={
//           row.getValue("role") === "ROLE_ADMIN"
//             ? "destructive"
//             : row.getValue("role") === "ROLE_TRAINER"
//             ? "default"
//             : "secondary"
//         }
//       >
//         {row.getValue("role")}
//       </Badge>
//     ),
//   },
//   {
//     id: "actions",
//     cell: ({ row }) => {
//       const user = row.original;

//       return (
//         <DropdownMenu>
//           <DropdownMenuTrigger asChild>
//             <Button variant="ghost" className="h-8 w-8 p-0">
//               <span className="sr-only">Open menu</span>
//               <MoreHorizontal className="h-4 w-4" />
//             </Button>
//           </DropdownMenuTrigger>
//           <DropdownMenuContent align="end">
//             <DropdownMenuLabel>Actions</DropdownMenuLabel>
//             <DropdownMenuItem
//               onClick={() => navigator.clipboard.writeText(user.email)}
//             >
//               Copy User Email
//             </DropdownMenuItem>
//             <DropdownMenuSeparator />
//             <DropdownMenuItem>View customer</DropdownMenuItem>
//             <DropdownMenuItem>View payment details</DropdownMenuItem>
//           </DropdownMenuContent>
//         </DropdownMenu>
//       );
//     },
//   },
// ];
