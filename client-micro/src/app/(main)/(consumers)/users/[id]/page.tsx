"use client";

import { Badge } from "@/components/ui/badge";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import { CustomEntityModel, UserDto } from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useSession } from "next-auth/react";
import { useParams, useRouter } from "next/navigation";
import { UpdateAccordion } from "./update-accordion";
import { useEffect, useState } from "react";
import { AlertDialogMakeTrainer } from "./make-trainer-alert";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { fetchStream } from "@/hoooks/fetchStream";
import { cn } from "@/lib/utils";

export default function UserPage() {
  const [stateUser, setStateUser] = useState<UserDto>();
  const [refresh, setRefresh] = useState(false);
  const { id } = useParams();
  const { messages } = useFetchStream<CustomEntityModel<UserDto>, BaseError>({
    path: `/users/${id}`,
    method: "GET",
    authToken: true,
  });
  const [isLoading, setIsLoading] = useState(false);
  const [respMsg, setRespMsg] = useState<{
    message: string;
    isError: boolean;
    isVisible: boolean;
  }>({
    message: "",
    isError: false,
    isVisible: false,
  });
  const session = useSession();
  const authUser = session.data?.user;
  const user = messages[0]?.content;

  const isOwner = authUser?.email === user?.email;
  const isUser = user?.role === "ROLE_USER";
  const isAuthAdmin = authUser?.role === "ROLE_ADMIN";

  useEffect(() => {
    if (user) {
      setStateUser(user);
    }
  }, [JSON.stringify(messages)]);

  const handleVerify = async () => {
    if (!user?.email) return;
    setIsLoading(true);
    const { messages, error } = await fetchStream({
      path: "/auth/verifyEmail",
      method: "POST",
      body: {
        email: user?.email,
      },
    });
    if (error) {
      setRespMsg({
        message: "Something went wrong please try again",
        isError: true,
        isVisible: true,
      });
    } else {
      setRespMsg({
        message: "Email for verification was sent, please verify your inbox!",
        isError: false,
        isVisible: true,
      });
    }
    setTimeout(
      () =>
        setRespMsg({
          message: "",
          isError: false,
          isVisible: false,
        }),
      5500,
    );
    setIsLoading(false);
  };

  console.table(user);

  if (!user || !session.data?.user)
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );
  return (
    <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all mt-4">
      <div className=" w-full mx-2 md:mx-0 md:w-1/2  border rounded-xl px-6 py-8">
        <div className="flex items-center justify-center gap-4">
          {stateUser?.image && (
            <Avatar>
              <AvatarImage
                src={stateUser?.image || ""}
                alt={stateUser?.email}
                className="w-20 h-20"
              />
              {/* <AvatarFallback>{stateUser?.email}</AvatarFallback> */}
            </Avatar>
          )}
          <h3 className="text-xl font-bold text-center">{stateUser?.email}</h3>
        </div>
        <div className="flex justify-between w-2/3 mx-auto items-center mt-10">
          <div className="flex flex-col items-center justify-center gap-2 w-full">
            <p className="text-lg w-full">
              First Name:{" "}
              <span className="font-bold ml-4">{stateUser?.firstName}</span>
            </p>
            <p className="text-lg w-full">
              Last Name:{" "}
              <span className="font-bold ml-4">{stateUser?.lastName}</span>
            </p>
          </div>

          <Badge
            variant={
              stateUser?.role === "ROLE_ADMIN"
                ? "destructive"
                : stateUser?.role === "ROLE_TRAINER"
                  ? "default"
                  : "secondary"
            }
            className="text-lg"
          >
            {stateUser?.role}
          </Badge>
        </div>
        <div className="flex items-center justify-center mt-8 w-full">
          {/* make it not a dialog, not working with cloudinary!!!!!!! */}
          {isAuthAdmin && isUser && (
            <AlertDialogMakeTrainer
              user={user}
              token={session?.data?.user?.token}
              setStateUser={setStateUser}
            />
          )}
        </div>
        {isOwner && (
          <UpdateAccordion
            user={user}
            token={session?.data?.user?.token}
            setStateUser={setStateUser}
          />
        )}
        {isOwner && user.provider === "LOCAL" && !user.emailVerified && (
          <div className="mt-10 flex flex-col items-center justify-center gap-6">
            {!respMsg.isVisible ? (
              <Button
                size="lg"
                disabled={isLoading}
                onClick={() => handleVerify()}
              >
                {isLoading ? (
                  <Loader2 className=" h-4 w-4 animate-spin" />
                ) : (
                  "Verify Your Email"
                )}
              </Button>
            ) : (
              <p
                className={cn(
                  "text-lg bold tracking-tighter",
                  respMsg.isError && "text-destructive",
                )}
              >
                {respMsg.message}
              </p>
            )}
          </div>
        )}
      </div>
    </section>
  );
}
