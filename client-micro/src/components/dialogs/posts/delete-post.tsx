import { AlertDialogDelete } from "@/components/dialogs/delete-model";
import { PostResponse } from "@/types/dto";

interface Props {
  post: PostResponse;
  token: string | undefined;
  callBack: () => void;
}

export default function AlertDialogDeletePost({
  post,
  token,
  callBack,
}: Props) {
  return (
    <AlertDialogDelete
      callBack={callBack}
      model={post}
      token={token}
      path="posts"
      title="post"
    />
  );
}
