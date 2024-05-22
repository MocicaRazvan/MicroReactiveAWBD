"use client";

import { useCallback, useState } from "react";

import { Card, CardContent, CardTitle } from "@/components/ui/card";
import { useForm } from "react-hook-form";
import {
  BasicFormProps,
  PostType,
  postSchema,
  tagsOptions,
} from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import { TitleBodyForm } from "@/components/forms/title-body";
import { Button } from "@/components/ui/button";
import CustomCloudinaryWidget from "@/components/forms/cloudinary-widget";
import MultipleSelector from "@/components/ui/multiple-selector";
import { Loader2 } from "lucide-react";
import { fetchStream } from "@/hoooks/fetchStream";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { CustomEntityModel, PostResponse } from "@/types/dto";
import { toast } from "@/components/ui/use-toast";
import { ToastAction } from "@/components/ui/toast";

interface Props extends Partial<PostType>, BasicFormProps {}

export default function PostForm({
  body = "",
  title = "",
  images = [],
  tags = [],
  path,
  method,
  submitText = "Create Post",
  header = "Create Post",
}: Props) {
  const session = useSession();
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<PostType>({
    resolver: zodResolver(postSchema),
    defaultValues: {
      title,
      body,
      images,
      tags,
    },
  });

  const onSubmit = useCallback(
    async (values: PostType) => {
      if (!session.data?.user?.token) return;
      setIsLoading(true);
      const postBody = { ...values, tags: values.tags.map((tag) => tag.value) };
      try {
        const { messages, error } = await fetchStream<
          CustomEntityModel<PostResponse>
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
          const toastAction = (
            <ToastAction
              altText="See"
              onClick={() =>
                router.push(`/posts/single/${messages[0].content.id}`)
              }
            >
              See Post
            </ToastAction>
          );
          title
            ? toast({
                title: values.title,
                description: "Post updated successfully",
                variant: "success",
                action: toastAction,
              })
            : toast({
                title: values.title,
                description: "Post created successfully",
                variant: "success",
                action: toastAction,
              });
          router.push(`/posts/single/${messages[0].content.id}`);
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
    [method, path, router, session.data?.user?.token],
  );
  if (!tagsOptions) return null;
  if (!session.data?.user?.token) return null;
  console.log(tagsOptions);
  return (
    <Card className="max-w-6xl w-full sm:px-2 md:px-5 py-6">
      <CardTitle className="font-bold text-2xl text-center">{header}</CardTitle>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <TitleBodyForm
              control={form.control}
              titlePlaceholder="Best splits"
              bodyPlaceholder="Body.."
            />
            <div
              onClick={(e) => {
                e.stopPropagation();
              }}
            >
              <CustomCloudinaryWidget
                formKey="images"
                type="image"
                multiple={true}
                defaultValues={images}
              />
            </div>

            <FormField
              control={form.control}
              name="tags"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Tags</FormLabel>
                  <FormControl>
                    <MultipleSelector
                      value={field.value}
                      onChange={field.onChange}
                      defaultOptions={tagsOptions}
                      placeholder="Select the tags for the post"
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
