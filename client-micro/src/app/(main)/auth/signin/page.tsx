"use client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { SignInType, signInSchema } from "@/types/forms";
import { signIn } from "next-auth/react";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Loader2 } from "lucide-react";

import OauthProviders from "@/app/(main)/auth/oauth-providers";
import { logError } from "@/app/(main)/auth/signin/actions";

export default function SingIn() {
  const form = useForm<SignInType>({
    resolver: zodResolver(signInSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });
  const router = useRouter();
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const onSubmit = async (values: SignInType) => {
    setIsLoading(true);
    if (errorMsg) setErrorMsg("");
    try {
      const result = await signIn("credentials", {
        redirect: false,
        email: values.email,
        password: values.password,
      });
      console.log("Sign-in result:", result);
      await logError("Sign-in result", result);
      setIsLoading(false);
      if (result?.error) {
        console.log("Authentication error:", result.error);
        await logError("Authentication error", result.error);
        setErrorMsg("Credentials Are Not Valid !");
      } else {
        console.log("Sign-in successful:", result);
        router.push("/");
      }
    } catch (error) {
      console.error("Unexpected error during sign-in:", error);
      await logError("Unexpected error during sign-in", error);
      setErrorMsg("An unexpected error occurred. Please try again later.");
      setIsLoading(false);
    }
  };

  return (
    <main className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
      <Card className="w-[500px]">
        <CardHeader>
          <CardTitle className="text-center">Sign In</CardTitle>
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
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input
                        type="password"
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
                <Button type="submit">Sign In</Button>
              ) : (
                <Button disabled>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Please wait
                </Button>
              )}
            </form>
          </Form>
          <OauthProviders />
        </CardContent>

        <CardFooter className="flex flex-col justify-start items-start gap-3">
          <div>
            <Link
              href="/auth/signup"
              className="text-sm italic hover:underline"
            >
              Don&apos;t have an account? Sign Up
            </Link>
          </div>
          <div>
            <Link
              href="/auth/forgot-password"
              className="text-sm italic hover:underline "
            >
              Forgot your password?
            </Link>
          </div>
        </CardFooter>
      </Card>
    </main>
  );
}
