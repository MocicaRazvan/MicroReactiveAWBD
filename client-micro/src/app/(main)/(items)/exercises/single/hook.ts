import { useEffect, useState } from "react";
import {
  ExerciseResponse,
  ExerciseResponseWithTrainingCount,
} from "@/types/dto";
import { useParams, useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import useFetchStream from "@/hoooks/useFetchStream";
import { BaseError } from "@/types/responses";

export const useGetExercise = () => {
  const [exerciseState, setExerciseState] = useState<ExerciseResponse | null>(
    null,
  );
  const { id } = useParams();
  const session = useSession();
  const router = useRouter();

  const authUser = session.data?.user;

  const { messages, error, refetch } = useFetchStream<
    ExerciseResponseWithTrainingCount,
    BaseError
  >({
    path: `/exercises/withTrainingCount/${id}`,
    method: "GET",
    authToken: true,
  });

  useEffect(() => {
    if (messages.length > 0) {
      setExerciseState(messages[0]);
    }
  }, [JSON.stringify(messages)]);

  return {
    exerciseState,
    setExerciseState,
    messages,
    error,
    refetch,
    authUser,
    router,
    session,
    id,
  };
};
