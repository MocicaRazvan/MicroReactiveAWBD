"use client";

import { CommentType, commnetBodySchema } from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Accordion,
  AccordionTrigger,
  AccordionContent,
  AccordionItem,
} from "@/components/ui/accordion";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { fetchStream } from "@/hoooks/fetchStream";
import Editor from "@/components/editor/editor";
interface Props {
  title?: string;
  body?: string;
  postId: number;
  token: string;
  refetch: () => void;
}

export default function CommentAcc({
  title = "",
  body = "",
  postId,
  token,
  refetch,
}: Props) {
  const form = useForm<CommentType>({
    resolver: zodResolver(commnetBodySchema),
    defaultValues: {
      body,
      title,
    },
  });
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const onSubmit = useCallback(
    async (body: CommentType) => {
      if (!token) return;
      setIsLoading(true);
      try {
        const { messages, error, isFinished } = await fetchStream({
          path: `/comments/create/${postId}`,
          method: "POST",
          body,
          token,
        });
        if (error) {
          setErrorMsg(error.message);
        } else {
          refetch();
        }
      } catch (error) {
        console.log(error);
      } finally {
        setIsLoading(false);
      }
    },
    [postId, refetch, token]
  );

  return (
    <Accordion type="single" collapsible className="w-full">
      <AccordionItem value="item-1">
        <AccordionTrigger>Add comment</AccordionTrigger>
        <AccordionContent className=" w-full flex items-center justify-center mx-auto ">
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(onSubmit)}
              className="space-y-8 w-full px-10 pt-1"
            >
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="How awesome..."
                        {...field}
                        onFocus={() => {
                          if (errorMsg) setErrorMsg("");
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="body"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Body</FormLabel>
                    <FormControl>
                      <div
                        onFocus={() => {
                          if (errorMsg) setErrorMsg("");
                        }}
                      >
                        <Editor
                          descritpion={field.value as string}
                          onChange={field.onChange}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {errorMsg && (
                <p className="font-medium text-destructive">{errorMsg}</p>
              )}
              <div className="w-full flex items-center justify-center">
                {!isLoading ? (
                  <Button type="submit" className="mx-auto">
                    Create
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
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
}
