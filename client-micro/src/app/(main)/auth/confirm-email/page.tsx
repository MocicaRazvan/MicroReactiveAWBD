"use client";
import Loader from "@/components/ui/spinner";
import { notFound, useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import useFetchStream from "@/hoooks/useFetchStream";

export default function ConfirmEmailPage() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const email = searchParams.get("email");
  const userId = searchParams.get("userId");
  const router = useRouter();
  if (!token || !email || !userId) {
    notFound();
  }
  const { messages, isFinished, error } = useFetchStream({
    path: "/auth/confirmEmail",
    method: "POST",
    queryParams: { email, token },
  });

  console.log(messages, isFinished, error);

  if (isFinished && !error) {
    router.replace(`/users/${userId}`);
  }

  return (
    <main className="w-full min-h-[calc(100vh-21rem)] flex items-center justify-center transition-all">
      {!isFinished && (
        <div className="w-full h-full flex flex-col  items-center justify-center ">
          <Loader className="mb-5" />
          <h1 className="text-lg bold tracking-ticghter ">
            Confirming your email...
          </h1>
        </div>
      )}
      {isFinished && error && (
        <div className="w-full h-full flex flex-col  items-center justify-center ">
          <h1 className="text-2xl bold tracking-ticghter text-destructive">
            Something went wrong, please repeat the process
          </h1>
        </div>
      )}
    </main>
  );
}
