import ExercisesTable from "@/components/data-table/exercises-table";
import Loader from "@/components/ui/spinner";
import { Suspense } from "react";

export default function TrainerExercisesAdmin({
  params: { userId },
}: {
  params: { userId: string };
}) {
  return (
    <Suspense
      fallback={
        <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14 ">
          <Loader className="w-full" />
        </section>
      }
    >
      <div className="w-full">
        <ExercisesTable
          path={`/exercises/trainer/${userId}`}
          title={`Users ${userId} Exercises`}
          forWhom="admin"
        />
      </div>
    </Suspense>
  );
}
