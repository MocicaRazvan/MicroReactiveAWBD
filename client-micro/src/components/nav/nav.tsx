"use client";
import {ModeToggle} from "../common/theme-switch";

import Link from "next/link";

import {useSession} from "next-auth/react";
import {Avatar} from "../ui/avatar";
import {AvatarImage} from "@radix-ui/react-avatar";
import {cn} from "@/lib/utils";
import {useMemo, useState} from "react";
import CartPop from "./cart-pop";
import {Home} from "lucide-react";
import {
    createExercisesLinks,
    createOrdersLinks,
    createPostsLinks,
    createTrainingsLinks,
    linkFactory,
} from "@/components/nav/links";
import {MenuBarMenuNav} from "@/components/nav/menu-bar-menu-nav";
import {BurgerNav} from "@/components/nav/burger-nav";
import {NavProfile} from "@/components/nav/nav-profile";

export default function Nav() {
    const session = useSession();
    const [showProfile, setShowProfile] = useState(false);

    const authUser = session?.data?.user;

    const isUser = authUser?.role === "ROLE_USER";
    const isTrainer = authUser?.role === "ROLE_TRAINER";
    const isAdmin = authUser?.role === "ROLE_ADMIN";
    const isAdminOrTrainer = isAdmin || isTrainer;

    const postsLinks = useMemo(
        () => linkFactory(authUser, createPostsLinks),
        [authUser],
    );

    const exercisesLinks = useMemo(
        () => linkFactory(authUser, createExercisesLinks),
        [authUser],
    );

    const trainingsLinks = useMemo(
        () => linkFactory(authUser, createTrainingsLinks),
        [authUser],
    );

    const ordersLinks = useMemo(
        () => linkFactory(authUser, createOrdersLinks),
        [authUser],
    );

    return (
        <nav
            className="min-h-10 md:flex items-center justify-between px-4 py-2 border-b sticky top-0 bg-opacity-60 z-[49]
    w-full border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 flex-wrap 2xl:border-l 2xl:border-r"
        >
            <div className="hidden md:flex items-center justify-between w-full">
                <div className="flex items-center justify-center gap-4 flex-wrap">
                    <div className="mr-8">
                        <Link
                            href="/"
                            className="font-bold hover:underline flex items-center justify-center gap-2"
                        >
                            <Home/> Home
                        </Link>
                    </div>

                    {isUser && (
                        <Link href="/posts/approved" className="font-bold hover:underline">
                            Posts
                        </Link>
                    )}
                    <MenuBarMenuNav
                        title={"Posts"}
                        render={!isUser}
                        links={postsLinks}
                        authUser={authUser}
                    />

                    <MenuBarMenuNav
                        title={"Exercises"}
                        render={isAdminOrTrainer}
                        links={exercisesLinks}
                        authUser={authUser}
                    />
                    {isUser && (
                        <Link
                            href="/trainings/approved"
                            className="font-bold hover:underline"
                        >
                            Trainings
                        </Link>
                    )}

                    <MenuBarMenuNav
                        title={"Trainings"}
                        render={!isUser}
                        links={trainingsLinks}
                        authUser={authUser}
                    />
                    {!isAdmin && authUser && (
                        <Link
                            href={`/users/${authUser?.id}/orders`}
                            className="font-bold hover:underline"
                        >
                            Your Orders
                        </Link>
                    )}
                    <MenuBarMenuNav
                        title={"Orders"}
                        render={isAdmin}
                        links={ordersLinks}
                        authUser={authUser}
                    />
                    {isAdmin && (
                        <Link href="/admin" className="font-bold hover:underline">
                            Admin
                        </Link>
                    )}
                </div>

                <div
                    className="mx-auto md:ml-auto md:mr-1 flex items-center justify-center gap-6
      mt-2 sm:mt-0
      "
                >
                    {authUser && (
                        <>
                            <CartPop userId={authUser.id}/>

                            <NavProfile
                                authUser={authUser}
                                setShowProfile={setShowProfile}
                                showProfile={showProfile}
                            />
                        </>
                    )}
                    {!authUser && (
                        <div>
                            <Link href="/auth/signin" className="font-bold hover:underline">
                                Sign In
                            </Link>
                        </div>
                    )}
                    <ModeToggle/>
                </div>
            </div>
            <div className="md:hidden w-full flex items-center justify-between">
                <BurgerNav
                    authUser={authUser}
                    postsLinks={postsLinks}
                    exercisesLinks={exercisesLinks}
                    trainingsLinks={trainingsLinks}
                    ordersLinks={ordersLinks}
                    isAdminOrTrainer={isAdminOrTrainer}
                    isUser={isUser}
                    isTrainer={isTrainer}
                    isAdmin={isAdmin}
                />
                <div className="flex items-center justify-center gap-5">
                    {authUser && <CartPop userId={authUser.id}/>}
                    <ModeToggle/>
                </div>
            </div>
        </nav>
    );
}
