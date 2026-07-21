"use client";

import { useRef, MouseEvent } from "react";
import { cn } from "@/lib/utils";

interface SpotlightCardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function SpotlightCard({ children, className, ...props }: SpotlightCardProps) {
  const cardRef = useRef<HTMLDivElement>(null);

  function handleMouseMove(e: MouseEvent<HTMLDivElement>) {
    const card = cardRef.current;
    if (!card) return;
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    card.style.setProperty("--mouse-x", `${x}px`);
    card.style.setProperty("--mouse-y", `${y}px`);
  }

  function handleMouseEnter() {
    const card = cardRef.current;
    if (!card) return;
    card.style.setProperty("--spotlight-opacity", "1");
  }

  function handleMouseLeave() {
    const card = cardRef.current;
    if (!card) return;
    card.style.setProperty("--spotlight-opacity", "0");
  }

  return (
    <div
      ref={cardRef}
      onMouseMove={handleMouseMove}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      className={cn(
        "relative rounded-xl border border-border bg-card p-6 overflow-hidden transition-all duration-300",
        className
      )}
      {...props}
    >
      {/* Spotlight ambient background glow */}
      <div
        className="pointer-events-none absolute -inset-px rounded-xl transition-opacity duration-300 z-0"
        style={{
          opacity: "var(--spotlight-opacity, 0)" as any,
          background: `radial-gradient(400px circle at var(--mouse-x, 0px) var(--mouse-y, 0px), rgba(59, 130, 246, 0.08), transparent 80%)`,
        }}
      />
      {/* Spotlight border mask overlay */}
      <div
        className="pointer-events-none absolute -inset-px rounded-xl transition-opacity duration-300 z-10"
        style={{
          opacity: "var(--spotlight-opacity, 0)" as any,
          background: `radial-gradient(150px circle at var(--mouse-x, 0px) var(--mouse-y, 0px), rgba(6, 182, 212, 0.4), rgba(139, 92, 246, 0.4), transparent 80%)`,
          maskImage: "linear-gradient(black, black) content-box, linear-gradient(black, black)",
          maskComposite: "exclude",
          WebkitMaskImage: "linear-gradient(black, black) content-box, linear-gradient(black, black)",
          WebkitMaskComposite: "xor",
          padding: "1px",
        }}
      />
      <div className="relative z-20 h-full flex flex-col">
        {children}
      </div>
    </div>
  );
}
