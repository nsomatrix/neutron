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
    version: "1.5.0",
    date: "2026-11-19",
    tag: "latest",
    changes: [
      { type: "changed", description: "Released updated standalone application binaries (neutron-1.5.0.jar & neutron-core-1.5.0.jar) with verified SHA-256 checksums" },
      { type: "added", description: "Virtual Keypad: Integrated on-screen virtual phone keypad with quick toggle shortcut (Ctrl+B)" },
      { type: "added", description: "Cross-Platform 3D: Bundled pre-compiled C99 JSR-184 M3G native binaries for Linux (x86_64), Windows (x86_64), and macOS (Intel & Apple Silicon arm64)" },
      { type: "fixed", description: "JSR-184 M3G Stability: Synchronized object finalization and added atomic handle verification, eliminating native SIGSEGV crashes on scene changes" },
      { type: "changed", description: "Graphics Engine: Pixel-exact integer alpha blending with zero-arithmetic fast-path rasterization (+38% throughput boost)" },
      { type: "fixed", description: "Nokia UI DirectGraphics: Implemented full transform manipulation matrix (FLIP_VERTICAL combinations) and polygon/triangle rendering" },
      { type: "fixed", description: "MMAPI Audio: Corrected MidiAudioPlayer lifecycle state transitions and coupled VolumeControl to hardware master gain" },
      { type: "fixed", description: "RMS Persistence: Implemented atomic temporary-file file persistence (saveToDiskSecure) and path traversal sanitization" },
      { type: "fixed", description: "JVM 21+ Compatibility: Cooperative thread interruption and cleanup replacing deprecated Thread.stop()" },
      { type: "fixed", description: "JSR-75 FileConnection: Wrapped output streams to safely close underlying RandomAccessFile descriptors on Windows" },
      { type: "added", description: "Universal Drag & Drop: Full window-wide drop target supporting multi-file drops, .jad/.jar pair preference, and RFC 2483 URI list parsing on Linux" },
      { type: "fixed", description: "MIDP 2.0 GameCanvas: Protected off-screen buffer lifecycle against premature invalidation during midlet startup and hot-swap" },
      { type: "added", description: "Display Engine: Dynamic aspect-ratio letterboxing with active black margin blanking to prevent canvas ghosting across games" }
    ],
  },
  {
    version: "1.4.0",
    date: "2026-11-06",
    tag: "stable",
    changes: [
      { type: "changed", description: "Released updated application binaries (neutron-1.4.0.jar & neutron-core-1.4.0.jar)" },
      { type: "fixed", description: "J2ME Core & Math Integrity: Corrected Sprite reference pixel coordinates and transform matrix calculations across all 8 orientations" },
      { type: "added", description: "JSR-184 M3G: Implemented full binary .m3g chunk stream parser with Zlib deflation and Adler-32 integrity validation" },
      { type: "fixed", description: "MMAPI Audio: Non-destructive end-of-media player lifecycle restoration with seamless repeat playback and tone generator binding" },
      { type: "added", description: "Nokia UI DirectGraphics: Implemented 32-bit ARGB drawPixels with rotational and mirror manipulation support" },
      { type: "fixed", description: "JSR-75 FileConnection: Enforced strict read-only mode validation (IllegalModeException) and blocked path-traversal escapes" },
      { type: "fixed", description: "GCF Networking: Preserved default HTTP GET method semantics until explicit output stream creation" },
      { type: "changed", description: "Project metadata: Updated developer role attribution to CoreDev" }
    ],
  },
  {
    version: "1.3.0",
    date: "2026-10-22",
    tag: "stable",
    changes: [
      { type: "changed", description: "Released updated application binaries (neutron-1.3.0.jar & neutron-core-1.3.0.jar)" },
      { type: "changed", description: "Streamlined Performance HUD with dynamic autoscaling badge and clean live FPS readout" },
      { type: "added", description: "Integrated cross-platform vector 2D fullscreen menu button replacing Unicode glyphs" },
      { type: "added", description: "Updated high-resolution official Neutron atomic brand emblem across window headers, dialogs, and dock" },
      { type: "added", description: "Authoritative runtime status bar tracking with emerald green state indicators" },
      { type: "changed", description: "Optimized direct-mapped RGB color cache and zero-allocation graphics rendering pipeline" },
      { type: "changed", description: "Enhanced Snapshot Manager initialization sequence for restored midlet suites" }
    ],
  },
  {
    version: "1.1.0",
    date: "2026-09-04",
    tag: "stable",
    changes: [
      { type: "changed", description: "Released updated application binaries (neutron-1.1.0.jar & neutron-core-1.1.0.jar)" },
      { type: "added", description: "Refactored network persistence system with Airplane Mode toggle" },
      { type: "added", description: "Implemented input event hooks and interactive Sequencer engine" },
      { type: "added", description: "Modernized performance controls dialogs with dynamic frame rate limiting" },
      { type: "added", description: "Integrated Snapshot Manager backup and restore architecture" }
    ],
  },
  {
    version: "1.0.0",
    date: "2026-07-20",
    tag: "stable",
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
