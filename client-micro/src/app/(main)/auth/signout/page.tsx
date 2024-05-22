"use client";
import { Button } from "@/components/ui/button";
import { signOut } from "next-auth/react";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function SingOut() {
  const router = useRouter();

  return (
    <main className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
      <div className="border p-10 rounded-xl flex justify-center items-center flex-col gap-10">
        <h1 className="text-2xl font-bold text-center text-destructive">
          Are you sure you want to sign out?
        </h1>

        <Button
          variant="destructive"
          className=" px-24 py-5"
          onClick={() => {
            signOut({ redirect: false, callbackUrl: "/auth/signin" });
            router.push("/auth/signin");
          }}
        >
          Sign Out
        </Button>
        <Link href="/auth/signin" className="italic hover:underline">
          Sign In
        </Link>
      </div>
    </main>
  );
}
