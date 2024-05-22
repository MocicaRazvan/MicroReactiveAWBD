import { AlertDialogDelete } from "@/components/dialogs/delete-model";
import { CommentResponse } from "@/types/dto";

interface Props {
  comment: CommentResponse;
  token: string | undefined;
  callBack: () => void;
  anchor?: React.ReactNode;
}

export default function AlertDialogDeleteComment({
  comment,
  token,
  callBack,
  anchor
}: Props) {
  return (
    <AlertDialogDelete
      callBack={callBack}
      model={comment}
      token={token}
      path="comments"
      title="comment"
      anchor={anchor}
    />
  );
}
