"use client";

import Image from "next/image";
import picture1 from "../../../public/header/Picture1.jpg";
import picture2 from "../../../public/header/Picture2.jpg";
import picture3 from "../../../public/header/Picture3.jpg";
import picture4 from "../../../public/header/Picture4.jpg";
import picture5 from "../../../public/header/Picture5.jpg";
import picture6 from "../../../public/header/Picture6.jpg";
import picture7 from "../../../public/header/Picture7.jpg";
import {
  useScroll,
  useTransform,
  motion,
  useInView,
  useAnimation,
  useMotionValueEvent,
  useAnimate,
} from "framer-motion";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";

export default function HomeHeader() {
  const container = useRef(null);
  const animationControl = useAnimation();

  const { scrollYProgress } = useScroll({
    target: container,
    offset: ["start start", "end end"],
  });
  const [hookedYPosition, setHookedYPosition] = useState(0);
  useMotionValueEvent(scrollYProgress, "change", setHookedYPosition);

  const scale4 = useTransform(scrollYProgress, [0, 1], [1, 4]);
  const scale5 = useTransform(scrollYProgress, [0, 1], [1, 5]);
  const scale6 = useTransform(scrollYProgress, [0, 1], [1, 6]);
  const scale8 = useTransform(scrollYProgress, [0, 1], [1, 8]);
  const scale9 = useTransform(scrollYProgress, [0, 1], [1, 9]);
  const fontSize = useTransform(scrollYProgress, [0, 1], ["0.8rem", "2.3rem"]);

  const pictures = useMemo(
    () => [
      {
        src: picture1,
        scale: scale4,
        style: { width: "25vw", height: "25vh" },
      },
      {
        src: picture2,
        scale: scale5,
        style: {
          width: "35vw",
          height: "30vh",
          top: "-30vh",
          left: "5vw",
        },
      },
      {
        src: picture3,
        scale: scale6,
        style: {
          width: "20vw",
          height: "45vh",
          top: "-10vh",
          left: "-25vw",
        },
      },
      {
        src: picture4,
        scale: scale5,
        style: {
          width: "25vw",
          height: "25vh",
          left: "27.5vw",
        },
      },
      {
        src: picture5,
        scale: scale6,
        style: {
          width: "20vw",
          height: "25vh",
          top: "27.5vh",
          left: "5vw",
        },
      },
      {
        src: picture6,
        scale: scale8,
        style: {
          width: "30vw",
          height: "25vh",
          top: "27.5vh",
          left: "-22.5vw",
        },
      },
      {
        src: picture7,
        scale: scale9,
        style: {
          width: "15vw",
          height: "15vh",
          top: "22.5vh",
          left: "25vw",
        },
      },
    ],
    [scale4, scale5, scale6, scale8, scale9],
  );

  useEffect(() => {
    if (hookedYPosition > 0.55) {
      animationControl.start({
        x: "-50%",
        opacity: 1,
        transition: {
          duration: 1,
          type: "spring",
          stiffness: 90,
          damping: 12,
        },
      });
    } else {
      animationControl.start({
        x: "-150%",
        opacity: 0,
        transition: {
          duration: 1,
          type: "spring",
          stiffness: 90,
          damping: 12,
        },
      });
    }
  }, [animationControl, hookedYPosition, scrollYProgress]);

  return (
    <div ref={container} className="h-[300vh]  relative">
      <div className="sticky top-0 h-[100vh] overflow-hidden">
        {pictures.map(({ src, scale, style }, index) => (
          <motion.div
            style={{ scale }}
            key={src + index.toString()}
            className="w-full h-full absolute top-0 flex items-center justify-center"
          >
            <div className="w-[25vw] h-[25vh] relative" style={{ ...style }}>
              <Image
                src={src}
                fill
                alt="image"
                placeholder="blur"
                className="object-cover"
              />
              {index === 0 && (
                <motion.div
                  className="absolute left-[50%] top-[50%] px-4 py-2 w-full"
                  initial={{ x: "-150%", opacity: 0 }}
                  animate={animationControl}
                >
                  <motion.h1
                    className=" hidden md:block tracking-tighter font-bold w-full text-center invert dark:invert-0"
                    style={{ fontSize }}
                  >
                    Are you ready?
                  </motion.h1>
                </motion.div>
              )}
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
