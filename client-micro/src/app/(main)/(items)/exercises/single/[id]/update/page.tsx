"use client";
import { useGetExercise } from "@/app/(main)/(items)/exercises/single/hook";
import { notFound } from "next/navigation";
import Loader from "@/components/ui/spinner";
import ExerciseForm from "@/components/forms/exercise-form";

export default function UpdateExercise() {
  const {
    exerciseState,
    setExerciseState,
    messages,
    error,
    refetch,
    authUser,
    router,
    session,
    id,
  } = useGetExercise();
  if (error?.status) {
    notFound();
  }

  if (!authUser || !messages[0])
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all overflow-hidden">
        <Loader className="w-full" />
      </section>
    );
  const exercise = messages[0];
  if (!exerciseState || !authUser) return null;
  const isOwner = exerciseState?.userId === parseInt(authUser.id);
  console.log({ userExercise: exerciseState?.userId, authUser: authUser.id });

  if (!isOwner) {
    console.log("not owner");
    notFound();
  }

  return (
    <section
      className="w-full flex  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14
    "
    >
      <ExerciseForm
        path={`/exercises/update/${exercise.id}`}
        method="PUT"
        body={exercise.body}
        title={exercise.title}
        images={exercise.images}
        videos={exercise.videos}
        muscleGroups={exercise.muscleGroups.map((m) => ({
          label: m,
          value: m,
        }))}
        submitText={`Update Exercise`}
        callback={refetch}
        header={`Update Exercise: ${exercise.title} `}
      />
    </section>
  );
}
