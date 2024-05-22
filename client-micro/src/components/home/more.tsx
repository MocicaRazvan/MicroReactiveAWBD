"use client";

import Image from "next/image";
import picture6 from "../../../public/header/Picture6.jpg";
import { useEffect, useRef, useState } from "react";
import { useScroll, useTransform, motion } from "framer-motion";
import { Dumbbell } from "lucide-react";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";
import Timeline from "@/components/home/timeline";

export default function MoreHome() {
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  const container = useRef(null);
  const router = useRouter();
  const { scrollYProgress } = useScroll({
    target: container,
    offset: ["start end", "end start"],
  });

  const ctY = useTransform(scrollYProgress, [0, 1], ["0%", "-60%"]);
  const bgY = useTransform(scrollYProgress, [0, 1], ["-10%", "30%"]);

  if (!isMounted) return null;

  return (
    <div
      ref={container}
      className="relative w-full min-h-[100vh] mt-20 overflow-hidden flex items-center justify-center"
    >
      <motion.div
        className="absolute inset-0 h-full w-full grid place-items-center z-0"
        style={{ y: bgY }}
      >
        <Image
          src={picture6}
          layout="fill"
          objectFit="cover"
          alt="parallax background"
        />
      </motion.div>

      <motion.div
        className="relative z-10 w-3/4 lg:w-1/2 text-4xl text-center bg-white bg-opacity-20 p-8 rounded-lg shadow-lg backdrop-blur-lg mt-10"
        style={{ y: ctY }}
        initial={{ opacity: 0, scale: 0 }}
        whileInView={{ opacity: 1, scale: 1 }}
        transition={{
          type: "spring",
          stiffness: 85,
          delay: 0.125,
          duration: 0.75,
          damping: 10,
        }}
      >
        <Button
          variant="default"
          className="text-2xl px-5 py-3"
          onClick={() => {
            router.push("/auth/signin");
          }}
        >
          Don&apos;t wait, start now!
        </Button>
        <p className="mt-6 italic font-bold">
          Be part of a strong community{" "}
          <Dumbbell
            size={34}
            className="inline ml-1 text-destructive font-bold"
          />
        </p>
      </motion.div>
    </div>
  );
}
