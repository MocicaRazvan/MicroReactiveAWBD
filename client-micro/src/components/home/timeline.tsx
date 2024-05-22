"use client";
import {
  VerticalTimeline,
  VerticalTimelineElement,
} from "react-vertical-timeline-component";
import "react-vertical-timeline-component/style.min.css";
import { BookIcon, Briefcase, PersonStanding } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useInView } from "framer-motion";
import { cn } from "@/lib/utils";

const timeLine = [
  {
    date: "Prepare",
    title: "Be aware",
    text: `Realize your problems and target them. Don't be afraid to make changes that make you uncomfortable.`,
    icon: <BookIcon />,
  },
  {
    date: "",
    title: "Get comfortable with being afraid",
    text: `It's normal to feel overwhelmed and insecure about getting started. Don't let these feelings stop you.`,
    icon: <BookIcon />,
  },
  {
    date: "",
    title: "Make a plan",
    text: `Plan ahead and be very structured. Make realistic targets and do things your way. Don't let others decide your journey.`,
    icon: <BookIcon />,
  },
  {
    date: "Start working",
    title: "Step by step",
    text: `Be patient and know that life is a marathon, not a sprint. Don't seek quick fixes; be happy with every little progress.`,
    icon: <PersonStanding />,
  },
  {
    date: "",
    title: "Work your ass off",
    text: `Working hard pays off. Every change requires sacrifices, so be prepared to make some. In the end, you'll thank yourself.`,
    icon: <PersonStanding />,
  },
  {
    date: "",
    title: "Become comfortable with your new life",
    text: `Over time, everything you thought was too hard will become trivial. When this happens, pat yourself on the back and see your progress.`,
    icon: <PersonStanding />,
  },
  {
    date: "Stay consistent",
    title: "Don't let the results slow you down",
    text: `Don't get too comfortable and think you can't lose what you've achieved. This is where the difference between the sprint and the marathon is made.`,
    icon: <Briefcase />,
  },
];
export default function Timeline() {
  return (
    <div className="w-full h-full">
      <VerticalTimeline lineColor="hsl(var(--foreground))">
        {timeLine.map(({ date, title, text, icon }, i) => (
          <TimelineElement
            key={title}
            date={date}
            title={title}
            text={text}
            icon={icon}
            index={i}
          />
        ))}
      </VerticalTimeline>
    </div>
  );
}

function TimelineElement({
  date,
  title,
  text,
  icon,
  index,
}: (typeof timeLine)[number] & { index: number }) {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: false });
  return (
    <div ref={ref}>
      <VerticalTimelineElement
        className={cn("vertical-timeline-element--work", "!mb-10")}
        date={date}
        iconStyle={{
          boxShadow: "none",
          backgroundColor: "hsl(var(--foreground))",
          color: "hsl(var(--background))",
        }}
        icon={icon}
        visible={isInView}
        position={index % 2 === 0 ? "left" : "right"}
        contentArrowStyle={{
          borderRight: "7px solid  hsl(var(--foreground))",
        }}
        contentStyle={{
          backgroundColor: "hsl(var(--border))",
          color: "hsl(var(--card-foreground))",
          borderRadius: "var(--radius)",
          fontSize: "1.2rem",
        }}
        textClassName={"!text-foreground !font-bold !text-xl"}
        dateClassName={"!text-foreground !font-bold !text-xl"}
      >
        <h3 className="!text-foreground">{title}</h3>
        <p className="!text-foreground">{text}</p>
      </VerticalTimelineElement>
    </div>
  );
}
