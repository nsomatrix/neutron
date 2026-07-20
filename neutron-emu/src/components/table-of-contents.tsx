"use client";

import { cn } from "@/lib/utils";
import { useScrollSpy } from "@/hooks/use-scroll-spy";
import { type TocItem } from "@/lib/toc";

interface TableOfContentsProps {
  items: TocItem[];
  className?: string;
}

export function TableOfContents({ items, className }: TableOfContentsProps) {
  const ids = items.map((item) => item.id);
  const activeId = useScrollSpy(ids, 100);

  if (items.length === 0) return null;

  return (
    <nav
      className={cn("space-y-1", className)}
      aria-label="Table of contents"
    >
      <p className="mb-3 text-sm font-medium text-foreground">On this page</p>
      {items.map((item) => (
        <a
          key={item.id}
          href={`#${item.id}`}
          className={cn(
            "block border-l-2 py-1 text-sm transition-colors duration-200",
            item.level === 2 && "pl-3",
            item.level === 3 && "pl-6",
            item.level === 4 && "pl-9",
            activeId === item.id
              ? "border-primary text-primary font-medium"
              : "border-transparent text-muted-foreground hover:border-border hover:text-foreground"
          )}
        >
          {item.title}
        </a>
      ))}
    </nav>
  );
}
