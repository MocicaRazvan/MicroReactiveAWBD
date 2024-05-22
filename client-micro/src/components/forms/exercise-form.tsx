"use client";

import {
  BasicFormProps,
  ExerciseType,
  exerciseSchema,
  muscleGroupsOptions,
} from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { useForm } from "react-hook-form";
import { Card, CardContent, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { TitleBodyForm } from "./title-body";
import CustomCloudinaryWidget from "./cloudinary-widget";
import MultipleSelector from "../ui/multiple-selector";
import { Button } from "../ui/button";
import { Loader2 } from "lucide-react";
import { CustomEntityModel, ExerciseResponse } from "@/types/dto";
import { fetchStream } from "@/hoooks/fetchStream";
import { toast } from "@/components/ui/use-toast";
import { ToastAction } from "@/components/ui/toast";

interface Props extends Partial<ExerciseType>, BasicFormProps {}

export default function ExerciseForm({
  body = "",
  title = "",
  images = [],
  muscleGroups = [],
  videos = [],
  submitText = "Create Exercise",
  path,
  method,
  header = "Create Exercise",
  callback,
}: Props) {
  const session = useSession();
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<ExerciseType>({
    resolver: zodResolver(exerciseSchema),
    defaultValues: {
      title,
      body,
      images,
      muscleGroups,
      videos,
    },
  });
  const onSubmit = useCallback(
    async (values: ExerciseType) => {
      if (!session.data?.user?.token) return;
      setIsLoading(true);
      const postBody = {
        ...values,
        muscleGroups: values.muscleGroups.map((m) => m.value),
      };
      try {
        const { messages, error } = await fetchStream<
          CustomEntityModel<ExerciseResponse>
        >({
          path,
          method,
          token: session.data?.user?.token,
          body: postBody,
        });
        console.log(error);
        console.log(messages);
        if (error?.message) {
          setErrorMsg(error.message);
        } else if (error?.error) {
          setErrorMsg(error.error);
        } else {
          if (callback) {
            callback();
          }
          const toastAction = (
            <ToastAction
              altText="See"
              onClick={() =>
                router.push(`/exercises/single/${messages[0].content.id}`)
              }
            >
              See Exercise
            </ToastAction>
          );
          title
            ? toast({
                title: values.title,
                description: "Exercise updated successfully",
                variant: "success",
                action: toastAction,
              })
            : toast({
                title: values.title,
                description: "Exercise created successfully",
                variant: "success",
                action: toastAction,
              });
          router.push(`/exercises/single/${messages[0].content.id}`);
        }
      } catch (error) {
        if (error instanceof Error) {
          setErrorMsg(error?.message);
        } else {
          setErrorMsg("Something went wrong");
        }
      } finally {
        setIsLoading(false);
      }
    },
    [callback, method, path, router, session.data?.user?.token],
  );
  return (
    <Card className="max-w-6xl w-full sm:px-2 md:px-5 py-6">
      <CardTitle className="font-bold text-2xl text-center">{header}</CardTitle>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <TitleBodyForm
              control={form.control}
              titlePlaceholder="Bench Press"
              bodyPlaceholder="Body.."
            />
            <div className="flex items-center justify-between gap-5">
              <CustomCloudinaryWidget
                formKey="images"
                type="image"
                multiple={true}
                defaultValues={images}
              />
              <CustomCloudinaryWidget
                formKey="videos"
                type="video"
                multiple={true}
                defaultValues={images}
              />
            </div>

            <FormField
              control={form.control}
              name="muscleGroups"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Muscles</FormLabel>
                  <FormControl>
                    <MultipleSelector
                      value={field.value}
                      onChange={field.onChange}
                      defaultOptions={muscleGroupsOptions}
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
            <div className="flex items-center justify-center flex-col gap-4">
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
