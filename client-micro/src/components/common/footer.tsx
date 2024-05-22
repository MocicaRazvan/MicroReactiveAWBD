import { Home, MailIcon, MapPinIcon, PhoneIcon } from "lucide-react";
import Link from "next/link";

export default function Footer() {
  return (
    <footer className="w-full py-6 mt-28 flex items-center justify-center flex-col">
      <div className="container flex flex-col gap-4 px-4 md:px-6">
        <div className="grid items-start gap-2 sm:grid-cols-2 md:gap-4 lg:grid-cols-4 lg:gap-8">
          <div className="flex items-center gap-2">
            <Link className="flex items-center gap-2 font-medium" href="/">
              <Home className="w-6 h-6" />
              <span className="sr-only">Home</span>
            </Link>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              Your path to wellness starts here.
            </p>
          </div>
          <div className="space-y-2">
            <ul className="grid grid-cols-2 gap-2">
              <li>
                <Link
                  className="text-sm font-medium"
                  href="/trainings/approved"
                >
                  Trainings
                </Link>
              </li>
              <li>
                <Link className="text-sm font-medium" href="/posts/approved">
                  Posts
                </Link>
              </li>
              <li>
                <Link className="text-sm font-medium" href="/auth/signup">
                  Sign Up
                </Link>
              </li>
              <li>
                <Link className="text-sm font-medium" href="/auth/signin">
                  Sign In
                </Link>
              </li>
            </ul>
          </div>
          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <MapPinIcon className="w-4 h-4 flex-shrink-0" />
              <p className="text-sm font-medium">123 Wellness Way, Suite 100</p>
            </div>
            <div className="flex items-center gap-2">
              <PhoneIcon className="w-4 h-4 flex-shrink-0" />
              <p className="text-sm font-medium">0764105200</p>
            </div>
            <div className="flex items-center gap-2">
              <MailIcon className="w-4 h-4 flex-shrink-0" />
              <a
                className="text-sm font-medium underline underline-offset-2"
                href="mailto:razvanmocica@gmail.com"
              >
                razvanmocica@gmail.com
              </a>
            </div>
          </div>
          <div className="space-y-2">
            <ul className="flex flex-col gap-2 sm:flex-row sm:gap-1">
              <li>
                <Link className="text-sm font-medium" href="#">
                  Terms of Service
                </Link>
              </li>
              <li>
                <Link className="text-sm font-medium" href="#">
                  Privacy Policy
                </Link>
              </li>
              <li>
                <Link className="text-sm font-medium" href="#">
                  Disclaimer
                </Link>
              </li>
            </ul>
          </div>
        </div>
        <div className="flex flex-col gap-1">
          <hr className="w-full border-gray-200 dark:border-gray-800" />
          <p className="text-xs text-gray-500 justify-self-center dark:text-gray-400">
            © {new Date().getFullYear()} Wellness Co. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
