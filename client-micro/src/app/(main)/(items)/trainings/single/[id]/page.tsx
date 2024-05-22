"use client";

import {
  CustomEntityModel,
  ExerciseResponse,
  PageableResponse,
  TrainingResponse,
  TrainingResponseWithOrderCount,
} from "@/types/dto";
import { Suspense, useCallback, useEffect, useMemo, useState } from "react";
import {
  notFound,
  useParams,
  useRouter,
  useSearchParams,
} from "next/navigation";
import { useSession } from "next-auth/react";
import useFetchStream from "@/hoooks/useFetchStream";
import { BaseError } from "@/types/responses";
import Loader from "@/components/ui/spinner";
import { parseAndValidateNumbers } from "@/lib/utils";
import CustomVideoCarousel from "@/components/common/custom-video-crousel";
import { fetchStream } from "@/hoooks/fetchStream";
import LikesDislikes from "@/components/common/single/likes-dislikes";
import Link from "next/link";
import { AvatarImage, Avatar } from "@/components/ui/avatar";
import TrainingExercises from "./training-exercises";
import CustomImageCarousel from "@/components/common/custom-image-crousel";
import AlertDialogDeleteTraining from "@/components/dialogs/trainings/delete-training";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import TrainingsForm from "@/components/forms/trainings-form";
import { Button } from "@/components/ui/button";
import { useGetTraining } from "@/app/(main)/(items)/trainings/single/hook";
import ElementHeader from "@/components/common/single/element-header";

export default function TrainingPage() {
  const [trainingState, setTrainingState] = useState<TrainingResponse | null>();
  const [exercisesResponse, setExercisesResponse] = useState<
    ExerciseResponse[]
  >([]);
  const { id } = useParams();
  const session = useSession();
  const router = useRouter();
  const authUser = session.data?.user;
  const searchParams = useSearchParams();
  const exercisesIds = parseAndValidateNumbers(
    searchParams.get("exercises"),
    "Exercises IDs must be a comma-separated list of numbers.",
    notFound,
  );

  console.log({ exercisesResponse });

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

  const react = useCallback(
    async (type: "like" | "dislike") => {
      if (!trainingState?.id || !session.data?.user?.token) return;
      try {
        const resp = await fetchStream<CustomEntityModel<TrainingResponse>>({
          path: `/trainings/${type}/${trainingState.id}`,
          method: "PATCH",
          token: session.data?.user?.token,
        });
        if (resp.error) throw resp.error;
        const newTraining = resp.messages[0]?.content;
        setTrainingState((prev) =>
          !prev
            ? prev
            : {
                ...prev,
                userLikes: newTraining.userLikes,
                userDislikes: newTraining.userDislikes,
              },
        );
      } catch (error) {
        console.log(error);
      }
    },
    [trainingState?.id, session.data?.user?.token],
  );

  if (error?.status || exercisesError?.status) {
    notFound();
  }

  if (!authUser || !messages[0] || !trainingState)
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );
  const isAdmin = authUser.role === "ROLE_ADMIN";
  const isOwner = trainingState?.userId === parseInt(authUser.id);
  const isOwnerOrAdmin = isOwner || isAdmin;

  if (trainingState && !trainingState.approved && !isOwnerOrAdmin) {
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

  const isLiked = trainingState?.userLikes.includes(parseInt(authUser.id));
  const isDisliked = trainingState?.userDislikes.includes(
    parseInt(authUser.id),
  );
  const user = messages[0]?.user;

  if (!trainingState || !user) return null;
  return (
    <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14">
      <div
        className="sticky top-[4rem] z-10 shadow-md p-4 w-[130px] rounded-xl
      bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 overflow-hidden
      "
      >
        <div className="flex justify-center items-center w-full gap-2">
          <span>Price: </span>
          <span className="font-bold">${trainingState.price}</span>
        </div>
      </div>
      <ElementHeader
        elementState={trainingState}
        react={react}
        isLiked={isLiked}
        isDisliked={isDisliked}
      />

      {trainingState?.images?.length > 0 && (
        <CustomImageCarousel images={trainingState?.images} />
      )}
      <div className="mt-20 px-14">
        <div
          className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
          dangerouslySetInnerHTML={{ __html: trainingState?.body ?? "" }}
        />
      </div>
      <Link href={`/users/${user.id}`}>
        <div className="flex items-center justify-center mt-16 gap-2 cursor-pointer px-4 py-2 hover:border rounded-lg transition-all w-1/3 mx-auto group">
          <Avatar className="group-hover:translate-y-[-10px]">
            <AvatarImage
              src={user.image}
              alt={user.email}
              className="w-16 h-16"
            />
          </Avatar>
          <p className="group-hover:translate-y-[-10px]">{user.email}</p>
        </div>
      </Link>
      {isOwnerOrAdmin && messages[0].orderCount === 0 && (
        <div className="sticky bottom-3 my-7 flex items-center justify-center gap-5">
          <AlertDialogDeleteTraining
            training={trainingState}
            token={session.data?.user?.token}
            callBack={() => {
              isAdmin
                ? router.push("/admin/trainings")
                : router.push(`/trainer/user/${authUser.id}/trainings`);
            }}
          />
          {isOwner && messages[0].orderCount === 0 && exercisesResponse && (
            <Button
              onClick={() => {
                router.push(
                  `/trainings/single/${id}/update/?exercises=` +
                    exercisesIds.join(`,`),
                );
              }}
            >
              Update Training
            </Button>
          )}
        </div>
      )}

      <div className="my-10 mt-24">
        <h1 className="font-bold text-4xl tracking-tight text-center">
          Exercises of the training
        </h1>
      </div>
      <Suspense fallback={<Loader className="w-full h-full" />}>
        <TrainingExercises exercises={exercisesResponse} />
      </Suspense>
    </section>
  );
}
