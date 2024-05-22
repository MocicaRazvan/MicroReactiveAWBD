"use client";

import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  ExerciseResponse,
  TrainingResponse,
} from "@/types/dto";
import { useSession } from "next-auth/react";
import Link from "next/link";
import { notFound, useParams } from "next/navigation";
import { useCallback, useMemo, useState } from "react";
import Loader from "../ui/spinner";
import { useForm } from "react-hook-form";
import { BasicFormProps, TrainingType, trainingSchema } from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { Card, CardContent, CardTitle } from "../ui/card";
import { TitleBodyForm } from "./title-body";
import CustomCloudinaryWidget from "./cloudinary-widget";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import MultipleSelector from "../ui/multiple-selector";
import { fetchStream } from "@/hoooks/fetchStream";
import { Session } from "next-auth";
import { toast } from "@/components/ui/use-toast";
import { ToastAction } from "@/components/ui/toast";

interface Props extends Partial<TrainingType>, BasicFormProps {
  redirect?: boolean;
  authUser: NonNullable<Session["user"]>;
}

export default function TrainingsForm({
  body = "",
  title = "",
  submitText = "Create Training",
  path,
  method,
  header = "Create Training",
  callback,
  images = [],
  exercises = [],
  price = 0,
  redirect = false,
  authUser,
}: Props) {
  // const authUser = useSession().data?.user;
  const {
    messages: userExercises,
    error: exerciseError,
    isFinished: isExerciseFinished,
  } = useFetchStream<CustomEntityModel<ExerciseResponse>>({
    path: "/exercises/approved/trainer/" + authUser?.id,
    method: "GET",
    authToken: true,
  });
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const form = useForm<TrainingType>({
    resolver: zodResolver(trainingSchema),
    defaultValues: {
      title,
      body,
      images,
      exercises,
      price,
    },
  });

  const exerciseIds = useMemo(
    () =>
      userExercises
        .map(({ content: { id, title } }) => ({
          label: title,
          value: id.toString(),
        }))
        .sort((a, b) => a.label.localeCompare(b.label)),
    [JSON.stringify(userExercises)],
  );

  const handleSearch = useCallback(
    async (search: string) => {
      console.log(search);
      return exerciseIds.filter(({ label }) =>
        label.toLowerCase().includes(search.toLowerCase()),
      );
    },
    [exerciseIds],
  );

  const onSubmit = useCallback(
    async (values: TrainingType) => {
      if (!authUser?.token) return;
      setIsLoading(true);
      const traininBody = {
        ...values,
        exercises: values.exercises.map((e) => parseInt(e.value)),
      };
      try {
        const { messages, error } = await fetchStream<
          CustomEntityModel<TrainingResponse>
        >({
          path,
          method,
          token: authUser.token,
          body: traininBody,
        });
        if (error?.message) {
          setErrorMsg(error.message);
        } else {
          callback?.();
          const toastAction = (
            <ToastAction
              altText="See"
              onClick={() =>
                router.push(
                  `/trainings/single/${messages[0].content.id}?exercises=` +
                    traininBody.exercises.join(`,`),
                )
              }
            >
              See Training
            </ToastAction>
          );
          title
            ? toast({
                title: values.title,
                description: "Exercise updated successfully",
                variant: "success",
              })
            : toast({
                title: values.title,
                description: "Exercise created successfully",
                variant: "success",
              });
          if (redirect) {
            router.push(`/trainer/user/${authUser.id}/trainings`);
          }
        }
      } catch (error) {
        console.log(error);
      } finally {
        setIsLoading(false);
      }
    },
    [authUser?.id, authUser?.token, callback, method, path, redirect, router],
  );

  if (exerciseError?.status) {
    return notFound();
  }

  if (!authUser) return null;

  console.log("isExerciseFinished", isExerciseFinished);
  console.log("exerciseIds", exerciseIds.length);
  console.log("exercises", exercises?.length);

  if (isExerciseFinished && exerciseIds && exerciseIds?.length === 0)
    return (
      <div>
        <Link
          href="/trainer/exercises/create"
          className="text-4xl mt-20 font-bold tracking-tighter hover:underline"
        >
          First create some exercises!
        </Link>
      </div>
    );

  if (!isExerciseFinished) {
    return (
      <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative pb-14">
        <h1>Loading exercises</h1>
        <Loader />
      </section>
    );
  }

  return (
    <Card className="max-w-6xl w-full sm:px-2 md:px-5 py-6">
      <CardTitle className="font-bold text-2xl text-center">{header}</CardTitle>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <TitleBodyForm
              control={form.control}
              titlePlaceholder="Chest day"
              bodyPlaceholder="Body.."
            />
            <CustomCloudinaryWidget
              formKey="images"
              type="image"
              multiple={true}
              defaultValues={images}
            />
            <FormField
              control={form.control}
              name="price"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Price</FormLabel>
                  <FormControl>
                    <Input placeholder="0" {...field} type="number" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="exercises"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Select The exercises for the training</FormLabel>
                  <FormControl>
                    <MultipleSelector
                      onSearch={handleSearch}
                      value={field.value}
                      onChange={field.onChange}
                      defaultOptions={exerciseIds}
                      placeholder="Select the muscle groups for the exercise"
                      emptyIndicator={
                        <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
                          no results found.
                        </p>
                      }
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex items-center justify-center mt-4 flex-col gap-4">
              {errorMsg && (
                <p className="font-medium text-destructive">{errorMsg}</p>
              )}
              {!isLoading ? (
                <Button
                  type="submit"
                  size="lg"
                  disabled={!form.formState.isDirty}
                >
                  {submitText}
                </Button>
              ) : (
                <Button disabled>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Please wait
                </Button>
              )}
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
