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
    version: "1.0.0",
    date: "2026-07-20",
    tag: "latest",
    changes: [
      { type: "added", description: "Initial release of the modern Java ME (J2ME) Emulator" },
      { type: "added", description: "CLDC 1.1 and MIDP 2.0 compliant core runtime execution engine" },
      { type: "added", description: "Swing-based high-performance desktop interface with dynamic FlatLaf theme support" },
      { type: "added", description: "Advanced display options featuring Integer scaling, Bilinear/Bicubic filters, CRT Scanlines, and LCD grid modes" },
      { type: "added", description: "Dynamic resolution presets from retro 128x160 up to 480x800, plus borderless fullscreen mode (F11)" },
      { type: "added", description: "Custom input key remapping and native controller/gamepad support" },
      { type: "added", description: "Audio engine supporting PCM audio playback, MIDI synthesis, and frequency tone sequences" },
      { type: "added", description: "Advanced GCF network control with global toggle, HTTP/SOCKS5 proxy, and authentication" },
      { type: "added", description: "Diagnostics suite including Performance HUD, real-time Logging Console, RMS record store inspector, and media/GIF recorders" }
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
