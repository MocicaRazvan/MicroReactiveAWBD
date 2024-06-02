import { Client } from "@stomp/stompjs";

export const publishWithConfirmation = (
  stompClient: Client,
  destination: string,
  body: string,
): Promise<void> => {
  return new Promise((resolve, reject) => {
    try {
      if (stompClient && stompClient.connected) {
        console.log("Publishing message to destination:", destination);
        stompClient.publish({
          destination,
          body,
        });
        // Resolve the promise after a slight delay
        setTimeout(() => {
          console.log("Publish successful, resolving promise.");
          resolve();
        }, 100);
      } else {
        console.error("Stomp client not connected");
        reject(new Error("Stomp client not connected"));
      }
    } catch (error) {
      console.error("Error during publish:", error);
      reject(error);
    }
  });
};
