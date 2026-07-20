import type { Metadata } from "next";
import { Badge } from "@/components/ui/badge";

export const metadata: Metadata = {
  title: "Changelog",
  description: "Release notes and version history for Neutron.",
};

interface Release {
  version: string;
  date: string;
  tag: "latest" | "stable" | "beta" | null;
  changes: {
    type: "added" | "changed" | "fixed" | "removed";
    description: string;
  }[];
}

const releases: Release[] = [
  {
    version: "2.0.0",
    date: "2024-01-15",
    tag: "latest",
    changes: [
      { type: "added", description: "Complete UI redesign with dark mode support" },
      { type: "added", description: "Plugin system for extensibility" },
      { type: "added", description: "CLI interface with full command support" },
      { type: "added", description: "Gamepad and controller support" },
      { type: "added", description: "SOCKS5 proxy authentication" },
      { type: "changed", description: "Upgraded to Java 17 baseline" },
      { type: "changed", description: "Improved audio latency by 40%" },
      { type: "changed", description: "Rewritten network stack with connection tracking" },
      { type: "fixed", description: "Memory leak in Sprite rendering pipeline" },
      { type: "fixed", description: "Fullscreen toggle causing UI freeze" },
      { type: "fixed", description: "RMS data corruption on concurrent access" },
    ],
  },
  {
    version: "1.5.0",
    date: "2023-09-20",
    tag: null,
    changes: [
      { type: "added", description: "Fullscreen mode with auto-hiding status bar" },
      { type: "added", description: "Network activity monitoring" },
      { type: "added", description: "Screenshot capture functionality" },
      { type: "changed", description: "Improved MIDP 2.0 compliance" },
      { type: "fixed", description: "Audio playback glitches on macOS" },
      { type: "fixed", description: "Key mapping not persisting across sessions" },
    ],
  },
  {
    version: "1.4.0",
    date: "2023-06-10",
    tag: null,
    changes: [
      { type: "added", description: "HTTPS connection support" },
      { type: "added", description: "Custom key binding configuration" },
      { type: "changed", description: "Performance improvements for Game API rendering" },
      { type: "fixed", description: "TiledLayer rendering artifacts at edges" },
      { type: "removed", description: "Deprecated J2SE compatibility layer" },
    ],
  },
  {
    version: "1.3.0",
    date: "2023-03-01",
    tag: null,
    changes: [
      { type: "added", description: "MIDI playback support" },
      { type: "added", description: "Configuration file (TOML format)" },
      { type: "changed", description: "Improved Sprite collision detection accuracy" },
      { type: "fixed", description: "Thread synchronization issues in Canvas paint" },
    ],
  },
];

const typeConfig = {
  added: { label: "Added", color: "text-emerald-500" },
  changed: { label: "Changed", color: "text-blue-accent" },
  fixed: { label: "Fixed", color: "text-amber-500" },
  removed: { label: "Removed", color: "text-red-500" },
} as const;

export default function ChangelogPage() {
  return (
    <div className="mx-auto max-w-3xl px-4 py-16">
      <h1 className="text-4xl font-bold tracking-tight">Changelog</h1>
      <p className="mt-4 text-lg text-muted-foreground">
        All notable changes to Neutron are documented here.
      </p>

      <div className="mt-12 space-y-16">
        {releases.map((release) => (
          <article key={release.version} className="relative">
            <div className="flex items-center gap-3 mb-6">
              <h2 className="text-2xl font-bold">v{release.version}</h2>
              {release.tag && (
                <Badge variant={release.tag === "latest" ? "default" : "secondary"}>
                  {release.tag}
                </Badge>
              )}
              <span className="text-sm text-muted-foreground">
                {release.date}
              </span>
            </div>

            <div className="space-y-3">
              {release.changes.map((change, i) => {
                const config = typeConfig[change.type];
                return (
                  <div
                    key={i}
                    className="flex items-start gap-3 rounded-lg border border-border bg-card/50 px-4 py-3"
                  >
                    <span
                      className={`shrink-0 text-xs font-semibold uppercase ${config.color}`}
                    >
                      {config.label}
                    </span>
                    <span className="text-sm text-muted-foreground">
                      {change.description}
                    </span>
                  </div>
                );
              })}
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
