import Image from "next/image";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "../ui/carousel";

interface Props {
  images: string[];
}

export default function CustomImageCarousel({ images }: Props) {
  return (
    <div className="w-full flex justify-center items-center overflow-hidden">
      <Carousel className="w-full max-w-4xl">
        <CarouselContent>
          {images.map((img, i) => (
            <CarouselItem key={img + i}>
              <div className="rounded-lg overflow-hidden w-full">
                <Image
                  src={img}
                  alt="post image"
                  width={400}
                  height={400}
                  className="w-full max-w-[1000px] max-h-[450px] object-cover h-full"
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
