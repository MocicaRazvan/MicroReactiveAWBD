import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { fetchStream } from "@/hoooks/fetchStream";
import { UserDto } from "@/types/dto";
import { Dispatch, SetStateAction } from "react";

interface Props {
  user: UserDto;
  token: string;
  setStateUser?: Dispatch<SetStateAction<UserDto | undefined>>;
}

export function AlertDialogMakeTrainer({ user, token, setStateUser }: Props) {
  const makeTrainer = async () => {
    console.log("object");
    try {
      const resp = await fetchStream({
        path: `/users/admin/${user.id}`,
        method: "PATCH",
        token,
      });

      if (resp.error) {
        console.log(resp.error);
      } else {
        if (setStateUser) {
          setStateUser((prev) =>
            prev ? { ...prev, role: "ROLE_TRAINER" } : prev
          );
        }
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button variant="destructive">Make Trainer</Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will permanently make a
            potentially dangerous user a trainer.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction asChild onClick={makeTrainer}>
            <Button variant="destructive">Continue</Button>
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
