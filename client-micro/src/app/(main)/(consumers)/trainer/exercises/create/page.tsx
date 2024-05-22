import ExerciseForm from "@/components/forms/exercise-form";
import { Loader } from "lucide-react";
import { Suspense } from "react";

export default function CreateExercise() {
  return (
    <main className="flex items-center justify-center px-6 py-10">
      <Suspense fallback={<Loader />}>
        <ExerciseForm path="/exercises/create" method="POST" />
      </Suspense>
    </main>
  );
}
