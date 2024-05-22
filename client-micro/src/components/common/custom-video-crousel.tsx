import { CldVideoPlayer } from "next-cloudinary";
import "next-cloudinary/dist/cld-video-player.css";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "../ui/carousel";

interface Props {
  videos: string[];
}

export default function CustomVideoCarousel({ videos }: Props) {
  return (
    <div className="w-full flex justify-center items-center overflow-hidden">
      <Carousel className="w-full max-w-4xl">
        <CarouselContent>
          {videos.map((video, i) => (
            <CarouselItem key={video + i}>
              <div className="rounded-lg overflow-hidden w-full">
                <CldVideoPlayer
                  src={video}
                  width={"1000"}
                  height={"450"}
                  className="w-full max-w-[1000px] max-h-[450px] object-cover"
                />
              </div>
            </CarouselItem>
          ))}
        </CarouselContent>
        <CarouselPrevious />
        <CarouselNext />
      </Carousel>
    </div>
  );
}
