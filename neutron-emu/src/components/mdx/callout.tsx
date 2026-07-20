import { cn } from "@/lib/utils";
import { AlertTriangle, Info, Lightbulb, AlertOctagon } from "lucide-react";
import { type ReactNode } from "react";

const variants = {
  info: {
    icon: Info,
    className: "border-blue-accent/30 bg-blue-accent/5",
    iconColor: "text-blue-accent",
    title: "Info",
  },
  tip: {
    icon: Lightbulb,
    className: "border-emerald-500/30 bg-emerald-500/5",
    iconColor: "text-emerald-500",
    title: "Tip",
  },
  warning: {
    icon: AlertTriangle,
    className: "border-amber-500/30 bg-amber-500/5",
    iconColor: "text-amber-500",
    title: "Warning",
  },
  danger: {
    icon: AlertOctagon,
    className: "border-red-500/30 bg-red-500/5",
    iconColor: "text-red-500",
    title: "Danger",
  },
} as const;

interface CalloutProps {
  type?: keyof typeof variants;
  title?: string;
  children: ReactNode;
}

export function Callout({ type = "info", title, children }: CalloutProps) {
  const variant = variants[type];
  const Icon = variant.icon;

  return (
    <div
      className={cn(
        "my-6 rounded-lg border p-4",
        variant.className
      )}
      role="note"
    >
      <div className="flex items-start gap-3">
        <Icon className={cn("mt-0.5 h-5 w-5 shrink-0", variant.iconColor)} />
        <div className="min-w-0 flex-1">
          <p className={cn("mb-1 text-sm font-semibold", variant.iconColor)}>
            {title || variant.title}
          </p>
          <div className="text-sm text-muted-foreground [&>p]:mb-0">
            {children}
          </div>
        </div>
      </div>
    </div>
  );
}
