"use client";

import { cn } from "@/lib/utils";
import * as AccordionPrimitive from "@radix-ui/react-accordion";
import { ChevronRight } from "lucide-react";
import { type ReactNode } from "react";

interface AccordionItem {
  title: string;
  value: string;
  children: ReactNode;
}

interface AccordionProps {
  items: AccordionItem[];
  className?: string;
  type?: "single" | "multiple";
}

export function Accordion({ items, className, type = "single" }: AccordionProps) {
  return (
    <AccordionPrimitive.Root
      type={type}
      collapsible={type === "single"}
      className={cn("my-6 space-y-2", className)}
    >
      {items.map((item) => (
        <AccordionPrimitive.Item
          key={item.value}
          value={item.value}
          className="rounded-lg border border-border"
        >
          <AccordionPrimitive.Header>
            <AccordionPrimitive.Trigger className="flex w-full items-center justify-between px-4 py-3 text-sm font-medium text-foreground transition-colors hover:bg-muted/50 [&[data-state=open]>svg]:rotate-90">
              {item.title}
              <ChevronRight className="h-4 w-4 shrink-0 text-muted-foreground transition-transform duration-200" />
            </AccordionPrimitive.Trigger>
          </AccordionPrimitive.Header>
          <AccordionPrimitive.Content className="overflow-hidden data-[state=closed]:animate-accordion-up data-[state=open]:animate-accordion-down">
            <div className="px-4 pb-4 text-sm text-muted-foreground">
              {item.children}
            </div>
          </AccordionPrimitive.Content>
        </AccordionPrimitive.Item>
      ))}
    </AccordionPrimitive.Root>
  );
}

// Simple components for MDX usage
export function AccordionGroup({ children }: { children: ReactNode }) {
  return (
    <AccordionPrimitive.Root type="single" collapsible className="my-6 space-y-2">
      {children}
    </AccordionPrimitive.Root>
  );
}

export function AccordionPanel({
  title,
  value,
  children,
}: {
  title: string;
  value: string;
  children: ReactNode;
}) {
  return (
    <AccordionPrimitive.Item value={value} className="rounded-lg border border-border">
      <AccordionPrimitive.Header>
        <AccordionPrimitive.Trigger className="flex w-full items-center justify-between px-4 py-3 text-sm font-medium text-foreground transition-colors hover:bg-muted/50 [&[data-state=open]>svg]:rotate-90">
          {title}
          <ChevronRight className="h-4 w-4 shrink-0 text-muted-foreground transition-transform duration-200" />
        </AccordionPrimitive.Trigger>
      </AccordionPrimitive.Header>
      <AccordionPrimitive.Content className="overflow-hidden">
        <div className="px-4 pb-4 text-sm text-muted-foreground">{children}</div>
      </AccordionPrimitive.Content>
    </AccordionPrimitive.Item>
  );
}
