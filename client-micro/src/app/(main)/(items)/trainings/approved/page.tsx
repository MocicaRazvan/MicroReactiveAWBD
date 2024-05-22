import { Suspense } from "react";
import TrainingsList from "./trainings-list";
import Loader from "@/components/ui/spinner";
import { getServerSession } from "next-auth";
import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";

export default async function TrainingsApprovedPage() {
  const session = await getServerSession(authOptions);
  if (!session?.user?.id) return null;

  return (
    <section className="w-full min-h-[calc(100vh-4rem)] transition-all py-5 px-4 max-w-[1250px] mx-auto ">
      <h1 className="text-3xl font-bold tracking-tight">Trainings</h1>
      <p className="text-gray-500 dark:text-gray-400">
        Made with love by our trainers and approved by our team.
      </p>
      <Suspense
        fallback={
          <section className="w-full min-h-[calc(100vh-4rem)] transition-all py-5 px-4 max-w-[1250px] mx-auto ">
            <Loader className="w-full" />
          </section>
        }
      >
        <TrainingsList userId={session?.user?.id} />
      </Suspense>
    </section>
  );
}
