"use client";

import CustomImageCarousel from "@/components/common/custom-image-crousel";
import CustomVideoCarousel from "@/components/common/custom-video-crousel";
import CustomPaginationButtons from "@/components/ui/custom-pagination-buttons";
import { ExerciseResponse } from "@/types/dto";

import { useState } from "react";

interface Props {
  exercises: ExerciseResponse[];
}

export default function TrainingExercises({ exercises }: Props) {
  const [currentIndex, setCurrentIndex] = useState(0);

  const currentExercise = exercises[currentIndex];
  if (!currentExercise) {
    return null;
  }
  return (
    <div className="mt-10">
      <div className="mb-5">
        <CustomPaginationButtons
          items={exercises}
          currentIndex={currentIndex}
          setCurrentIndex={setCurrentIndex}
        />
      </div>
      <h1 className="text-4xl bold text-center mb-20">
        {currentExercise.title}
      </h1>
      {currentExercise.videos?.length > 0 && (
        <CustomVideoCarousel videos={currentExercise.videos} />
      )}
      <div className="mt-20 px-14 mb-20">
        <div
          className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
          dangerouslySetInnerHTML={{ __html: currentExercise?.body ?? "" }}
        />
      </div>
      {currentExercise.images?.length > 0 && (
        <CustomImageCarousel images={currentExercise.images} />
      )}

      <div className="mt-10">
        <CustomPaginationButtons
          items={exercises}
          currentIndex={currentIndex}
          setCurrentIndex={setCurrentIndex}
        />
      </div>
    </div>
  );
}
