"use client";

import {
  notFound,
  useParams,
  useRouter,
  useSearchParams,
} from "next/navigation";
import { parseAndValidateNumbers } from "@/lib/utils";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  ExerciseResponse,
  PageableResponse,
  TrainingResponse,
  TrainingResponseWithOrderCount,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useEffect, useState } from "react";
import { useGetTraining } from "@/app/(main)/(items)/trainings/single/hook";
import { useSession } from "next-auth/react";
import Loader from "@/components/ui/spinner";
import TrainingsForm from "@/components/forms/trainings-form";

export default function UpdateTraining() {
  const router = useRouter();
  const session = useSession();
  const authUser = session.data?.user;
  const [trainingState, setTrainingState] = useState<TrainingResponse | null>();
  const [exercisesResponse, setExercisesResponse] = useState<
    ExerciseResponse[]
  >([]);
  const { id } = useParams();
  const searchParams = useSearchParams();
  const exercisesIds = parseAndValidateNumbers(
    searchParams.get("exercises"),
    "Exercises IDs must be a comma-separated list of numbers.",
    notFound,
  );
  const {
    messages,
    error,
    refetch,
    isFinished,
    isExerciseFinished,
    exercisesError,
    exercisesMessages,
  } = useGetTraining({
    exercisesIds,
    setExercisesResponse,
    setTrainingState,
    id: id instanceof Array ? id[0] : id,
  });

  if (error?.status || exercisesError?.status) {
    notFound();
  }

  if (!isFinished || !isExerciseFinished) {
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );
  }
  if (!authUser || !messages[0])
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );
  const isOwner = trainingState?.userId === parseInt(authUser.id);
  console.log({ userTraining: trainingState?.userId, authUser: authUser.id });
  if (!isOwner && trainingState) {
    console.log("not owner");
    notFound();
  }

  if (
    isFinished &&
    isExerciseFinished &&
    exercisesResponse.length > 0 &&
    trainingState &&
    exercisesResponse.length !== trainingState.exercises.length
  ) {
    notFound();
  }

  console.log("exercisesres", exercisesResponse);
  if (!trainingState || !authUser) return null;

  return (
    <section
      className="w-full flex  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14
    "
    >
      <TrainingsForm
        path={`/trainings/update/${trainingState?.id}`}
        method="PUT"
        title={trainingState?.title}
        body={trainingState?.body}
        images={trainingState?.images}
        exercises={exercisesResponse.map(({ title, id }) => ({
          label: title,
          value: id.toString(),
        }))}
        price={trainingState?.price}
        submitText="Update Training"
        callback={refetch}
        header={`Update Training: ${trainingState?.title} `}
        authUser={authUser}
      />
    </section>
  );
}
