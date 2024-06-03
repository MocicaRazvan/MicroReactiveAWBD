"use client";
import { useChatNotification } from "@/context/chat-message-notification-context";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Bell, ShoppingCartIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Fragment, useCallback } from "react";
import Link from "next/link";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { usePathname, useRouter } from "next/navigation";
import {
  ChatMessageNotificationResponse,
  ChatMessageResponse,
  ConversationUserResponse,
} from "@/types/dto";
import { Session } from "next-auth";

// import { useChatContext } from "@/context/chat-context";

interface NotificationPopProps {
  authUser: NonNullable<Session["user"]>;
}

export default function NotificationPop({ authUser }: NotificationPopProps) {
  const { getNotificationsGroupedBySender, getTotals } = useChatNotification();
  const totalNotifications = getTotals().total;
  const notificationsGroupedBySender = getNotificationsGroupedBySender();
  const stompClient = useStompClient();
  const router = useRouter();
  // const { setActiveChatId } = useChatContext();
  const { removeBySender } = useChatNotification();
  const pathName = usePathname();

  useSubscription(`/user/${authUser.email}/chat/changed`, (message) => {
    const newMessage = JSON.parse(message.body) as ConversationUserResponse;
    console.log("newMessage chat notif", newMessage);
    console.log("not path", pathName);

    if (newMessage.connectedChatRoom?.id) {
      // router.push(`/chat/?chatId=${newMessage.connectedChatRoom?.id}`);
      // setActiveChatId(newMessage.connectedChatRoom?.id);
      // router.push(`/chat`);
      const sender = newMessage.connectedChatRoom?.users.find(
        (user) => user.email !== authUser.email,
      );
      if (sender && stompClient && stompClient.connected) {
        removeBySender({
          stompClient,
          senderEmail: sender.email,
          receiverEmail: authUser.email,
        });
        router.push(`/chat/?chatId=${newMessage.connectedChatRoom?.id}`);
        // if (pathName === "/chat") {
        //   router.refresh();
        // } else {
        //   router.push(`/chat`);
        // }
      }
    }
  });

  const handleNavigation = useCallback(
    (senderNotif: ChatMessageNotificationResponse) => {
      if (stompClient && stompClient.connected) {
        stompClient.publish({
          destination: "/app/changeRoom",
          body: JSON.stringify({
            chatId: senderNotif.reference.id,
            userEmail: senderNotif.receiver.email,
          }),
        });
        // router.push(`/chat/?chatId=${senderNotif.reference.id}`);
        // simulate delay so that publish can be sent before navigation
        // setTimeout(() => {
        //   router.push(`/chat/?chatId=${senderNotif.reference.id}`);
        // }, 1000);
      }
    },
    [stompClient, router],
  );

  return (
    <DropdownMenu modal={false}>
      <DropdownMenuTrigger asChild>
        <div className="relative">
          <Button variant="outline" disabled={totalNotifications === 0}>
            <Bell />
          </Button>
          {totalNotifications > 0 && (
            <div className="absolute top-[-2px] right-[-10px] rounded-full w-7 h-7 bg-destructive flex items-center justify-center">
              <p>{totalNotifications}</p>
            </div>
          )}
        </div>
      </DropdownMenuTrigger>
      {totalNotifications > 0 && (
        <DropdownMenuContent className={"w-64"}>
          <DropdownMenuGroup>
            <ScrollArea
              className={cn(
                "w-full px-1",
                notificationsGroupedBySender.totalSenders < 5
                  ? `h-[calc(${notificationsGroupedBySender.totalSenders}rem+3.5rem)]`
                  : "h-60",
              )}
            >
              {Object.entries(notificationsGroupedBySender.notifications).map(
                ([sender, senderNotif], i) => (
                  <Fragment key={sender}>
                    <DropdownMenuItem onClick={(e) => e.preventDefault()}>
                      <div
                        // href={`/chat/?chatId=${senderNotif[0].reference.id}`}
                        onClick={() => handleNavigation(senderNotif[0])}
                        className=" transitiion-all font-bold hover:underline cursor-pointer hover:bg-accent hover:text-accent-foreground"
                      >
                        <p>
                          You have {senderNotif.length} from {sender}
                        </p>
                      </div>
                    </DropdownMenuItem>
                    {i !== notificationsGroupedBySender.totalSenders - 1 && (
                      <DropdownMenuSeparator />
                    )}
                  </Fragment>
                ),
              )}
            </ScrollArea>
          </DropdownMenuGroup>
        </DropdownMenuContent>
      )}
    </DropdownMenu>
  );
}
