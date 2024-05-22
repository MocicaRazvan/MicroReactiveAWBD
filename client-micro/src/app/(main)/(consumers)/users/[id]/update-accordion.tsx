"use client";
import { Button } from "@/components/ui/button";

import { zodResolver } from "@hookform/resolvers/zod";
import { set, useForm } from "react-hook-form";

import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import {
  SignInType,
  UpdateProfileType,
  signInSchema,
  updateProfileSchema,
} from "@/types/forms";
import { Dispatch, SetStateAction, useState } from "react";
import { useRouter } from "next/navigation";
import { Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { fetchStream } from "@/hoooks/fetchStream";
import { UserDto } from "@/types/dto";
import CustomCloudinaryWidget from "@/components/forms/cloudinary-widget";
import {
  Accordion,
  AccordionTrigger,
  AccordionContent,
  AccordionItem,
} from "@/components/ui/accordion";
import { useSession } from "next-auth/react";
import { toast } from "@/components/ui/use-toast";

interface Props {
  user: UserDto;
  token: string;
  setStateUser?: Dispatch<SetStateAction<UserDto | undefined>>;
}

export function UpdateAccordion({
  user: { firstName, lastName, image, id },
  token,
  setStateUser,
}: Props) {
  const form = useForm<UpdateProfileType>({
    resolver: zodResolver(updateProfileSchema),
    defaultValues: {
      firstName,
      lastName,
      image: image ? [image] : [],
    },
  });
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [open, setOpen] = useState(true);
  const session = useSession();

  console.log(session);

  const onSubmit = async (values: UpdateProfileType) => {
    if (!session) return;
    setIsLoading(true);
    const body = { ...values, image: values?.image?.[0] || "" };
    try {
      const { messages, error, isFinished } = await fetchStream({
        path: `/users/${id}`,
        method: "PUT",
        body,
        token,
      });
      if (error) {
        setErrorMsg(error.message);
      } else {
        console.log(messages);
        await session.update({
          ...session,
          data: { ...session.data, user: { ...session.data?.user, ...body } },
        });
        toast({
          description: "Profile updated successfully",
          variant: "success",
        });
        router.refresh();
        router.push("/users/" + id);
      }
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
      if (setStateUser) {
        setStateUser((prev) => (prev ? { ...prev, ...body } : prev));
      }
    }
  };

  return (
    <Accordion type="single" collapsible className="w-full">
      <AccordionItem value="item-1">
        <AccordionTrigger>Edit Profile</AccordionTrigger>
        <AccordionContent className="sm:max-w-[425px] flex items-center justify-center mx-auto">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>First Name</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="John"
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
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Last Name</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Doe"
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
              <div
                className="flex items-center justify-center my-10 z-[9999]"
                onClick={(e) => e.stopPropagation()}
              >
                <CustomCloudinaryWidget
                  formKey="image"
                  type="image"
                  multiple={false}
                  defaultValues={image ? [image] : []}
                />
              </div>
              {errorMsg && (
                <p className="font-medium text-destructive">{errorMsg}</p>
              )}
              <div className="w-full flex items-center justify-center">
                {!isLoading ? (
                  <Button
                    type="submit"
                    variant="default"
                    className="mx-auto"
                    size="lg"
                  >
                    Update
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
