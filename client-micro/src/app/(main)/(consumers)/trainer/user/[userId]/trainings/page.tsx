import TrainingsTable from "@/components/data-table/trainings-table";
import Loader from "@/components/ui/spinner";
import { Suspense } from "react";

export default function TrainerTrainings({
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
        <TrainingsTable
          path={`/trainings/trainer/${userId}`}
          title="Your Trainings"
          forWhom="trainer"
        />
      </div>
    </Suspense>
  );
}
