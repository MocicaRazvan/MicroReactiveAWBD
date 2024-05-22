"use client";
import CustomImageCarousel from "@/components/common/custom-image-crousel";
import CustomVideoCarousel from "@/components/common/custom-video-crousel";
import AlertDialogDeleteExercise from "@/components/dialogs/exercises/delete-exercise";
import ExerciseForm from "@/components/forms/exercise-form";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  ExerciseResponse,
  ExerciseResponseWithTrainingCount,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useSession } from "next-auth/react";
import { notFound, useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useGetExercise } from "@/app/(main)/(items)/exercises/single/hook";
import { Button } from "@/components/ui/button";
import ElementHeader from "@/components/common/single/element-header";

export default function SingleExercise() {
  // const [exerciseState, setExerciseState] = useState<ExerciseResponse | null>(
  //   null
  // );
  // const { id } = useParams();
  // const session = useSession();
  // const router = useRouter();
  //
  // const authUser = session.data?.user;
  //
  // const { messages, error, refetch } = useFetchStream<
  //   ExerciseResponseWithTrainingCount,
  //   BaseError
  // >({
  //   path: `/exercises/withTrainingCount/${id}`,
  //   method: "GET",
  //   authToken: true,
  // });
  //
  // useEffect(() => {
  //   if (messages.length > 0) {
  //     setExerciseState(messages[0]);
  //   }
  // }, [JSON.stringify(messages)]);
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
  const isAdmin = authUser.role === "ROLE_ADMIN";
  const isOwner = exerciseState?.userId === parseInt(authUser.id);
  const isOwnerOrAdmin = isOwner || isAdmin;

  if (!isOwnerOrAdmin) {
    notFound();
  }

  console.log(messages);

  return (
    <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14">
      <ElementHeader elementState={exercise} />
      {exercise?.videos?.length > 0 && (
        <CustomVideoCarousel videos={exercise?.videos} />
      )}
      <div className="mt-20 px-14 mb-20">
        <div
          className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
          dangerouslySetInnerHTML={{ __html: exerciseState?.body ?? "" }}
        />
      </div>
      {exercise?.images?.length > 0 && (
        <CustomImageCarousel images={exercise?.images} />
      )}
      {isOwnerOrAdmin && messages[0].trainingCount === 0 && (
        <div className="sticky bottom-3 my-7 flex items-center justify-center gap-4">
          <AlertDialogDeleteExercise
            exercise={exercise}
            token={session.data?.user?.token}
            callBack={() => {
              isAdmin
                ? router.push("/admin/exercises")
                : router.push(`/trainer/user/${authUser.id}/exercises`);
            }}
          />
          {isOwner && messages[0].trainingCount === 0 && (
            <Button
              onClick={() => {
                router.push(`/exercises/single/${id}/update/`);
              }}
            >
              Update Exercise
            </Button>
          )}
        </div>
      )}
      {/*{isOwner && messages[0].trainingCount === 0 && (*/}
      {/*  <Accordion*/}
      {/*    type="single"*/}
      {/*    collapsible*/}
      {/*    className="w-1/2 mx-auto mt-10 z-50"*/}
      {/*  >*/}
      {/*    <AccordionItem value="item-1">*/}
      {/*      <AccordionTrigger>Update Exercise</AccordionTrigger>*/}
      {/*      <AccordionContent>*/}
      {/*        <ExerciseForm*/}
      {/*          path={`/exercises/update/${exercise.id}`}*/}
      {/*          method="PUT"*/}
      {/*          body={exercise.body}*/}
      {/*          title={exercise.title}*/}
      {/*          images={exercise.images}*/}
      {/*          videos={exercise.videos}*/}
      {/*          muscleGroups={exercise.muscleGroups.map((m) => ({*/}
      {/*            label: m,*/}
      {/*            value: m,*/}
      {/*          }))}*/}
      {/*          submitText="Update Exercise"*/}
      {/*          callback={refetch}*/}
      {/*        />*/}
      {/*      </AccordionContent>*/}
      {/*    </AccordionItem>*/}
      {/*  </Accordion>*/}
      {/*)}*/}
    </section>
  );
}
