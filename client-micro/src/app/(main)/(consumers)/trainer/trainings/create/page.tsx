import { authOptions } from "@/app/api/auth/[...nextauth]/auth-options";
import TrainingsForm from "@/components/forms/trainings-form";
import { Loader } from "lucide-react";
import { getServerSession } from "next-auth";
import { Suspense } from "react";

export default async function CreateTraining() {
  const session = await getServerSession(authOptions);

  if (!session?.user) return null;

  return (
    <main className="flex items-center justify-center px-6 py-10">
      <Suspense fallback={<Loader />}>
        <TrainingsForm
          path="/trainings/create"
          method="POST"
          redirect={true}
          callback={async () => {
            "use server";
          }}
          authUser={session.user}
        />
      </Suspense>
    </main>
  );
}
