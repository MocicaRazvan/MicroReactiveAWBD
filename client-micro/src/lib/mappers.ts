import {
  ExerciseResponse,
  ExerciseResponseWithTrainingCount,
} from "@/types/dto";

export const fromExerciseResponseWithTrainingCountToExerciseResponse = (
  data: ExerciseResponseWithTrainingCount
): ExerciseResponse => ({
  ...data,
});
