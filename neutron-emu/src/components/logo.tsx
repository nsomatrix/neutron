import Link from "next/link";
import { cn } from "@/lib/utils";

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
      <div className="relative flex h-8 w-8 items-center justify-center">
        {/* Atom core */}
        <div className="absolute h-3 w-3 rounded-full bg-gradient-to-br from-blue-accent via-cyan-accent to-purple-accent shadow-[0_0_12px_rgba(59,130,246,0.5)]" />
        {/* Orbit rings */}
        <div className="absolute h-8 w-8 rounded-full border border-primary/30 group-hover:border-primary/50 transition-colors duration-300" />
        <div className="absolute h-6 w-6 rounded-full border border-cyan-accent/20 rotate-45 group-hover:border-cyan-accent/40 transition-colors duration-300" />
      </div>
      {showText && (
        <span className="text-lg font-semibold tracking-tight text-foreground">
          Neutron
        </span>
      )}
    </Link>
  );
}
