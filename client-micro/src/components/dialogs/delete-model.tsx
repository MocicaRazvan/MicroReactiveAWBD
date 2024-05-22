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
import { TitleBodyUser } from "@/types/dto";
import { toast } from "@/components/ui/use-toast";

interface Props {
  model: TitleBodyUser;
  token: string | undefined;
  path: string;
  title?: string;
  callBack: () => void;
  anchor?: React.ReactNode;
}

export function AlertDialogDelete({
  model,
  token,
  callBack,
  path,
  title = path,
  anchor,
}: Props) {
  const deleteModel = async () => {
    if (token === undefined) return;
    try {
      const resp = await fetchStream({
        path: `/${path}/delete/${model.id}`,
        method: "DELETE",
        token,
      });

      if (resp.error) {
        console.log(resp.error);
      } else {
        toast({
          title: model.title,
          description: "Deleted",
          variant: "destructive",
        });
        callBack?.();
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        {anchor ? (
          anchor
        ) : (
          <Button
            variant="outline"
            className="border-destructive text-destructive"
          >
            Delete
          </Button>
        )}
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will delete {title} and it will
            be lost forever.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction asChild onClick={deleteModel}>
            <Button variant="destructive">Continue</Button>
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
