import Link from "next/link";
import { ArrowLeft, ArrowRight } from "lucide-react";
import { cn } from "@/lib/utils";

interface PaginationProps {
  prev?: { title: string; href: string };
  next?: { title: string; href: string };
  className?: string;
}

export function Pagination({ prev, next, className }: PaginationProps) {
  return (
    <div
      className={cn(
        "mt-12 flex flex-col gap-4 border-t border-border pt-8 sm:flex-row sm:justify-between",
        className
      )}
    >
      {prev ? (
        <Link
          href={prev.href}
          className="group flex flex-1 items-center gap-3 rounded-lg border border-border p-4 transition-all hover:border-primary/30 hover:bg-muted/50"
        >
          <ArrowLeft className="h-4 w-4 shrink-0 text-muted-foreground transition-transform group-hover:-translate-x-0.5" />
          <div>
            <p className="text-xs text-muted-foreground">Previous</p>
            <p className="text-sm font-medium text-foreground">{prev.title}</p>
          </div>
        </Link>
      ) : (
        <div className="flex-1" />
      )}

      {next ? (
        <Link
          href={next.href}
          className="group flex flex-1 items-center justify-end gap-3 rounded-lg border border-border p-4 transition-all hover:border-primary/30 hover:bg-muted/50 text-right"
        >
          <div>
            <p className="text-xs text-muted-foreground">Next</p>
            <p className="text-sm font-medium text-foreground">{next.title}</p>
          </div>
          <ArrowRight className="h-4 w-4 shrink-0 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
        </Link>
      ) : (
        <div className="flex-1" />
      )}
    </div>
  );
}
