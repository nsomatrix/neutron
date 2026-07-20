"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";
import { ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";
import { docsConfig, type DocsSidebarItem } from "@/config/docs";
import { motion, AnimatePresence } from "framer-motion";

function SidebarCategory({ item, depth = 0 }: { item: DocsSidebarItem; depth?: number }) {
  const pathname = usePathname();
  const hasChildren = item.items && item.items.length > 0;

  const isActive = item.href === pathname;
  const isChildActive = hasChildren && item.items!.some(
    (child) =>
      child.href === pathname ||
      (child.items && child.items.some((gc) => gc.href === pathname))
  );

  const [open, setOpen] = useState(isChildActive || depth === 0);

  // Simple link (no children)
  if (!hasChildren && item.href) {
    return (
      <Link
        href={item.href}
        className={cn(
          "flex items-center rounded-md px-3 py-1.5 text-sm transition-colors",
          isActive
            ? "bg-primary/10 font-medium text-primary"
            : "text-muted-foreground hover:bg-muted hover:text-foreground"
        )}
      >
        {isActive && (
          <span className="mr-2 h-1 w-1 rounded-full bg-primary" />
        )}
        {item.title}
        {item.label && (
          <span className="ml-auto rounded-full bg-primary/10 px-1.5 py-0.5 text-xs font-medium text-primary">
            {item.label}
          </span>
        )}
      </Link>
    );
  }

  // Category with children
  return (
    <div>
      <button
        onClick={() => setOpen(!open)}
        className={cn(
          "flex w-full items-center justify-between rounded-md px-3 py-1.5 text-sm font-medium transition-colors",
          depth === 0
            ? "text-foreground"
            : "text-muted-foreground hover:text-foreground"
        )}
        aria-expanded={open}
      >
        {item.title}
        <ChevronRight
          className={cn(
            "h-3.5 w-3.5 shrink-0 text-muted-foreground transition-transform duration-200",
            open && "rotate-90"
          )}
        />
      </button>
      <AnimatePresence initial={false}>
        {open && hasChildren && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.2, ease: "easeInOut" }}
            className="overflow-hidden"
          >
            <div className={cn("ml-3 border-l border-border pl-3 mt-0.5", depth > 0 && "ml-2 pl-2")}>
              {item.items!.map((child) => (
                <SidebarCategory
                  key={child.title}
                  item={child}
                  depth={depth + 1}
                />
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export function DocsSidebar({ className }: { className?: string }) {
  return (
    <aside
      className={cn("flex flex-col gap-1", className)}
      aria-label="Documentation sidebar"
    >
      {docsConfig.map((item) => (
        <SidebarCategory key={item.title} item={item} />
      ))}
    </aside>
  );
}
