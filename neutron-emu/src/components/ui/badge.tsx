import { cn } from "@/lib/utils";
import { type ReactNode } from "react";

interface BadgeProps {
  children: ReactNode;
  variant?: "default" | "secondary" | "outline" | "destructive";
  className?: string;
}

export function Badge({ children, variant = "default", className }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium transition-colors",
        variant === "default" && "bg-primary/10 text-primary",
        variant === "secondary" && "bg-muted text-muted-foreground",
        variant === "outline" && "border border-border text-muted-foreground",
        variant === "destructive" && "bg-destructive/10 text-destructive",
        className
      )}
    >
      {children}
    </span>
  );
}
