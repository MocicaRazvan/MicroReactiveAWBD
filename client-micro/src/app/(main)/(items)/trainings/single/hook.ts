import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  ExerciseResponse,
  PageableResponse,
  TrainingResponse,
  TrainingResponseWithOrderCount,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { Dispatch, SetStateAction, useEffect } from "react";

interface Args {
  exercisesIds: number[];
  setExercisesResponse: Dispatch<SetStateAction<ExerciseResponse[]>>;
  setTrainingState: Dispatch<
    SetStateAction<TrainingResponse | null | undefined>
  >;
  id: string;
}

export function useGetTraining({
  exercisesIds,
  setExercisesResponse,
  setTrainingState,
  id,
}: Args) {
  const {
    messages: exercisesMessages,
    error: exercisesError,
    isFinished: isExerciseFinished,
  } = useFetchStream<
    PageableResponse<CustomEntityModel<ExerciseResponse>>,
    BaseError
  >({
    path: `/exercises/byIds`,
    method: "PATCH",
    authToken: true,
    body: {
      page: 0,
      size: exercisesIds.length,
    },
    arrayQueryParam: { ids: exercisesIds.map(String) },
  });

  const { messages, error, isFinished, refetch } = useFetchStream<
    TrainingResponseWithOrderCount,
    BaseError
  >({
    path: `/trainings/withOrderCount/${id}`,
    method: "GET",
    authToken: true,
  });

  useEffect(() => {
    if (exercisesMessages.length > 0) {
      setExercisesResponse(exercisesMessages.map((e) => e.content.content));
    }
  }, [JSON.stringify(exercisesMessages)]);

  useEffect(() => {
    if (messages.length > 0) {
      setTrainingState(messages[0]);
    }
  }, [JSON.stringify(messages)]);
  console.log({ t: messages?.[0] });
  return {
    messages,
    error,
    refetch,
    isFinished,
    isExerciseFinished,
    exercisesError,
    exercisesMessages,
  };
}
