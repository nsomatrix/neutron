import Link from "next/link";
import { ChevronRight, Home } from "lucide-react";
import { cn } from "@/lib/utils";

interface BreadcrumbItem {
  title: string;
  href?: string;
}

interface BreadcrumbProps {
  items: BreadcrumbItem[];
  className?: string;
}

export function Breadcrumb({ items, className }: BreadcrumbProps) {
  return (
    <nav
      className={cn("flex items-center gap-1.5 text-sm", className)}
      aria-label="Breadcrumb"
    >
      <Link
        href="/docs"
        className="flex items-center text-muted-foreground transition-colors hover:text-foreground"
        aria-label="Documentation home"
      >
        <Home className="h-3.5 w-3.5" />
      </Link>
      {items.map((item, i) => (
        <span key={i} className="flex items-center gap-1.5">
          <ChevronRight className="h-3.5 w-3.5 text-muted-foreground/50" />
          {item.href ? (
            <Link
              href={item.href}
              className="text-muted-foreground transition-colors hover:text-foreground"
            >
              {item.title}
            </Link>
          ) : (
            <span className="text-foreground font-medium">{item.title}</span>
          )}
        </span>
      ))}
    </nav>
  );
}
