import { AlertDialogApprove } from "@/components/dialogs/approve-model";

import { PostResponse } from "@/types/dto";

interface Props {
  post: PostResponse;
  token: string | undefined;
  callBack: () => void;
}

export default function AlertDialogApprovePost({
  post,
  token,
  callBack,
}: Props) {
  return (
    <AlertDialogApprove
      callBack={callBack}
      model={post}
      token={token}
      path="posts"
      title="post"
    />
  );
}
