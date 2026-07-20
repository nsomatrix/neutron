"use client";

import { cn } from "@/lib/utils";
import * as TabsPrimitive from "@radix-ui/react-tabs";
import { type ReactNode } from "react";

interface TabItem {
  label: string;
  value: string;
  children: ReactNode;
}

interface TabsProps {
  items: TabItem[];
  defaultValue?: string;
  className?: string;
}

export function Tabs({ items, defaultValue, className }: TabsProps) {
  return (
    <TabsPrimitive.Root
      defaultValue={defaultValue || items[0]?.value}
      className={cn("my-6", className)}
    >
      <TabsPrimitive.List className="flex border-b border-border">
        {items.map((item) => (
          <TabsPrimitive.Trigger
            key={item.value}
            value={item.value}
            className={cn(
              "px-4 py-2 text-sm font-medium transition-colors",
              "border-b-2 border-transparent text-muted-foreground",
              "hover:text-foreground",
              "data-[state=active]:border-primary data-[state=active]:text-foreground"
            )}
          >
            {item.label}
          </TabsPrimitive.Trigger>
        ))}
      </TabsPrimitive.List>
      {items.map((item) => (
        <TabsPrimitive.Content
          key={item.value}
          value={item.value}
          className="mt-4"
        >
          {item.children}
        </TabsPrimitive.Content>
      ))}
    </TabsPrimitive.Root>
  );
}

// Simple Tab components for MDX usage
export function TabGroup({
  children,
  defaultValue,
}: {
  children: ReactNode;
  defaultValue?: string;
}) {
  return (
    <TabsPrimitive.Root defaultValue={defaultValue} className="my-6">
      {children}
    </TabsPrimitive.Root>
  );
}

export function TabList({ children }: { children: ReactNode }) {
  return (
    <TabsPrimitive.List className="flex border-b border-border">
      {children}
    </TabsPrimitive.List>
  );
}

export function Tab({ value, children }: { value: string; children: ReactNode }) {
  return (
    <TabsPrimitive.Trigger
      value={value}
      className={cn(
        "px-4 py-2 text-sm font-medium transition-colors",
        "border-b-2 border-transparent text-muted-foreground",
        "hover:text-foreground",
        "data-[state=active]:border-primary data-[state=active]:text-foreground"
      )}
    >
      {children}
    </TabsPrimitive.Trigger>
  );
}

export function TabPanel({ value, children }: { value: string; children: ReactNode }) {
  return (
    <TabsPrimitive.Content value={value} className="mt-4">
      {children}
    </TabsPrimitive.Content>
  );
}
