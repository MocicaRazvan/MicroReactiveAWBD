import HomeTitle from "@/components/home/about";
import HomeCard from "@/components/home/card";
import HomeHeader from "@/components/home/header";
import MoreHome from "@/components/home/more";
import { Dumbbell, Newspaper, User2 } from "lucide-react";
import Timeline from "@/components/home/timeline";

export default function Home() {
  const cards = [
    {
      title: "Register",
      description:
        "Register to get access to all the features of our platform, and become a part of our community.",
      href: "/auth/signin",
      icon: <User2 size={64} className="w-full mx-auto" />,
    },
    {
      title: "Read Posts",
      description:
        "Read posts to gain knowledge and insights about different topics written by professionals.",
      href: "/posts/approved",
      icon: <Newspaper size={64} className="w-full mx-auto" />,
    },
    {
      title: "Buy Trainings",
      description:
        "Buy trainings to improve get the full experience of our platform. We offer a wide range of trainings.",
      href: "/trainings/approved",
      icon: <Dumbbell size={64} className="w-full mx-auto" />,
    },
  ];

  return (
    <main className="space-y-5 ">
      <HomeHeader />
      <div className="h-8" />
      <div className="flex flex-wrap items-center justify-center gap-10 mt-15 overflow-hidden">
        {cards.map((item, i) => (
          <HomeCard key={i} index={i} {...item} />
        ))}
      </div>
      <div className="mt-16">
        <HomeTitle />
      </div>
      <div className="mt-10">
        <MoreHome />
      </div>
      <div className="mt-10">
        <Timeline />
      </div>
    </main>
  );
}
