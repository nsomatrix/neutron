"use client";

import React, { useState, useEffect, useRef, useCallback, useSyncExternalStore } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image, { type StaticImageData } from "next/image";
import Link from "next/link";
import logoImg from "@/assets/ntn.png";
import render1 from "@/assets/render1.gif";
import bounceTales from "@/assets/bounce-tales.gif";
import gokart from "@/assets/gokart.gif";
import rollercoasterRush from "@/assets/rollercoaster-rush.gif";
import { useTheme } from "next-themes";
import {
  Terminal as TerminalIcon,
  RefreshCw,
  Cpu,
  X,
  Minimize2,
  Square,
  Info,
  Gamepad2,
  Folder,
  FileArchive,
  Check,
} from "lucide-react";

const gameGifs: Record<string, StaticImageData> = {
  ninja_offline: render1,
  bounce_tales: bounceTales,
  gokart: gokart,
  rollercoaster: rollercoasterRush,
};

const gameNames: Record<string, string> = {
  ninja_offline: "Ninja School Offline",
  bounce_tales: "Bounce Tales",
  gokart: "3D Go-Kart Racing",
  rollercoaster: "Rollercoaster Rush",
};

const gameDurations: Record<string, number> = {
  ninja_offline: 4310, // 4910ms total - 600ms transition
  bounce_tales: 15600, // 16200ms total - 600ms transition
  gokart: 4000, // 4600ms total - 600ms transition
  rollercoaster: 6600, // 7200ms total - 600ms transition
};

const jarFiles = [
  { id: "ninja_offline", filename: "ninja_school_offline.jar", size: "354 KB" },
  { id: "bounce_tales", filename: "bounce_tales.jar", size: "612 KB" },
  { id: "gokart", filename: "gokart_3d.jar", size: "446 KB" },
  { id: "rollercoaster", filename: "rollercoaster_rush.jar", size: "389 KB" },
] as const;

const keypadDigits = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"] as const;

const dpadButtons = [
  { id: "UP_LEFT", label: "↖", title: "Up-Left" },
  { id: "UP", label: "▲", title: "Up" },
  { id: "UP_RIGHT", label: "↗", title: "Up-Right" },
  { id: "LEFT", label: "◀", title: "Left" },
  { id: "OK", label: "OK", title: "SELECT / FIRE" },
  { id: "RIGHT", label: "▶", title: "Right" },
  { id: "DOWN_LEFT", label: "↙", title: "Down-Left" },
  { id: "DOWN", label: "▼", title: "Down" },
  { id: "DOWN_RIGHT", label: "↘", title: "Down-Right" },
] as const;

type AppState = "typing" | "booting" | "launcher" | "gameplay";
type GraphicsFilter = "nearest" | "scanlines" | "lcd";
type EmulatorTheme = "dark" | "light" | "dracula";

interface ThemePalette {
  windowBg: string;
  titleBarBg: string;
  menuBarBg: string;
  textColor: string;
  borderColor: string;
  dropdownBg: string;
  dropdownHover: string;
  keypadPanelBg: string;
  keypadBtnBg: string;
  keypadBtnHover: string;
  keypadBtnPressed: string;
  keypadBorder: string;
}

const themeStyles: Record<EmulatorTheme, ThemePalette> = {
  dracula: {
    windowBg: "bg-[#282a36]",
    titleBarBg: "bg-[#1e1f29]",
    menuBarBg: "bg-[#21222c]",
    textColor: "text-[#f8f8f2]",
    borderColor: "border-[#44475a]",
    dropdownBg: "bg-[#282a36]",
    dropdownHover: "hover:bg-[#44475a]",
    keypadPanelBg: "bg-[#21222c]",
    keypadBtnBg: "bg-[#343746] text-[#f8f8f2]",
    keypadBtnHover: "hover:bg-[#3f4354]",
    keypadBtnPressed: "bg-[#6272a4] text-white border-[#bd93f9]",
    keypadBorder: "border-[#44475a]",
  },
  dark: {
    windowBg: "bg-[#1e1e1e]",
    titleBarBg: "bg-[#121212]",
    menuBarBg: "bg-[#1a1a1a]",
    textColor: "text-[#fafafa]",
    borderColor: "border-[#2d2d2d]",
    dropdownBg: "bg-[#1e1e1e]",
    dropdownHover: "hover:bg-[#2d2d2d]",
    keypadPanelBg: "bg-[#1b1b1b]",
    keypadBtnBg: "bg-[#2b2b2b] text-[#e5e5e5]",
    keypadBtnHover: "hover:bg-[#353535]",
    keypadBtnPressed: "bg-blue-600/80 text-white border-blue-400",
    keypadBorder: "border-[#3a3a3a]",
  },
  light: {
    windowBg: "bg-[#ffffff]",
    titleBarBg: "bg-[#f3f4f6]",
    menuBarBg: "bg-[#e5e7eb]",
    textColor: "text-[#09090b]",
    borderColor: "border-[#d1d5db]",
    dropdownBg: "bg-[#ffffff]",
    dropdownHover: "hover:bg-[#f3f4f6]",
    keypadPanelBg: "bg-[#f2f2f2]",
    keypadBtnBg: "bg-[#ffffff] text-[#1e1e1e]",
    keypadBtnHover: "hover:bg-[#e8eaed]",
    keypadBtnPressed: "bg-blue-600 text-white border-blue-700",
    keypadBorder: "border-slate-300",
  },
};

interface VirtualKeypadPanelProps {
  theme: ThemePalette;
  activeKey: string | null;
  onPressKey: (keyId: string, label: string) => void;
  onClose: () => void;
}

