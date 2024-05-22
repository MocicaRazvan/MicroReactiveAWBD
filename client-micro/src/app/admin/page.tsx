import { redirect } from "next/navigation";

export default function AdminPage() {
  redirect("/admin/posts");
  return <section className="min-h-[100vh]"></section>;
}
