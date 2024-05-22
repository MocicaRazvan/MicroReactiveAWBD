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
import { Approve, PostResponse, UserDto } from "@/types/dto";
import { useRouter } from "next/navigation";
import { Dispatch, SetStateAction } from "react";
import { toast } from "@/components/ui/use-toast";

interface Props {
  model: Approve;
  token: string | undefined;
  path: string;
  title?: string;
  callBack: () => void;
}

export function AlertDialogApprove({
  model,
  token,
  callBack,
  path,
  title = path,
}: Props) {
  const approve = async () => {
    if (token === undefined) return;
    try {
      const resp = await fetchStream({
        path: `/${path}/admin/approve/${model.id}`,
        method: "PATCH",
        token,
      });

      if (resp.error) {
        console.log(resp.error);
      } else {
        callBack();
        toast({
          title: model.title,
          description: "Approved",
          variant: "success",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="outline"
          className="border-destructive text-destructive"
        >
          Approve
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will make the {title} approved so
            it can be seen by all users!
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction asChild onClick={approve}>
            <Button variant="destructive">Continue</Button>
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
