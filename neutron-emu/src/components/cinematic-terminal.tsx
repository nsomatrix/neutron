"use client";

import React, { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image from "next/image";
import logoImg from "@/assets/ntn.png";
import render1 from "@/assets/render1.gif";
import render2 from "@/assets/render2.gif";
import render3 from "@/assets/render3.gif";
import render4 from "@/assets/render4.gif";
import { useTheme } from "next-themes";
import { 
  Terminal as TerminalIcon, RefreshCw, Play, Pause, Circle, 
  Cpu, X, Minimize2, Square, Info
} from "lucide-react";

// Game GIFs and Names mapping
const gameGifs: Record<string, any> = {
  ninja_offline: render1,
  ninja_world: render2,
  nsomatrix: render3,
  neutron: render4,
};

const gameNames: Record<string, string> = {
  ninja_offline: "Ninja School Offline",
  ninja_world: "Ninja School World",
  nsomatrix: "NSOMatrix",
  neutron: "Neutron",
};

const gameDurations: Record<string, number> = {
  ninja_offline: 4310, // 4910ms total - 600ms transition
  ninja_world: 5980,   // 6580ms total - 600ms transition
  nsomatrix: 5920,     // 6520ms total - 600ms transition
  neutron: 3490,       // 4090ms total - 600ms transition
};

// Types
type AppState = "typing" | "booting" | "launcher" | "gameplay";
type GraphicsFilter = "nearest" | "bilinear" | "scanlines" | "lcd";
type EmulatorTheme = "dark" | "light" | "dracula";

interface GameApp {
  id: string;
  name: string;
  developer: string;
  iconBg: string;
  iconSvg: React.ReactNode;
}

export function CinematicTerminal() {
  const { resolvedTheme } = useTheme();
  const [mounted, setMounted] = useState(false);
  
  useEffect(() => {
    setMounted(true);
  }, []);

  const isLight = mounted && resolvedTheme === "light";
  const [appState, setAppState] = useState<AppState>("typing");
  const [terminalText, setTerminalText] = useState("");
  const [bootLogs, setBootLogs] = useState<string[]>([]);
  const [selectedAppId, setSelectedAppId] = useState("ninja_offline");
  
  // Custom interactive settings linked to actual JMenuBar options
  const [graphicsFilter, setGraphicsFilter] = useState<GraphicsFilter>("nearest");
  const [emulationSpeed, setEmulationSpeed] = useState<number>(1.0);
  const [uiTheme, setUiTheme] = useState<EmulatorTheme>("dracula");
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [activeMenu, setActiveMenu] = useState<string | null>(null);
  
  // Modal dialog states mirroring JVM SwingDialogPanel
  const [isAboutOpen, setIsAboutOpen] = useState(false);
  const [isSystemInfoOpen, setIsSystemInfoOpen] = useState(false);
  const [isFileChooserOpen, setIsFileChooserOpen] = useState(false);

  const logTimerRef = useRef<NodeJS.Timeout | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  // J2ME Game Apps List
  const apps: GameApp[] = [
    {
      id: "ninja_offline",
      name: "Ninja School Offline",
      developer: "Teamobi",
      iconBg: "from-blue-900 to-indigo-950",
      iconSvg: (
        <svg className="w-10 h-10 text-cyan-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polygon points="12,2 22,22 12,17 2,22" fill="currentColor" fillOpacity="0.2" />
          <circle cx="12" cy="12" r="3" className="animate-pulse" />
        </svg>
      ),
    },
    {
      id: "ninja_world",
      name: "Ninja School World",
      developer: "Teamobi",
      iconBg: "from-orange-600 to-red-950",
      iconSvg: (
        <svg className="w-10 h-10 text-orange-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="8" fill="currentColor" fillOpacity="0.3" />
          <path d="M12,4 C14,8 14,16 12,20" />
        </svg>
      ),
    },
    {
      id: "nsomatrix",
      name: "NSOMatrix",
      developer: "nsomatrix",
      iconBg: "from-emerald-900 to-emerald-950",
      iconSvg: (
        <svg className="w-10 h-10 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M4,4 H20 V8 H8 V12 H20 V20 H4" strokeLinecap="round" strokeLinejoin="round" fill="none" />
          <rect x="2" y="2" width="4" height="4" fill="currentColor" />
        </svg>
      ),
    },
    {
      id: "neutron",
      name: "Neutron",
      developer: "nsomatrix",
      iconBg: "from-cyan-900 to-teal-950",
      iconSvg: (
        <svg className="w-10 h-10 text-cyan-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polygon points="12,2 22,9 12,22 2,9" fill="currentColor" fillOpacity="0.2" />
        </svg>
      ),
    },
  ];

  // Theme Styling Configuration mapping to FlatLaf variables
  const themeStyles = {
    dracula: {
      windowBg: "bg-[#282a36]",
      titleBarBg: "bg-[#1e1f29]",
      menuBarBg: "bg-[#21222c]",
      textColor: "text-[#f8f8f2]",
      borderColor: "border-[#44475a]",
      btnActive: "bg-[#bd93f9]/20 text-[#bd93f9]",
      accentGlow: "shadow-[#bd93f9]/20",
      dropdownBg: "bg-[#282a36]",
      dropdownHover: "hover:bg-[#44475a]",
    },
    dark: {
      windowBg: "bg-[#1e1e1e]",
      titleBarBg: "bg-[#121212]",
      menuBarBg: "bg-[#1a1a1a]",
      textColor: "text-[#fafafa]",
      borderColor: "border-[#2d2d2d]",
      btnActive: "bg-primary/20 text-primary",
      accentGlow: "shadow-primary/20",
      dropdownBg: "bg-[#1e1e1e]",
      dropdownHover: "hover:bg-[#2d2d2d]",
    },
    light: {
      windowBg: "bg-[#ffffff]",
      titleBarBg: "bg-[#f3f4f6]",
      menuBarBg: "bg-[#e5e7eb]",
      textColor: "text-[#09090b]",
      borderColor: "border-[#d1d5db]",
      btnActive: "bg-blue-600/10 text-blue-600",
      accentGlow: "shadow-blue-500/15",
      dropdownBg: "bg-[#ffffff]",
      dropdownHover: "hover:bg-[#f3f4f6]",
    }
  };

  const activeTheme = themeStyles[uiTheme];

  // Sync FlatLaf theme with global theme
  useEffect(() => {
    if (!resolvedTheme) return;
    if (resolvedTheme === "light") {
      setUiTheme("light");
    } else {
      setUiTheme("dracula");
    }
  }, [resolvedTheme]);

  // Click outside menu handler to close JMenuBar dropdowns
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setActiveMenu(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Typing Effect
  useEffect(() => {
    if (appState !== "typing") return;

    const command = "java -jar neutron.jar";
    let index = 0;
    setTerminalText("");
    
    const interval = setInterval(() => {
      if (index < command.length) {
        const char = command.charAt(index);
        setTerminalText((prev) => prev + char);
        index++;
      } else {
        clearInterval(interval);
        setTimeout(() => {
          setAppState("booting");
        }, 800);
      }
    }, 80);

    return () => clearInterval(interval);
  }, [appState]);

  // Boot Logs Streaming
  useEffect(() => {
    if (appState !== "booting") return;

    const logs = [
      "Starting JVM Runtime Environment",
      "Loading Neutron Emulator Core v1.0.0",
      "Configuring execution subsystems",
      "  - Display: Canvas 320x240 @ 60 FPS",
      "  - Input: System keyboard mapping mode",
      "  - Audio: JSR-135 Player (PCM 44.1kHz)",
      "  - Storage: JSR-75 FileConnection mount (/sdcard)",
      "Mounting application catalogue: './games/'",
      "Scanning MIDlet Suite jar files",
      "Found 6 verified Java ME Midlets.",
      "Launching J2ME Suite Selector Menu",
    ];

    setBootLogs([]);
    let logIndex = 0;

    const addLog = () => {
      if (logIndex < logs.length) {
        setBootLogs((prev) => [...prev, logs[logIndex]]);
        logIndex++;
        const nextDelay = logIndex > 4 ? 200 : 350;
        logTimerRef.current = setTimeout(addLog, nextDelay);
      } else {
        logTimerRef.current = setTimeout(() => {
          setAppState("launcher");
        }, 1000);
      }
    };

    addLog();

    return () => {
      if (logTimerRef.current) clearTimeout(logTimerRef.current);
    };
  }, [appState]);

  // Autopilot Animation: Demos loading a JAR via Run JAR File menu option
  useEffect(() => {
    if (appState !== "launcher") return;

    setSelectedAppId("ninja_offline");

    // 1. Open the Run Menu
    const timer1 = setTimeout(() => {
      setActiveMenu("run");
    }, 1000);

    // 2. Select Run JAR File -> open JFileChooser
    const timer2 = setTimeout(() => {
      setActiveMenu(null);
      setIsFileChooserOpen(true);
    }, 2200);

    // 3. Highlight and open the game
    const timer3 = setTimeout(() => {
      setIsFileChooserOpen(false);
      setAppState("gameplay");
    }, 3800);

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
      clearTimeout(timer3);
    };
  }, [appState]);


  // Attract Mode: Automatically cycle through all 4 games infinitely, playing each GIF exactly once per cycle
  useEffect(() => {
    if (appState !== "gameplay") return;

    const gameSequence = ["ninja_offline", "ninja_world", "nsomatrix", "neutron"];
    const currentDuration = gameDurations[selectedAppId] || 5000;

    const timer = setTimeout(() => {
      const currentIndex = gameSequence.indexOf(selectedAppId);
      const nextIndex = (currentIndex + 1) % gameSequence.length;
      const nextId = gameSequence[nextIndex];
      
      setSelectedAppId(nextId);
    }, currentDuration);

    return () => clearTimeout(timer);
  }, [appState, selectedAppId]);



  const isBooted = appState === "launcher" || appState === "gameplay";

  return (
    <div className="w-full flex flex-col items-center">
      {/* Interactive Controls Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 w-full max-w-[322px] mb-4 px-3 py-1.5 border border-border bg-card/60 backdrop-blur-sm rounded-xl">
        <div className="flex items-center gap-1.5">
          <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
          <span className="text-[10px] font-mono text-muted-foreground">
            Status: <span className="text-foreground font-semibold uppercase">{appState}</span>
          </span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              if (logTimerRef.current) clearTimeout(logTimerRef.current);
              setAppState("typing");
              setTerminalText("");
              setBootLogs([]);
              setIsAboutOpen(false);
              setIsSystemInfoOpen(false);
              setIsFileChooserOpen(false);
            }}
            className="flex items-center p-1 rounded border border-border bg-muted/30 text-muted-foreground hover:text-foreground hover:bg-muted/60 transition-all"
            title="Reset Workstation"
          >
            <RefreshCw className="h-2.5 w-2.5" />
          </button>
        </div>
      </div>

      {/* Identical Twin In-Place Window Transitions */}
      <div className="relative w-[322px] h-[320px] flex items-center justify-center">
        <AnimatePresence mode="wait">
          
          {/* 1. Terminal Window (Typing / Booting Phase) */}
          {!isBooted ? (
            <motion.div
              key="terminal-window"
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              transition={{ duration: 0.25 }}
              className={`w-full h-full rounded-xl border overflow-hidden font-mono flex flex-col justify-between transition-all duration-300 ${
                isLight 
                  ? "border-slate-300 bg-slate-50 text-slate-800 shadow-md" 
                  : "border-neutral-900 bg-black/95 text-neutral-300 shadow-xl"
              }`}
            >
              {/* Header */}
              <div className={`flex items-center justify-between border-b px-3 h-[30px] shrink-0 transition-all duration-300 ${
                isLight 
                  ? "border-slate-300 bg-slate-200/80" 
                  : "border-neutral-900 bg-neutral-950"
              }`}>
                <div className="flex items-center gap-1.5">
                  <span className="h-2.5 w-2.5 rounded-full bg-red-500/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-yellow-500/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-green-500/80" />
                  <span className={`ml-1 text-[10px] transition-colors duration-300 ${
                    isLight ? "text-slate-600" : "text-neutral-400"
                  }`}>bash - neutron@host</span>
                </div>
                <TerminalIcon className={`h-3 w-3 transition-colors duration-300 ${
                  isLight ? "text-slate-500" : "text-neutral-500"
                }`} />
              </div>

              {/* Body */}
              <div className={`p-3 flex-1 overflow-y-auto text-[10px] leading-relaxed select-none flex flex-col justify-start transition-colors duration-300 ${
                isLight ? "text-slate-700" : "text-neutral-300"
              }`}>
                <div className="flex items-center gap-1 mb-1.5">
                  <span className={`font-bold transition-colors duration-300 ${
                    isLight ? "text-emerald-600" : "text-emerald-400"
                  }`}>mackruize@neutron:~$</span>
                  <span className={`font-medium transition-colors duration-300 ${
                    isLight ? "text-slate-900" : "text-white"
                  }`}>{terminalText}</span>
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

                {/* Boot Logs */}
                <div className={`space-y-0.5 mt-1 font-medium transition-colors duration-300 ${
                  isLight ? "text-slate-600" : "text-neutral-400"
                }`}>
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
                          <span className={`shrink-0 font-bold ${isLight ? "text-cyan-600" : "text-cyan-500"}`}>▸</span>
                        ) : isGreenBullet ? (
                          <span className={`shrink-0 font-bold ${isLight ? "text-emerald-600" : "text-emerald-500"}`}>✓</span>
                        ) : (
                          <span className={`shrink-0 font-bold ${isLight ? "text-slate-400" : "text-neutral-600"}`}>::</span>
                        )}
                        <span>{log}</span>
                      </motion.div>
                    );
                  })}
                </div>
              </div>
            </motion.div>
          ) : (
            <motion.div
              key="emulator-window"
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              transition={{ duration: 0.25 }}
              className={`w-full h-full rounded-xl border ${activeTheme.borderColor} ${activeTheme.windowBg} ${activeTheme.textColor} shadow-2xl flex flex-col relative select-none overflow-hidden`}
            >
              {/* A. JFrame Window Title Bar (Height: 30px) */}
              <div className={`flex items-center justify-between px-3 h-[30px] ${activeTheme.titleBarBg} border-b ${activeTheme.borderColor} shrink-0`}>
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
                
                {/* Window Controls */}
                <div className="flex items-center gap-1">
                  <button className="p-0.5 hover:bg-neutral-700/30 rounded text-neutral-400">
                    <Minimize2 className="h-2.5 w-2.5" />
                  </button>
                  <button className="p-0.5 hover:bg-neutral-700/30 rounded text-neutral-400">
                    <Square className="h-2 w-2" />
                  </button>
                  <button 
                    onClick={() => {
                      setAppState("typing");
                      setTerminalText("");
                      setBootLogs([]);
                    }}
                    className="p-0.5 hover:bg-red-500/80 hover:text-white rounded text-neutral-400 transition-colors"
                  >
                    <X className="h-2.5 w-2.5" />
                  </button>
                </div>
              </div>

              {/* B. JMenuBar - Interactive Dropdowns (Height: 24px) */}
              <div ref={menuRef} className={`flex items-center gap-1 px-2 h-[24px] ${activeTheme.menuBarBg} border-b ${activeTheme.borderColor} text-[11px] font-medium font-sans shrink-0 relative z-30`}>
                
                {/* Menu 1: Run */}
                <div className="relative">
                  <span 
                    className="px-2 py-0.5 text-neutral-400 select-none cursor-default font-medium"
                  >
                    Run
                  </span>
                  {activeMenu === "run" && (
                    <div className={`absolute left-0 mt-1 w-32 rounded-lg border ${activeTheme.borderColor} ${activeTheme.dropdownBg} shadow-xl p-1 text-[11px] flex flex-col gap-0.5`}>
                      <button 
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

                {/* Menu 2: Config */}
                <div className="relative">
                  <span 
                    className="px-2 py-0.5 text-neutral-400 select-none cursor-default font-medium"
                  >
                    Config
                  </span>
                </div>

                {/* Menu 3: Controls */}
                <div className="relative">
                  <span 
                    className="px-2 py-0.5 text-neutral-400 select-none cursor-default font-medium"
                  >
                    Controls
                  </span>
                </div>

                {/* Menu 4: Misc */}
                <div className="relative">
                  <button 
                    onClick={() => setActiveMenu(activeMenu === "misc" ? null : "misc")}
                    className={`px-2 py-0.5 rounded hover:bg-neutral-500/20 transition-all ${activeMenu === "misc" ? "bg-neutral-500/20" : ""}`}
                  >
                    Misc
                  </button>
                  {activeMenu === "misc" && (
                    <div className={`absolute left-0 mt-1 w-36 rounded-lg border ${activeTheme.borderColor} ${activeTheme.dropdownBg} shadow-xl p-1 text-[11px] flex flex-col gap-0.5`}>
                      <button 
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
                        onClick={() => {
                          setIsSystemInfoOpen(true);
                          setIsAboutOpen(false);
                          setActiveMenu(null);
                        }}
                        className={`w-full text-left px-2 py-1 rounded ${activeTheme.textColor} ${activeTheme.dropdownHover}`}
                      >
                        System Info
                      </button>
                      <a 
                        href="/docs" 
                        onClick={() => setActiveMenu(null)}
                        className={`w-full text-left px-2 py-1 rounded block ${activeTheme.textColor} ${activeTheme.dropdownHover}`}
                      >
                        Docs Help
                      </a>
                    </div>
                  )}
                </div>
              </div>

              {/* C. JFrame Canvas Content Area (Height: 240px) */}
              <div 
                className="w-[320px] h-[240px] bg-neutral-950 relative overflow-hidden shrink-0 border-b border-neutral-900"
              >
                
                <AnimatePresence mode="wait">
                  
                  {/* Launcher State: Empty state awaiting file load */}
                  {appState === "launcher" && (
                    <motion.div
                      key="launcher"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className={`w-full h-full flex flex-col justify-between font-sans text-xs relative transition-colors duration-300 ${
                        uiTheme === "light" ? "bg-slate-50 text-slate-800" : uiTheme === "dark" ? "bg-[#181818] text-[#fafafa]" : "bg-[#1e1f29] text-[#f8f8f2]"
                      }`}
                    >
                      {/* Top Bar */}
                      <div className={`px-2 py-0.5 border-b flex justify-between items-center text-[9px] font-mono transition-colors duration-300 ${
                        uiTheme === "light" 
                          ? "bg-slate-100 border-slate-250 text-slate-500" 
                          : uiTheme === "dark" 
                            ? "bg-neutral-900 border-neutral-800 text-neutral-400" 
                            : "bg-[#1a1b23] border-[#2a2c3a] text-neutral-400"
                      }`}>
                        <span>Neutron JVM v1.0</span>
                        <span>12:00 PM</span>
                      </div>

                      {/* Centered Message */}
                      <div className="flex flex-col items-center justify-center p-4 text-center gap-1.5 flex-1">
                        <div className={`h-10 w-10 rounded-full flex items-center justify-center border animate-pulse transition-all duration-300 ${
                          uiTheme === "light"
                            ? "bg-slate-200/50 border-slate-300"
                            : uiTheme === "dark"
                              ? "bg-neutral-800/40 border-neutral-700/60"
                              : "bg-[#282a36]/50 border-[#44475a]/60"
                        }`}>
                          <svg className={`h-5 w-5 transition-colors duration-300 ${uiTheme === "light" ? "text-slate-600" : "text-neutral-400"}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                            <rect x="5" y="2" width="14" height="20" rx="3" fill="currentColor" fillOpacity="0.1" />
                            <rect x="7" y="4" width="10" height="8" rx="1" fill="currentColor" fillOpacity="0.15" />
                            <circle cx="10" cy="16" r="1.5" />
                            <circle cx="14.5" cy="15.5" r="1" />
                            <circle cx="15.5" cy="17" r="1" />
                          </svg>
                        </div>
                        <p className={`text-[10px] font-bold transition-colors duration-300 ${
                          uiTheme === "light" ? "text-slate-800" : "text-neutral-200"
                        }`}>No MIDlet Loaded</p>
                        <p className={`text-[8.5px] max-w-[200px] leading-normal transition-colors duration-300 ${
                          uiTheme === "light" ? "text-slate-500" : "text-neutral-400"
                        }`}>
                          Go to <span className={`font-semibold font-mono ${
                            uiTheme === "light" ? "text-cyan-600" : "text-cyan-400"
                          }`}>Run &gt; Run JAR File</span> to load and execute a J2ME midlet archive.
                        </p>
                      </div>

                      {/* Footer */}
                      <div className={`px-2 py-0.5 border-t flex justify-between text-[8px] font-bold font-mono transition-colors duration-300 ${
                        uiTheme === "light"
                          ? "bg-slate-100 border-slate-250 text-slate-500"
                          : uiTheme === "dark"
                            ? "bg-neutral-900 border-neutral-800 text-neutral-400"
                            : "bg-[#1a1b23] border-[#2a2c3a] text-neutral-400"
                      }`}>
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
                      <div className="relative w-[320px] h-[240px] overflow-hidden bg-black flex items-center justify-center">
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
                                imageRendering: (graphicsFilter === "nearest" || graphicsFilter === "scanlines" || graphicsFilter === "lcd") ? "pixelated" : "auto",
                                WebkitTouchCallout: "none",
                                WebkitUserSelect: "none",
                              }}
                            />
                          </motion.div>
                        </AnimatePresence>

                        {/* Scanlines Filter Overlay */}
                        {graphicsFilter === "scanlines" && (
                          <div className="absolute inset-0 pointer-events-none mix-blend-overlay opacity-60 bg-[linear-gradient(to_bottom,rgba(0,0,0,0.35)_50%,transparent_50%)] bg-[length:100%_2px] z-10" />
                        )}

                        {/* LCD Grid Filter Overlay */}
                        {graphicsFilter === "lcd" && (
                          <div className="absolute inset-0 pointer-events-none mix-blend-overlay opacity-40 bg-[linear-gradient(to_right,rgba(0,0,0,0.25)_1px,transparent_1px),linear-gradient(to_bottom,rgba(0,0,0,0.25)_1px,transparent_1px)] bg-[length:3px_3px] z-10" />
                        )}

                        {/* CRT Glass Reflection / Glare */}
                        <div className="absolute inset-0 pointer-events-none bg-gradient-to-tr from-transparent via-white/[0.02] to-white/[0.06] mix-blend-screen z-15" />
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* SwingDialog Dialog Modal 1: About Panel */}
                <AnimatePresence>
                  {isAboutOpen && (
                    <motion.div 
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div className={`w-[260px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2.5 text-xs shadow-2xl`}>
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1.5">
                            <Info className="h-3.5 w-3.5 text-primary" /> About Neutron
                          </span>
                          <button onClick={() => setIsAboutOpen(false)} className="hover:bg-neutral-500/20 rounded p-0.5">
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>
                        <div className="space-y-1 text-[11px] text-neutral-400">
                          <p className={`${activeTheme.textColor} font-bold text-xs`}>Neutron Emulator v1.0.0</p>
                          <p>Lead Vendor: <span className="font-semibold text-neutral-300">nsomatrix</span></p>
                          <p>Lead Developer: <span className="font-semibold text-neutral-300">mackruize</span></p>
                          <p>Platform: Java SE / Swing GUI</p>
                          <p>License: GNU LGPL v2.1</p>
                        </div>
                        <button 
                          onClick={() => setIsAboutOpen(false)}
                          className="mt-1 w-full py-1 rounded bg-primary text-white text-[11px] font-semibold hover:bg-primary-hover active:scale-95 transition-all"
                        >
                          OK
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* SwingDialog Dialog Modal 2: System Info Panel */}
                <AnimatePresence>
                  {isSystemInfoOpen && (
                    <motion.div 
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div className={`w-[270px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2 text-xs shadow-2xl`}>
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1.5">
                            <Cpu className="h-3.5 w-3.5 text-cyan-400" /> System Properties
                          </span>
                          <button onClick={() => setIsSystemInfoOpen(false)} className="hover:bg-neutral-500/20 rounded p-0.5">
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>
                        <div className="max-h-[160px] overflow-y-auto font-mono text-[10px] text-neutral-400 space-y-1.5 pr-1">
                          <div className="flex justify-between border-b border-neutral-800/40 pb-0.5">
                            <span className="text-neutral-500">java.version</span>
                            <span className="text-neutral-200">1.8.0_382</span>
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
                          onClick={() => setIsSystemInfoOpen(false)}
                          className="mt-1.5 w-full py-1 rounded bg-primary text-white text-[11px] font-semibold hover:bg-primary-hover active:scale-95 transition-all"
                        >
                          Close
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* SwingDialog Dialog Modal 3: JFileChooser Dialog */}
                <AnimatePresence>
                  {isFileChooserOpen && (
                    <motion.div 
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-45 p-4"
                    >
                      <div className={`w-[290px] rounded-lg border ${activeTheme.borderColor} ${activeTheme.windowBg} p-3 flex flex-col gap-2 text-xs shadow-2xl`}>
                        <div className="flex items-center justify-between border-b pb-1.5 border-neutral-700/40">
                          <span className="font-bold flex items-center gap-1">
                            📁 JFileChooser
                          </span>
                          <button onClick={() => setIsFileChooserOpen(false)} className="hover:bg-neutral-500/20 rounded p-0.5">
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>

                        {/* File list of J2ME midlet JAR archives */}
                        <div className={`rounded border p-1 max-h-[140px] overflow-y-auto space-y-0.5 transition-colors duration-300 ${
                          uiTheme === "light" 
                            ? "bg-slate-100 border-slate-200" 
                            : "bg-neutral-950/80 border-neutral-800/80"
                        }`}>
                          {[
                            { id: "ninja_offline", filename: "ninja_school_offline.jar", size: "354 KB" },
                            { id: "ninja_world", filename: "ninja_school_world.jar", size: "482 KB" },
                            { id: "nsomatrix", filename: "nsomatrix.jar", size: "298 KB" },
                            { id: "neutron", filename: "neutron.jar", size: "512 KB" }
                          ].map((file) => {
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
                                <span className="flex items-center gap-1">
                                  <span className={uiTheme === "light" ? "text-slate-400" : "text-neutral-500"}>📄</span>
                                  {file.filename}
                                </span>
                                <span className={`text-[9px] font-sans ${uiTheme === "light" ? "text-slate-400" : "text-neutral-500"}`}>{file.size}</span>
                              </div>
                            );
                          })}
                        </div>

                        {/* Dialogue Buttons */}
                        <div className="flex justify-end gap-1.5 mt-1">
                          <button
                            onClick={() => setIsFileChooserOpen(false)}
                            className={`px-3 py-1 rounded border text-[10px] transition-colors ${
                              uiTheme === "light"
                                ? "border-slate-300 text-slate-700 hover:bg-slate-100/80"
                                : "border-neutral-700 text-neutral-350 hover:bg-neutral-800"
                            }`}
                          >
                            Cancel
                          </button>
                          <button
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

              {/* D. SwingStatusBar - Status Bar (Height: 26px) */}
              <div className={`px-3 h-[26px] ${activeTheme.titleBarBg} border-t ${activeTheme.borderColor} text-[9px] flex items-center justify-between text-neutral-400 font-mono shrink-0`}>
                <div className="flex items-center gap-1.5 truncate max-w-[200px]">
                  <span className="flex h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" />
                  <span className="truncate">
                    {appState === "gameplay" 
                      ? `Running: ${gameNames[selectedAppId] || "Ninja School Offline"} (${emulationSpeed.toFixed(1)}x)` 
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
