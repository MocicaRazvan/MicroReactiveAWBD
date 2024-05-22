"use client";

import { AnimatedText } from "../ui/animated-text";
import { ReactElement } from "react";

export default function HomeTitle() {
  const text: ReactElement = (
    <div className="prose max-w-none mx-auto my-8 px-4 py-6 text-foreground text-xl">
      <ul className="text-xl md:text-4xl leading-relaxed list-disc pl-5 md:space-y-6 list-outside">
        <li className="text-lg md:text-xl">
          At our wellness and fitness platform, we believe in empowering
          individuals to achieve their health goals by providing comprehensive
          and holistic solutions. Our diverse team of expert trainers brings
          together a wealth of experience and knowledge, offering personalized
          guidance tailored to your unique fitness journey. Whether you&apos;re
          looking to shed those extra pounds, build muscle, improve flexibility,
          or simply cultivate a healthier lifestyle, our trainers are committed
          to helping you reach your goals. We&apos;re here to support you every
          step of the way, from setting realistic targets to tracking your
          progress and celebrating your achievements.
        </li>
        <li className="text-lg md:text-xl">
          What sets us apart is our inclusive community where trainers and
          clients alike come together to support and motivate each other. Our
          trainers regularly contribute to our blog, sharing valuable insights,
          practical tips, and innovative workout routines to keep you inspired
          and informed. We also offer customizable training packages that allow
          you to choose a plan that fits your schedule, budget, and fitness
          level. From one-on-one coaching to group classes and on-demand
          training, you&apos;ll find everything you need to start your fitness
          journey or elevate your current regime. We believe that fitness should
          be fun, engaging, and accessible to everyone, regardless of the
          obstacles they face.
        </li>
        <li className="text-lg md:text-xl">
          But it&apos;s not just about workouts. Our platform also offers
          nutritional advice, mindfulness practices, and a wealth of resources
          to help you cultivate a balanced lifestyle. We understand that true
          wellness is a blend of physical, mental, and emotional health, and
          we&apos;re here to support you every step of the way. Whether
          you&apos;re looking to lose weight, gain muscle, improve your
          flexibility, or simply boost your overall well-being, we have the
          tools, knowledge, and expertise to help you achieve your goals. Our
          trainers are passionate about empowering you to take control of your
          health and transform your life for the better.
        </li>
        <li className="text-lg md:text-xl">
          Choose us because we don&apos;t believe in a one-size-fits-all
          approach. We believe in personalized, holistic fitness that empowers
          you to become the best version of yourself. Join our community today
          and embark on a transformative journey towards lasting wellness.
          We&apos;re here to support you every step of the way, from setting
          realistic targets to tracking your progress and celebrating your
          achievements.
        </li>
      </ul>
    </div>
  );

  return (
    <div className="space-y-20 mt-20">
      <AnimatedText
        text="Why to chose us..."
        className="md:text-[4rem] text-4xl tracking-tighter font-bold text-center"
        // once={true}
        animation={{
          hidden: { opacity: 0, y: 20 },
          visible: { opacity: 1, y: 0, transition: { staggerChildren: 0.1 } },
        }}
      />
      <div className=" px-20 hidden md:block">
        <AnimatedText
          text={text}
          // once={true}
          className="text-center text-2xl leading-7 "
          animation={{
            hidden: { opacity: 0 },
            visible: { opacity: 1, transition: { duration: 1 } },
          }}
          split={false}
          amount={0.35}
        />
      </div>
      <div className=" px-20 block md:hidden">
        <AnimatedText
          text={text}
          // once={true}
          className="text-center text-2xl leading-7 "
          animation={{
            hidden: { opacity: 0 },
            visible: { opacity: 1, transition: { duration: 1 } },
          }}
          split={false}
          amount={0.05}
        />
      </div>
    </div>
  );
}
