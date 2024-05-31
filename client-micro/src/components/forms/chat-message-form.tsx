"use client";
import { useForm } from "react-hook-form";
import {
  conversationMessageSchema,
  ConversationMessageType,
} from "@/types/forms";
import { zodResolver } from "@hookform/resolvers/zod";
import { useCallback, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import Editor from "@/components/editor/editor";
import { useStompClient } from "react-stomp-hooks";
import {
  ChatMessageNotificationBody,
  ConversationUserResponse,
} from "@/types/dto";

interface ChatMessageFormProps {
  chatRoomId: number;
  sender: ConversationUserResponse;
  receiver: ConversationUserResponse;
}

export default function ChatMessageForm({
  sender,
  receiver,
  chatRoomId,
}: ChatMessageFormProps) {
  const stompClient = useStompClient();
  const [editorKey, setEditorKey] = useState(Math.random());
  const form = useForm<ConversationMessageType>({
    resolver: zodResolver(conversationMessageSchema),
    defaultValues: {
      content: "",
    },
  });

  const onSubmit = useCallback(
    async ({ content }: ConversationMessageType) => {
      form.setValue("content", "");
      setEditorKey(Math.random());
      if (stompClient && content && stompClient.connected) {
        stompClient.publish({
          destination: "/app/sendMessage",
          body: JSON.stringify({
            content,
            chatRoomId,
            senderEmail: sender.email,
            receiverEmail: receiver.email,
          }),
        });
        console.log("sending notification");
        console.log("receiverchatId", receiver.connectedChatRoom?.id);
        console.log("connectedChatRoom", chatRoomId);
        if (
          !receiver.connectedChatRoom?.id ||
          receiver.connectedChatRoom.id !== chatRoomId
        ) {
          const body: ChatMessageNotificationBody = {
            senderEmail: sender.email,
            receiverEmail: receiver.email,
            type: "NEW_MESSAGE",
            referenceId: chatRoomId,
            content: "New message from " + sender.email,
            extraLink: "",
          };
          stompClient.publish({
            destination: "/app/chatMessageNotification/sendNotification",
            body: JSON.stringify(body),
          });
        }
      }
    },
    [chatRoomId, form, receiver, sender, stompClient],
  );
  return (
    <div className="py-2 border-t flex items-center px-5">
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className={" w-full h-full"}
        >
          <FormField
            control={form.control}
            name="content"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="sr-only">Content</FormLabel>
                <Editor
                  descritpion={field.value as string}
                  onChange={field.onChange}
                  placeholder={"Type a message"}
                  key={editorKey}
                />
                <FormMessage />
              </FormItem>
            )}
          />
          <Button
            className="ml-4 mt-2"
            type="submit"
            size="lg"
            disabled={!form.formState.isDirty}
          >
            Send
          </Button>
        </form>
      </Form>
    </div>
  );
}
