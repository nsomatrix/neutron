"use client";

import { useTheme } from "next-themes";
import { useSyncExternalStore } from "react";
import { flushSync } from "react-dom";
import { Sun, Moon, Monitor } from "lucide-react";
import { cn } from "@/lib/utils";

export function ThemeSwitcher() {
  const { theme, setTheme } = useTheme();
  const mounted = useSyncExternalStore(
    () => () => {},
    () => true,
    () => false
  );

  function handleThemeChange(nextTheme: string) {
    if (nextTheme === theme) return;

    const prefersReducedMotion = window.matchMedia(
      "(prefers-reduced-motion: reduce)"
    ).matches;
    const doc = document as Document & {
      startViewTransition?: (cb: () => void) => { finished?: Promise<void> };
    };

    if (!doc.startViewTransition || prefersReducedMotion) {
      setTheme(nextTheme);
      return;
    }

    const resolvedClass =
      nextTheme === "system"
        ? window.matchMedia("(prefers-color-scheme: dark)").matches
          ? "dark"
          : "light"
        : nextTheme;

    const transition = doc.startViewTransition(() => {
      const root = document.documentElement;
      root.classList.remove("light", "dark");
      root.classList.add(resolvedClass);
      root.style.colorScheme = resolvedClass;

      flushSync(() => {
        setTheme(nextTheme);
      });
    });

    transition?.finished?.catch(() => {});
  }

  if (!mounted) {
    return (
      <div
        className="flex h-9 w-[92px] items-center rounded-lg border border-border bg-muted/50 p-[3px]"
        aria-hidden="true"
      />
    );
  }

  const themes = [
    { value: "light", icon: Sun, label: "Light" },
    { value: "dark", icon: Moon, label: "Dark" },
    { value: "system", icon: Monitor, label: "System" },
  ] as const;

  return (
    <div className="flex h-9 items-center gap-0.5 rounded-lg border border-border bg-muted/50 p-[3px]">
      {themes.map(({ value, icon: Icon, label }) => (
        <button
          key={value}
          type="button"
          onClick={() => handleThemeChange(value)}
          className={cn(
            "flex h-7 w-7 items-center justify-center rounded-md transition-colors duration-150",
            theme === value
              ? "bg-background text-foreground shadow-sm"
              : "text-muted-foreground hover:text-foreground"
          )}
          aria-label={`Switch to ${label} theme`}
          aria-pressed={theme === value}
          title={label}
        >
          <Icon className="h-3.5 w-3.5" />
        </button>
      ))}
    </div>
  );
}
