import Link from "next/link";
import Image from "next/image";
import { cn } from "@/lib/utils";
import logoImg from "@/assets/ntn.png";

interface LogoProps {
  className?: string;
  showText?: boolean;
}

export function Logo({ className, showText = true }: LogoProps) {
  return (
    <Link
      href="/"
      className={cn("flex items-center gap-2.5 group", className)}
      aria-label="Neutron Home"
    >
      <div className="relative h-8 w-8 shrink-0 overflow-hidden rounded-lg">
        <Image
          src={logoImg}
          alt="Neutron Logo"
          width={32}
          height={32}
          className="h-full w-full object-contain"
          priority
        />
      </div>
      {showText && (
        <span className="text-lg font-semibold tracking-tight text-foreground">
          Neutron
        </span>
      )}
    </Link>
  );
}
