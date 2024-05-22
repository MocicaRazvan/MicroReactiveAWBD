"use client";
import { useForm } from "react-hook-form";
import { emailSchema, EmailType } from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useState } from "react";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { fetchStream } from "@/hoooks/fetchStream";

export default function ForgotPasswordPage() {
  const form = useForm<EmailType>({
    resolver: zodResolver(emailSchema),
    defaultValues: {
      email: "",
    },
  });
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [show, setShow] = useState(false);

  const onSubmit = async ({ email }: EmailType) => {
    setShow(false);
    setIsLoading(true);
    const { messages, error } = await fetchStream({
      path: "/auth/resetPassword",
      method: "POST",
      body: {
        email,
      },
    });
    console.log(messages);
    console.log(error);
    setIsLoading(false);
    setShow(true);
  };

  return (
    <main className="w-full min-h-[calc(100vh-21rem)] flex items-center justify-center transition-all">
      <Card className="w-[500px]">
        <CardHeader>
          <CardTitle className="text-center">Send Email Reset</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="johndoe@gmail.com"
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
              {errorMsg && (
                <p className="font-medium text-destructive">{errorMsg}</p>
              )}
              {!isLoading ? (
                <Button type="submit">Send Request</Button>
              ) : (
                <Button disabled>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Please wait
                </Button>
              )}
            </form>
          </Form>
        </CardContent>
        <CardFooter className="mt-4 flex items-center justify-center">
          {show && (
            <p className="text-lg tracking-tighter font-bold text-center">
              Email was sent, please verify your inbox!
            </p>
          )}
        </CardFooter>
      </Card>
    </main>
  );
}