function VirtualKeypadPanel({
  theme,
  activeKey,
  onPressKey,
  onClose,
}: VirtualKeypadPanelProps) {
  const keyClass = (keyId: string) =>
    `flex items-center justify-center rounded-[5px] border text-[9px] font-bold font-sans leading-none select-none cursor-pointer transition-[transform,background-color,border-color,color,box-shadow] duration-200 ease-out ${
      activeKey === keyId
        ? `${theme.keypadBtnPressed} translate-y-[1px] scale-[0.96] shadow-inner`
        : `${theme.keypadBtnBg} ${theme.keypadBorder} ${theme.keypadBtnHover} shadow-[0_1px_0_rgba(0,0,0,0.18)]`
    }`;

  return (
    <div
      className={`h-[136px] px-1.5 py-1.5 flex flex-col gap-1 border-t ${theme.borderColor} ${theme.keypadPanelBg}`}
      aria-label="Virtual Keypad"
    >
      {/* Top Bar: Menu (SOFT1), Close, Back (SOFT2) */}
      <div className="grid grid-cols-3 gap-1 h-[22px] shrink-0">
        <button
          type="button"
          onClick={() => onPressKey("SOFT1", "Menu (SOFT1)")}
          className={keyClass("SOFT1")}
        >
          Menu
        </button>
        <button
          type="button"
          onClick={onClose}
          className={keyClass("CLOSE")}
          title="Close virtual buttons"
        >
          Close
        </button>
        <button
          type="button"
          onClick={() => onPressKey("SOFT2", "Back (SOFT2)")}
          className={keyClass("SOFT2")}
        >
          Back
        </button>
      </div>

      {/* Main Split: 3x4 Numeric Keypad (Left) + 3x3 8-Way D-Pad (Right) */}
      <div className="grid grid-cols-2 gap-1.5 flex-1 min-h-0">
        <div className="grid grid-cols-3 grid-rows-4 gap-[2px]">
          {keypadDigits.map((digit) => {
            const keyId = `KEY_${digit}`;
            return (
              <button
                key={keyId}
                type="button"
                onClick={() => onPressKey(keyId, digit)}
                className={keyClass(keyId)}
              >
                {digit}
              </button>
            );
          })}
        </div>

        <div className="grid grid-cols-3 grid-rows-3 gap-[2px]">
          {dpadButtons.map((btn) => (
            <button
              key={btn.id}
              type="button"
              title={btn.title}
              onClick={() => onPressKey(btn.id, btn.title)}
              className={`${keyClass(btn.id)} ${btn.id === "OK" ? "text-[8.5px] tracking-tight" : "text-[9px]"}`}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

export function CinematicTerminal() {
  const { resolvedTheme } = useTheme();
  const mounted = useSyncExternalStore(
    () => () => {},
    () => true,
    () => false
  );

  const isLight = mounted && resolvedTheme === "light";
  const [appState, setAppState] = useState<AppState>("typing");
  const [terminalText, setTerminalText] = useState("");
  const [bootLogs, setBootLogs] = useState<string[]>([]);
  const [selectedAppId, setSelectedAppId] = useState("ninja_offline");

  const [graphicsFilter, setGraphicsFilter] = useState<GraphicsFilter>("nearest");
  const [uiThemeState, setUiThemeState] = useState<EmulatorTheme | null>(null);
  const defaultTheme: EmulatorTheme = isLight ? "light" : "dracula";
  const uiTheme: EmulatorTheme = uiThemeState ?? defaultTheme;
  const activeTheme = themeStyles[uiTheme];

  const [activeMenu, setActiveMenu] = useState<string | null>(null);
  const [isAboutOpen, setIsAboutOpen] = useState(false);
  const [isSystemInfoOpen, setIsSystemInfoOpen] = useState(false);
  const [isFileChooserOpen, setIsFileChooserOpen] = useState(false);

  // Virtual Buttons demonstration state
  const [showVirtualKeypad, setShowVirtualKeypad] = useState(false);
  const [activeVirtualKey, setActiveVirtualKey] = useState<string | null>(null);
  const [keypadOsdLabel, setKeypadOsdLabel] = useState<string | null>(null);
  const [statusNotice, setStatusNotice] = useState<string | null>(null);
  const [autoKeypadEnabled, setAutoKeypadEnabled] = useState(true);

  const logTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const noticeTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const osdTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const pressTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  const flashStatusNotice = useCallback((msg: string, duration = 1600) => {
    if (noticeTimerRef.current) clearTimeout(noticeTimerRef.current);
    setStatusNotice(msg);
    noticeTimerRef.current = setTimeout(() => {
      setStatusNotice(null);
    }, duration);
  }, []);

  const flashOsd = useCallback((label: string, duration = 1300) => {
    if (osdTimerRef.current) clearTimeout(osdTimerRef.current);
    setKeypadOsdLabel(label);
    osdTimerRef.current = setTimeout(() => {
      setKeypadOsdLabel(null);
    }, duration);
  }, []);

  const toggleVirtualKeypad = useCallback(
    (nextVisible: boolean, isManual = false) => {
      if (isManual) {
        setAutoKeypadEnabled(false);
        setActiveVirtualKey(null);
      }
      setShowVirtualKeypad(nextVisible);
      flashStatusNotice(
        nextVisible ? "Virtual Buttons: Enabled" : "Virtual Buttons: Disabled"
      );
      flashOsd(nextVisible ? "Virtual Buttons · Ctrl+B" : "Virtual Buttons · Closed");
    },
    [flashOsd, flashStatusNotice]
  );

  const handleManualVirtualKeyPress = useCallback(
    (keyId: string, label: string) => {
      setAutoKeypadEnabled(false);
      if (pressTimerRef.current) clearTimeout(pressTimerRef.current);
      setActiveVirtualKey(keyId);
      flashStatusNotice(`Virtual Key: ${label}`, 900);
      pressTimerRef.current = setTimeout(() => {
        setActiveVirtualKey(null);
      }, 180);
    },
    [flashStatusNotice]
  );

  const resetWorkstation = useCallback(() => {
    if (logTimerRef.current) clearTimeout(logTimerRef.current);
    if (noticeTimerRef.current) clearTimeout(noticeTimerRef.current);
    if (osdTimerRef.current) clearTimeout(osdTimerRef.current);
    if (pressTimerRef.current) clearTimeout(pressTimerRef.current);
    setAppState("typing");
    setTerminalText("");
    setBootLogs([]);
    setActiveMenu(null);
    setIsAboutOpen(false);
    setIsSystemInfoOpen(false);
    setIsFileChooserOpen(false);
    setShowVirtualKeypad(false);
    setActiveVirtualKey(null);
    setKeypadOsdLabel(null);
    setStatusNotice(null);
    setAutoKeypadEnabled(true);
  }, []);

  // Close JMenuBar dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setActiveMenu(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Typing phase
  useEffect(() => {
    if (appState !== "typing") return;

    const command = "java -jar neutron.jar";
    let index = 0;
    let bootDelayTimer: ReturnType<typeof setTimeout> | null = null;

    const interval = setInterval(() => {
      if (index < command.length) {
        const char = command.charAt(index);
        setTerminalText((prev) => prev + char);
        index++;
      } else {
        clearInterval(interval);
        bootDelayTimer = setTimeout(() => {
          setAppState("booting");
        }, 700);
      }
    }, 75);

    return () => {
      clearInterval(interval);
      if (bootDelayTimer) clearTimeout(bootDelayTimer);
    };
  }, [appState]);

  // Boot logs phase
  useEffect(() => {
    if (appState !== "booting") return;

    const logs = [
      "Starting JVM Runtime Environment",
      "Loading Neutron Emulator Core v1.5.0",
      "Configuring execution subsystems",
      "  - Display: Canvas 320x240 @ 60 FPS",
      "  - Input: System keyboard + Virtual Keypad",
      "  - Audio: JSR-135 Player (PCM 44.1kHz)",
      "  - Storage: JSR-75 FileConnection (/sdcard)",
      "Mounting application catalogue: './games/'",
      "Scanning MIDlet Suite jar files",
      "Found 4 verified Java ME Midlets.",
      "Launching J2ME Suite Selector Menu",
    ];

    let logIndex = 0;

    const addLog = () => {
      if (logIndex < logs.length) {
        setBootLogs((prev) => [...prev, logs[logIndex]]);
        logIndex++;
        const nextDelay = logIndex > 4 ? 180 : 320;
        logTimerRef.current = setTimeout(addLog, nextDelay);
      } else {
        logTimerRef.current = setTimeout(() => {
          setAppState("launcher");
        }, 850);
      }
    };

    addLog();

    return () => {
      if (logTimerRef.current) clearTimeout(logTimerRef.current);
    };
  }, [appState]);

  // Launcher autopilot phase
  useEffect(() => {
    if (appState !== "launcher") return;

    const timer0 = setTimeout(() => {
      setSelectedAppId("ninja_offline");
    }, 0);

    const timer1 = setTimeout(() => {
      setActiveMenu("run");
    }, 900);

    const timer2 = setTimeout(() => {
      setActiveMenu(null);
      setIsFileChooserOpen(true);
    }, 1900);

    const timer3 = setTimeout(() => {
      setIsFileChooserOpen(false);
      setAppState("gameplay");
    }, 3400);

    return () => {
      clearTimeout(timer0);
      clearTimeout(timer1);
      clearTimeout(timer2);
      clearTimeout(timer3);
    };
  }, [appState]);

  // Attract Mode: cycle through showcase GIFs cleanly
  useEffect(() => {
    if (appState !== "gameplay") return;

    const gameSequence = ["ninja_offline", "bounce_tales", "gokart", "rollercoaster"];
    const currentDuration = gameDurations[selectedAppId] || 6000;

    const timer = setTimeout(() => {
      const currentIndex = gameSequence.indexOf(selectedAppId);
      const nextIndex = (currentIndex + 1) % gameSequence.length;
      setSelectedAppId(gameSequence[nextIndex]);
    }, currentDuration);

    return () => clearTimeout(timer);
  }, [appState, selectedAppId]);

  // Cinematic Virtual Buttons cycle: popup -> simulated inputs -> retract -> repeat
  useEffect(() => {
    if (appState !== "gameplay" || !autoKeypadEnabled) return;

    const timers: ReturnType<typeof setTimeout>[] = [];
    const schedule = (fn: () => void, ms: number) => {
      const t = setTimeout(fn, ms);
      timers.push(t);
    };

    const runCycle = () => {
      // 1. Open Virtual Buttons via simulated Ctrl+B toggle
      schedule(() => {
        setShowVirtualKeypad(true);
        flashStatusNotice("Virtual Buttons: Enabled (Ctrl+B)", 2000);
        flashOsd("Virtual Buttons · Ctrl+B", 1800);
      }, 0);

      // 2. Deliberate, one-at-a-time button presses after drawer opens
      const pressSequence: Array<{ offset: number; key: string; holdMs: number }> = [
        { offset: 1100, key: "RIGHT", holdMs: 520 },
        { offset: 2350, key: "UP", holdMs: 520 },
        { offset: 3600, key: "OK", holdMs: 560 },
        { offset: 4900, key: "KEY_5", holdMs: 520 },
        { offset: 6150, key: "SOFT1", holdMs: 520 },
      ];

      pressSequence.forEach(({ offset, key, holdMs }) => {
        schedule(() => {
          setActiveVirtualKey(key);
        }, offset);
        schedule(() => {
          setActiveVirtualKey((prev) => (prev === key ? null : prev));
        }, offset + holdMs);
      });

      // 3. Press Close button clearly, release, and smoothly retract
      schedule(() => {
        setActiveVirtualKey("CLOSE");
      }, 7500);

      schedule(() => {
        setActiveVirtualKey(null);
      }, 7980);

      schedule(() => {
        setShowVirtualKeypad(false);
        flashStatusNotice("Virtual Buttons: Disabled", 1600);
      }, 8200);

      // 4. Pause before repeating the showcase cycle
      schedule(runCycle, 26000);
    };

    schedule(runCycle, 1600);

    return () => {
      timers.forEach(clearTimeout);
    };
  }, [appState, autoKeypadEnabled, flashOsd, flashStatusNotice]);

  const isBooted = appState === "launcher" || appState === "gameplay";

  return (
    <div className="w-full flex flex-col items-center">
      {/* Interactive Controls Header */}
      <div className="flex items-center justify-between gap-2 w-full max-w-[322px] mb-4 px-3 py-1.5 border border-border bg-card/60 backdrop-blur-sm rounded-xl">
        <div className="flex items-center gap-1.5 min-w-0">
          <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse shrink-0" />
          <span className="text-[10px] font-mono text-muted-foreground truncate">
            Status: <span className="text-foreground font-semibold uppercase">{appState}</span>
          </span>
        </div>
        <div className="flex items-center gap-1.5 shrink-0">
          {isBooted && (
            <button
              type="button"
              onClick={() => toggleVirtualKeypad(!showVirtualKeypad, true)}
              className={`flex items-center gap-1 px-1.5 py-0.5 rounded border text-[9px] font-mono transition-all ${
                showVirtualKeypad
                  ? "border-cyan-accent/50 bg-cyan-accent/15 text-cyan-700 dark:text-cyan-400"
                  : "border-border bg-muted/30 text-muted-foreground hover:text-foreground hover:bg-muted/60"
              }`}
              title="Toggle Virtual Buttons (Ctrl+B)"
            >
              <Gamepad2 className="h-2.5 w-2.5" />
              <span>Keypad</span>
            </button>
          )}
          <button
            type="button"
            onClick={resetWorkstation}
            className="flex items-center p-1 rounded border border-border bg-muted/30 text-muted-foreground hover:text-foreground hover:bg-muted/60 transition-all"
            title="Reset Workstation"
            aria-label="Reset Workstation"
          >
            <RefreshCw className="h-2.5 w-2.5" />
          </button>
        </div>
      </div>

      {/* Responsive Frame Container */}
      <div className="relative w-full max-w-[322px] flex items-center justify-center">
        <AnimatePresence mode="wait">
          {/* 1. Terminal Window (Typing / Booting Phase) */}
          {!isBooted ? (
            <motion.div
              key="terminal-window"
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              transition={{ duration: 0.25 }}
              className={`w-full h-[320px] rounded-xl border overflow-hidden font-mono flex flex-col justify-between transition-all duration-300 ${
                isLight
                  ? "border-slate-300 bg-slate-50 text-slate-800 shadow-md"
                  : "border-neutral-900 bg-black/95 text-neutral-300 shadow-xl"
              }`}
            >
              {/* Terminal Header */}
              <div
                className={`flex items-center justify-between border-b px-3 h-[30px] shrink-0 transition-all duration-300 ${
                  isLight ? "border-slate-300 bg-slate-200/80" : "border-neutral-900 bg-neutral-950"
                }`}
              >
                <div className="flex items-center gap-1.5">
                  <span className="h-2.5 w-2.5 rounded-full bg-red-500/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-yellow-500/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-green-500/80" />
                  <span
                    className={`ml-1 text-[10px] transition-colors duration-300 ${
                      isLight ? "text-slate-600" : "text-neutral-400"
                    }`}
                  >
                    bash - neutron@host
                  </span>
                </div>
                <TerminalIcon
                  className={`h-3 w-3 transition-colors duration-300 ${
                    isLight ? "text-slate-500" : "text-neutral-500"
                  }`}
                />
              </div>

              {/* Terminal Body */}
              <div
                className={`p-3 flex-1 overflow-y-auto text-[10px] leading-relaxed select-none flex flex-col justify-start transition-colors duration-300 ${
                  isLight ? "text-slate-700" : "text-neutral-300"
                }`}
              >
                <div className="flex items-center gap-1 mb-1.5">
                  <span
                    className={`font-bold transition-colors duration-300 ${
                      isLight ? "text-emerald-600" : "text-emerald-400"
                    }`}
                  >
                    nsomatrix@neutron:~$
                  </span>
                  <span
                    className={`font-medium transition-colors duration-300 ${
                      isLight ? "text-slate-900" : "text-white"
                    }`}
                  >
                    {terminalText}
                  </span>
                  {appState === "typing" && (
                    <motion.span
                      animate={{ opacity: [1, 0] }}
                      transition={{ repeat: Infinity, duration: 0.8 }}
                      className={`inline-block w-1 h-3 transition-colors duration-300 ${
                        isLight ? "bg-slate-800" : "bg-white"
                      }`}
                    />
                  )}
                </div>

                <div
                  className={`space-y-0.5 mt-1 font-medium transition-colors duration-300 ${
                    isLight ? "text-slate-600" : "text-neutral-400"
                  }`}
                >
                  {bootLogs.map((log, idx) => {
                    if (!log) return null;
                    const isCyanBullet = log.trim().startsWith("-");
                    const isGreenBullet = log.startsWith("Found") || log.startsWith("VM booted");
                    return (
                      <motion.div
                        initial={{ opacity: 0, x: -4 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.12 }}
                        key={idx}
                        className="flex items-start gap-1"
                      >
                        {isCyanBullet ? (
                          <span className={`shrink-0 font-bold ${isLight ? "text-cyan-600" : "text-cyan-500"}`}>
                            ▸
                          </span>
                        ) : isGreenBullet ? (
                          <span className={`shrink-0 font-bold ${isLight ? "text-emerald-600" : "text-emerald-500"}`}>
                            ✓
                          </span>
                        ) : (
                          <span className={`shrink-0 font-bold ${isLight ? "text-slate-400" : "text-neutral-600"}`}>
                            ::
                          </span>
                        )}
                        <span>{log}</span>
                      </motion.div>
                    );
                  })}
                </div>
              </div>
            </motion.div>
          ) : (
            /* 2. Swing Emulator JFrame Window (Launcher / Gameplay + Retractable Virtual Keypad) */
            <motion.div
              key="emulator-window"
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              transition={{ duration: 0.25 }}
              className={`w-full rounded-xl border ${activeTheme.borderColor} ${activeTheme.windowBg} ${activeTheme.textColor} shadow-2xl flex flex-col relative select-none overflow-hidden`}
            >
              {/* A. JFrame Window Title Bar (30px) */}
              <div
                className={`flex items-center justify-between px-3 h-[30px] ${activeTheme.titleBarBg} border-b ${activeTheme.borderColor} shrink-0`}
              >
                <div className="flex items-center gap-1.5">
                  <Image
                    src={logoImg}
                    alt="Neutron Logo"
                    className="h-3.5 w-3.5 object-contain shrink-0"
                    width={14}
                    height={14}
                  />
                  <span className="text-[11px] font-bold tracking-wide font-sans truncate">
                    Neutron
                  </span>
                </div>

                <div className="flex items-center gap-1">
                  <span className="p-0.5 text-neutral-400/70" aria-hidden="true">
                    <Minimize2 className="h-2.5 w-2.5" />
                  </span>
                  <span className="p-0.5 text-neutral-400/70" aria-hidden="true">
                    <Square className="h-2 w-2" />
                  </span>
                  <button
                    type="button"
                    onClick={resetWorkstation}
                    className="p-0.5 hover:bg-red-500/80 hover:text-white rounded text-neutral-400 transition-colors"
                    title="Close Emulator"
                    aria-label="Close Emulator"
                  >
                    <X className="h-2.5 w-2.5" />
                  </button>
                </div>
              </div>

              {/* B. JMenuBar (24px) */}
              <div
                ref={menuRef}
                className={`flex items-center gap-1 px-2 h-[24px] ${activeTheme.menuBarBg} border-b ${activeTheme.borderColor} text-[11px] font-medium font-sans shrink-0 relative z-30`}
              >
                {/* Run */}
                <div className="relative">
                  <button
                    type="button"
                    onClick={() => setActiveMenu(activeMenu === "run" ? null : "run")}
                    className={`px-2 py-0.5 rounded hover:bg-neutral-500/20 transition-all ${
                      activeMenu === "run" ? "bg-neutral-500/20" : ""
                    }`}
                  >
                    Run
                  </button>
                  {activeMenu === "run" && (
                    <div
                      className={`absolute left-0 mt-1 w-32 rounded-lg border ${activeTheme.borderColor} ${activeTheme.dropdownBg} shadow-xl p-1 text-[11px] flex flex-col gap-0.5`}
                    >
                      <button
                        type="button"
                        onClick={() => {
                          setIsFileChooserOpen(true);
                          setActiveMenu(null);
                        }}
                        className={`w-full text-left px-2 py-1 rounded ${activeTheme.textColor} ${activeTheme.dropdownHover} transition-colors`}
                      >
                        Run JAR File
                      </button>
                    </div>
                  )}
                </div>

                {/* Config */}
                <div className="relative">
                  <button
                    type="button"
                    onClick={() => setActiveMenu(activeMenu === "config" ? null : "config")}
                    className={`px-2 py-0.5 rounded hover:bg-neutral-500/20 transition-all ${
                      activeMenu === "config" ? "bg-neutral-500/20" : ""
                    }`}
                  >
                    Config
                  </button>
                  {activeMenu === "config" && (
                    <div
                      className={`absolute left-0 mt-1 w-44 rounded-lg border ${activeTheme.borderColor} ${activeTheme.dropdownBg} shadow-xl p-1 text-[11px] flex flex-col gap-0.5`}
                    >
                      <button
                        type="button"
                        onClick={() => {
                          toggleVirtualKeypad(!showVirtualKeypad, true);
                          setActiveMenu(null);
                        }}
                        className={`w-full flex items-center justify-between px-2 py-1 rounded ${activeTheme.textColor} ${activeTheme.dropdownHover} transition-colors`}
                      >
                        <span className="flex items-center gap-1.5">
                          <Check className={`h-3 w-3 ${showVirtualKeypad ? "opacity-100 text-emerald-500" : "opacity-0"}`} />
                          Virtual Buttons
                        </span>
                        <span className="text-[9px] text-neutral-400 font-mono">Ctrl+B</span>
                      </button>
                      <div className={`my-0.5 border-t ${activeTheme.borderColor}`} />
                      {(["nearest", "scanlines", "lcd"] as const).map((filter) => (
                        <button
                          key={filter}
                          type="button"
                          onClick={() => {
                            setGraphicsFilter(filter);
                            setActiveMenu(null);
                          }}
                          className={`w-full flex items-center gap-1.5 px-2 py-1 rounded capitalize ${activeTheme.textColor} ${activeTheme.dropdownHover} transition-colors`}
                        >
                          <Check className={`h-3 w-3 ${graphicsFilter === filter ? "opacity-100 text-primary" : "opacity-0"}`} />
                          {filter === "lcd" ? "LCD Grid" : filter === "scanlines" ? "CRT Scanlines" : "Nearest Neighbor"}
                        </button>
                      ))}
                      <div className={`my-0.5 border-t ${activeTheme.borderColor}`} />
                      {(["dracula", "dark", "light"] as const).map((t) => (
                        <button
                          key={t}
                          type="button"
                          onClick={() => {
                            setUiThemeState(t);
                            setActiveMenu(null);
                          }}
                          className={`w-full flex items-center gap-1.5 px-2 py-1 rounded capitalize ${activeTheme.textColor} ${activeTheme.dropdownHover} transition-colors`}
                        >
                          <Check className={`h-3 w-3 ${uiTheme === t ? "opacity-100 text-primary" : "opacity-0"}`} />
                          Theme: {t}
                        </button>
                      ))}
                    </div>
                  )}
                </div>

                {/* Controls */}
                <span className="px-2 py-0.5 text-neutral-400 select-none cursor-default font-medium">
                  Controls
                </span>

                {/* Misc */}
                <div className="relative">
                  <button
                    type="button"
                    onClick={() => setActiveMenu(activeMenu === "misc" ? null : "misc")}
                    className={`px-2 py-0.5 rounded hover:bg-neutral-500/20 transition-all ${
                      activeMenu === "misc" ? "bg-neutral-500/20" : ""
                    }`}
                  >
                    Misc
                  </button>
                  {activeMenu === "misc" && (
                    <div
                      className={`absolute left-0 mt-1 w-36 rounded-lg border ${activeTheme.borderColor} ${activeTheme.dropdownBg} shadow-xl p-1 text-[11px] flex flex-col gap-0.5`}
                    >
                      <button
                        type="button"
                        onClick={() => {
                          setIsAboutOpen(true);
                          setIsSystemInfoOpen(false);
                          setActiveMenu(null);
                        }}
                        className={`w-full text-left px-2 py-1 rounded ${activeTheme.textColor} ${activeTheme.dropdownHover}`}
                      >
                        About
                      </button>
                      <button
                        type="button"
                        onClick={() => {
                          setIsSystemInfoOpen(true);
                          setIsAboutOpen(false);
                          setActiveMenu(null);
                        }}
                        className={`w-full text-left px-2 py-1 rounded ${activeTheme.textColor} ${activeTheme.dropdownHover}`}
                      >
                        System Info
                      </button>
                      <Link
                        href="/docs"
                        onClick={() => setActiveMenu(null)}
                        className={`w-full text-left px-2 py-1 rounded block ${activeTheme.textColor} ${activeTheme.dropdownHover}`}
                      >
                        Docs Help
                      </Link>
                    </div>
                  )}
                </div>
              </div>

              {/* C. JFrame Canvas Content Area (4:3, 320x240 at standard width) */}
              <div className="w-full aspect-[4/3] bg-neutral-950 relative overflow-hidden shrink-0">
                <AnimatePresence mode="wait">
                  {/* Launcher State */}
                  {appState === "launcher" && (
                    <motion.div
                      key="launcher"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className={`w-full h-full flex flex-col justify-between font-sans text-xs relative transition-colors duration-300 ${
                        uiTheme === "light"
                          ? "bg-slate-50 text-slate-800"
                          : uiTheme === "dark"
                            ? "bg-[#181818] text-[#fafafa]"
                            : "bg-[#1e1f29] text-[#f8f8f2]"
                      }`}
                    >
                      <div
                        className={`px-2 py-0.5 border-b flex justify-between items-center text-[9px] font-mono transition-colors duration-300 ${
                          uiTheme === "light"
                            ? "bg-slate-100 border-slate-200 text-slate-500"
                            : uiTheme === "dark"
                              ? "bg-neutral-900 border-neutral-800 text-neutral-400"
                              : "bg-[#1a1b23] border-[#2a2c3a] text-neutral-400"
                        }`}
                      >
                        <span>Neutron v1.5.0</span>
                        <span>MIDP 2.0</span>
                      </div>

                      <div className="flex flex-col items-center justify-center p-4 text-center gap-1.5 flex-1">
                        <div
                          className={`h-10 w-10 rounded-full flex items-center justify-center border animate-pulse transition-all duration-300 ${
                            uiTheme === "light"
                              ? "bg-slate-200/50 border-slate-300"
                              : uiTheme === "dark"
                                ? "bg-neutral-800/40 border-neutral-700/60"
                                : "bg-[#282a36]/50 border-[#44475a]/60"
                          }`}
                        >
                          <svg
                            className={`h-5 w-5 transition-colors duration-300 ${
                              uiTheme === "light" ? "text-slate-600" : "text-neutral-400"
                            }`}
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="1.5"
                          >
                            <rect x="5" y="2" width="14" height="20" rx="3" fill="currentColor" fillOpacity="0.1" />
                            <rect x="7" y="4" width="10" height="8" rx="1" fill="currentColor" fillOpacity="0.15" />
                            <circle cx="10" cy="16" r="1.5" />
                            <circle cx="14.5" cy="15.5" r="1" />
                            <circle cx="15.5" cy="17" r="1" />
                          </svg>
                        </div>
                        <p
                          className={`text-[10px] font-bold transition-colors duration-300 ${
                            uiTheme === "light" ? "text-slate-800" : "text-neutral-200"
                          }`}
                        >
                          No MIDlet Loaded
                        </p>
                        <p
                          className={`text-[8.5px] max-w-[200px] leading-normal transition-colors duration-300 ${
                            uiTheme === "light" ? "text-slate-500" : "text-neutral-400"
                          }`}
                        >
                          Go to{" "}
                          <span
                            className={`font-semibold font-mono ${
                              uiTheme === "light" ? "text-cyan-600" : "text-cyan-400"
                            }`}
                          >
                            Run &gt; Run JAR File
                          </span>{" "}
                          to load and execute a J2ME midlet archive.
                        </p>
                      </div>

                      <div
                        className={`px-2 py-0.5 border-t flex justify-between text-[8px] font-bold font-mono transition-colors duration-300 ${
                          uiTheme === "light"
                            ? "bg-slate-100 border-slate-200 text-slate-500"
                            : uiTheme === "dark"
                              ? "bg-neutral-900 border-neutral-800 text-neutral-400"
                              : "bg-[#1a1b23] border-[#2a2c3a] text-neutral-400"
                        }`}
                      >
                        <span>MENU</span>
                        <span>HELP</span>
                      </div>
                    </motion.div>
                  )}

                  {/* Gameplay State */}
                  {appState === "gameplay" && (
                    <motion.div
                      key="gameplay"
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0 }}
                      className="w-full h-full flex flex-col justify-between relative"
                    >
                      <div className="relative w-full h-full overflow-hidden bg-black flex items-center justify-center">
                        <AnimatePresence mode="wait">
                          <motion.div
                            key={selectedAppId}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.6, ease: "easeInOut" }}
                            className="w-full h-full absolute inset-0 select-none pointer-events-none"
                            onContextMenu={(e) => e.preventDefault()}
                          >
                            <Image
                              src={gameGifs[selectedAppId] || render1}
                              alt={gameNames[selectedAppId] || "Ninja School Offline"}
                              unoptimized
                              priority
                              draggable={false}
                              onContextMenu={(e) => e.preventDefault()}
                              className="w-full h-full object-cover select-none pointer-events-none"
                              style={{
                                imageRendering: "pixelated",
                                WebkitTouchCallout: "none",
                                WebkitUserSelect: "none",
                              }}
                            />
                          </motion.div>
                        </AnimatePresence>

                        {graphicsFilter === "scanlines" && (
                          <div className="absolute inset-0 pointer-events-none mix-blend-overlay opacity-60 bg-[linear-gradient(to_bottom,rgba(0,0,0,0.35)_50%,transparent_50%)] bg-[length:100%_2px] z-10" />
                        )}

                        {graphicsFilter === "lcd" && (
                          <div className="absolute inset-0 pointer-events-none mix-blend-overlay opacity-40 bg-[linear-gradient(to_right,rgba(0,0,0,0.25)_1px,transparent_1px),linear-gradient(to_bottom,rgba(0,0,0,0.25)_1px,transparent_1px)] bg-[length:3px_3px] z-10" />
                        )}

                        {/* Subtle screen reflection */}
                        <div className="absolute inset-0 pointer-events-none bg-gradient-to-tr from-transparent via-white/[0.02] to-white/[0.06] mix-blend-screen z-15" />

                        {/* Cinematic OSD toast when Virtual Buttons popup/retract */}
                        <AnimatePresence>
                          {keypadOsdLabel && (
                            <motion.div
                              key={keypadOsdLabel}
                              initial={{ opacity: 0, y: 6, scale: 0.96 }}
                              animate={{ opacity: 1, y: 0, scale: 1 }}
                              exit={{ opacity: 0, y: 4, scale: 0.96 }}
                              transition={{ duration: 0.2 }}
                              className="absolute bottom-2 left-1/2 -translate-x-1/2 z-20 pointer-events-none flex items-center gap-1.5 rounded-full border border-white/15 bg-black/75 px-2.5 py-0.5 text-[9px] font-mono text-white backdrop-blur-md shadow-lg whitespace-nowrap"
                            >
                              <Gamepad2 className="h-2.5 w-2.5 text-cyan-400 shrink-0" />
                              <span>{keypadOsdLabel}</span>
                            </motion.div>
                          )}
                        </AnimatePresence>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* Modal 1: About Panel */}
                <AnimatePresence>
                  {isAboutOpen && (
                    <motion.div
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div
                        className={`w-[260px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2.5 text-xs shadow-2xl`}
                      >
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1.5">
                            <Info className="h-3.5 w-3.5 text-primary" /> About Neutron
                          </span>
                          <button
                            type="button"
                            onClick={() => setIsAboutOpen(false)}
                            className="hover:bg-neutral-500/20 rounded p-0.5"
                          >
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>
                        <div className="space-y-1 text-[11px] text-neutral-400">
                          <p className={`${activeTheme.textColor} font-bold text-xs`}>Neutron Emulator v1.5.0</p>
                          <p>
                            Vendor: <span className="font-semibold text-neutral-300">NSOMatrix</span>
                          </p>
                          <p>
                            CoreDev: <span className="font-semibold text-neutral-300">Manish K</span>
                          </p>
                          <p>Platform: Java SE / Swing GUI</p>
                          <p>License: LGPL v2.1 / Apache 2.0</p>
                        </div>
                        <button
                          type="button"
                          onClick={() => setIsAboutOpen(false)}
                          className="mt-1 w-full py-1 rounded bg-primary text-white text-[11px] font-semibold hover:bg-primary/90 active:scale-95 transition-all"
                        >
                          OK
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* Modal 2: System Info Panel */}
                <AnimatePresence>
                  {isSystemInfoOpen && (
                    <motion.div
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div
                        className={`w-[270px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2 text-xs shadow-2xl`}
                      >
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1.5">
                            <Cpu className="h-3.5 w-3.5 text-cyan-400" /> System Properties
                          </span>
                          <button
                            type="button"
                            onClick={() => setIsSystemInfoOpen(false)}
                            className="hover:bg-neutral-500/20 rounded p-0.5"
                          >
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>
                        <div className="max-h-[160px] overflow-y-auto font-mono text-[10px] text-neutral-400 space-y-1.5 pr-1">
                          <div className="flex justify-between border-b border-neutral-800/40 pb-0.5">
                            <span className="text-neutral-500">java.version</span>
                            <span className="text-neutral-200">17.0.13 (LTS)</span>
                          </div>
                          <div className="flex justify-between border-b border-neutral-800/40 pb-0.5">
                            <span className="text-neutral-500">os.name</span>
                            <span className="text-neutral-200">Linux / X11</span>
                          </div>
                          <div className="flex justify-between border-b border-neutral-800/40 pb-0.5">
                            <span className="text-neutral-500">swing.defaultlaf</span>
                            <span className="text-neutral-200">FlatLaf Dracula</span>
                          </div>
                          <div className="flex justify-between border-b border-neutral-800/40 pb-0.5">
                            <span className="text-neutral-500">max.heap.memory</span>
                            <span className="text-neutral-200">512 MB</span>
                          </div>
                          <div className="flex justify-between pb-0.5">
                            <span className="text-neutral-500">active.threads</span>
                            <span className="text-neutral-200">4 (EDT + 3 VM)</span>
                          </div>
                        </div>
                        <button
                          type="button"
                          onClick={() => setIsSystemInfoOpen(false)}
                          className="mt-1.5 w-full py-1 rounded bg-primary text-white text-[11px] font-semibold hover:bg-primary/90 active:scale-95 transition-all"
                        >
                          Close
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* Modal 3: JFileChooser Dialog */}
                <AnimatePresence>
                  {isFileChooserOpen && (
                    <motion.div
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div
                        className={`w-[280px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2 text-xs shadow-2xl`}
                      >
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1.5">
                            <Folder className="h-3.5 w-3.5 text-primary" />
                            Open MIDlet Archive
                          </span>
                          <button
                            type="button"
                            onClick={() => setIsFileChooserOpen(false)}
                            className="hover:bg-neutral-500/20 rounded p-0.5"
                          >
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>

                        <div
                          className={`rounded border p-1 max-h-[140px] overflow-y-auto space-y-0.5 transition-colors duration-300 ${
                            uiTheme === "light"
                              ? "bg-slate-100 border-slate-200"
                              : "bg-neutral-950/80 border-neutral-800/80"
                          }`}
                        >
                          {jarFiles.map((file) => {
                            const isSelected = selectedAppId === file.id;
                            return (
                              <div
                                key={file.id}
                                onClick={() => setSelectedAppId(file.id)}
                                className={`flex items-center justify-between px-2 py-1 rounded cursor-pointer text-[10px] font-mono transition-all border ${
                                  isSelected
                                    ? uiTheme === "light"
                                      ? "bg-blue-600/10 text-blue-600 border-blue-600/30 font-semibold"
                                      : "bg-cyan-500/10 text-cyan-400 border-cyan-500/30 font-semibold"
                                    : uiTheme === "light"
                                      ? "text-slate-700 hover:text-slate-900 hover:bg-slate-200/60 border-transparent"
                                      : "text-neutral-400 hover:text-neutral-100 hover:bg-neutral-900/60 border-transparent"
                                }`}
                              >
                                <span className="flex items-center gap-1.5 truncate">
                                  <FileArchive
                                    className={`h-3 w-3 shrink-0 ${
                                      isSelected
                                        ? uiTheme === "light"
                                          ? "text-blue-600"
                                          : "text-cyan-400"
                                        : uiTheme === "light"
                                          ? "text-slate-400"
                                          : "text-neutral-500"
                                    }`}
                                  />
                                  <span className="truncate">{file.filename}</span>
                                </span>
                                <span
                                  className={`text-[9px] font-sans shrink-0 ml-2 ${
                                    uiTheme === "light" ? "text-slate-400" : "text-neutral-500"
                                  }`}
                                >
                                  {file.size}
                                </span>
                              </div>
                            );
                          })}
                        </div>

                        <div className="flex justify-end gap-1.5 mt-1">
                          <button
                            type="button"
                            onClick={() => setIsFileChooserOpen(false)}
                            className={`px-3 py-1 rounded border text-[10px] transition-colors ${
                              uiTheme === "light"
                                ? "border-slate-300 text-slate-700 hover:bg-slate-100/80"
                                : "border-neutral-700 text-neutral-300 hover:bg-neutral-800"
                            }`}
                          >
                            Cancel
                          </button>
                          <button
                            type="button"
                            onClick={() => {
                              setIsFileChooserOpen(false);
                              setAppState("gameplay");
                            }}
                            className={`px-4 py-1 rounded text-white text-[10px] font-semibold hover:opacity-90 active:scale-95 transition-all ${
                              uiTheme === "light"
                                ? "bg-blue-600"
                                : uiTheme === "dracula"
                                  ? "bg-[#bd93f9]"
                                  : "bg-primary"
                            }`}
                          >
                            Open
                          </button>
                        </div>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>

              {/* D. Retractable On-Screen Virtual Buttons Drawer (BorderLayout.SOUTH) */}
              <AnimatePresence initial={false}>
                {showVirtualKeypad && (
                  <motion.div
                    key="virtual-keypad-drawer"
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: 136, opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    transition={{ duration: 0.52, ease: [0.22, 1, 0.36, 1] }}
                    className="overflow-hidden shrink-0"
                  >
                    <VirtualKeypadPanel
                      theme={activeTheme}
                      activeKey={activeVirtualKey}
                      onPressKey={handleManualVirtualKeyPress}
                      onClose={() => toggleVirtualKeypad(false, true)}
                    />
                  </motion.div>
                )}
              </AnimatePresence>

              {/* E. SwingStatusBar (26px) */}
              <div
                className={`px-3 h-[26px] ${activeTheme.titleBarBg} border-t ${activeTheme.borderColor} text-[9px] flex items-center justify-between text-neutral-400 font-mono shrink-0`}
              >
                <div className="flex items-center gap-1.5 truncate max-w-[210px]">
                  <span className="flex h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse shrink-0" />
                  <span className="truncate">
                    {statusNotice
                      ? statusNotice
                      : appState === "gameplay"
                        ? `Running: ${gameNames[selectedAppId] || "Ninja School Offline"} (1.0x)`
                        : "Launcher active."}
                  </span>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  <span>320x240</span>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}
