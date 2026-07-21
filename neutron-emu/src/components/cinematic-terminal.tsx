"use client";

import React, { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image from "next/image";
import logoImg from "@/assets/ntn.png";
import { useTheme } from "next-themes";
import { 
  Terminal as TerminalIcon, RefreshCw, Play, Pause, Circle, 
  Cpu, X, Minimize2, Square, Info
} from "lucide-react";

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
  const [selectedAppId, setSelectedAppId] = useState("gof2");
  const [score, setScore] = useState(0);
  const [shield, setShield] = useState(100);
  
  // Custom interactive settings linked to actual JMenuBar options
  const [graphicsFilter, setGraphicsFilter] = useState<GraphicsFilter>("scanlines");
  const [emulationSpeed, setEmulationSpeed] = useState<number>(1.0);
  const [uiTheme, setUiTheme] = useState<EmulatorTheme>("dracula");
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [activeMenu, setActiveMenu] = useState<string | null>(null);
  
  // Modal dialog states mirroring JVM SwingDialogPanel
  const [isAboutOpen, setIsAboutOpen] = useState(false);
  const [isSystemInfoOpen, setIsSystemInfoOpen] = useState(false);
  const [isFileChooserOpen, setIsFileChooserOpen] = useState(false);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const animationFrameRef = useRef<number | null>(null);
  const lastTimeRef = useRef<number>(0);
  const lastShotRef = useRef<number>(0);
  const logTimerRef = useRef<NodeJS.Timeout | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);
  
  // Game states for our landscape canvas shooter (320x240 grid)
  const playerRef = useRef({
    x: 160,
    y: 200,
    width: 20,
    height: 16,
    speed: 160, // pixels per second
    targetX: 160,
  });

  const lasersRef = useRef<{ x: number; y: number; speed: number }[]>([]);
  const enemiesRef = useRef<{ x: number; y: number; width: number; height: number; speed: number; type: "enemy" | "asteroid" }[]>([]);
  const particlesRef = useRef<{ x: number; y: number; vx: number; vy: number; color: string; life: number; maxLife: number; size: number }[]>([]);
  const starsRef = useRef<{ x: number; y: number; speed: number; size: number }[]>([]);

  // J2ME Game Apps List
  const apps: GameApp[] = [
    {
      id: "gof2",
      name: "Galaxy on Fire 2",
      developer: "Fishlabs",
      iconBg: "from-blue-900 to-indigo-950",
      iconSvg: (
        <svg className="w-10 h-10 text-cyan-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polygon points="12,2 22,22 12,17 2,22" fill="currentColor" fillOpacity="0.2" />
          <circle cx="12" cy="12" r="3" className="animate-pulse" />
        </svg>
      ),
    },
    {
      id: "bounce",
      name: "Bounce Tales",
      developer: "Rovio Mobile",
      iconBg: "from-orange-600 to-red-950",
      iconSvg: (
        <svg className="w-10 h-10 text-orange-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="8" fill="currentColor" fillOpacity="0.3" />
          <path d="M12,4 C14,8 14,16 12,20" />
        </svg>
      ),
    },
    {
      id: "snake",
      name: "Snake II",
      developer: "Nokia",
      iconBg: "from-emerald-900 to-emerald-950",
      iconSvg: (
        <svg className="w-10 h-10 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M4,4 H20 V8 H8 V12 H20 V20 H4" strokeLinecap="round" strokeLinejoin="round" fill="none" />
          <rect x="2" y="2" width="4" height="4" fill="currentColor" />
        </svg>
      ),
    },
    {
      id: "diamond",
      name: "Diamond Rush",
      developer: "Gameloft",
      iconBg: "from-cyan-900 to-teal-950",
      iconSvg: (
        <svg className="w-10 h-10 text-cyan-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polygon points="12,2 22,9 12,22 2,9" fill="currentColor" fillOpacity="0.2" />
        </svg>
      ),
    },
    {
      id: "asphalt",
      name: "Asphalt 3: 3D",
      developer: "Gameloft",
      iconBg: "from-rose-900 to-rose-950",
      iconSvg: (
        <svg className="w-10 h-10 text-rose-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M4,18 L7,10 L17,10 L20,18" />
          <circle cx="8" cy="15" r="2.5" />
          <circle cx="16" cy="15" r="2.5" />
        </svg>
      ),
    },
    {
      id: "doom",
      name: "Doom RPG",
      developer: "id Software",
      iconBg: "from-red-950 to-neutral-950",
      iconSvg: (
        <svg className="w-10 h-10 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M12,4 L19,10 L19,20 L12,17 L5,20 L5,10 Z" fill="currentColor" fillOpacity="0.2" />
          <line x1="9" y1="10" x2="15" y2="10" />
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

    setSelectedAppId("gof2");

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

  // Initialize Canvas Particles & Stars for landscape grid (320x240)
  useEffect(() => {
    if (appState !== "gameplay") return;

    const stars = [];
    for (let i = 0; i < 40; i++) {
      stars.push({
        x: Math.random() * 320,
        y: Math.random() * 240,
        speed: Math.random() * 80 + 20, 
        size: Math.random() * 1.5 + 0.5,
      });
    }
    starsRef.current = stars;
    lasersRef.current = [];
    enemiesRef.current = [];
    particlesRef.current = [];
    setScore(0);
    setShield(100);
    playerRef.current.x = 160;
    playerRef.current.y = 200;
    playerRef.current.targetX = 160;
  }, [appState]);

  // Game loop & canvas rendering (320x240 landscape grid)
  useEffect(() => {
    if (appState !== "gameplay") return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let isDestroyed = false;

    // Main update & render loop
    const updateAndRender = (timestamp: number) => {
      if (isDestroyed) return;
      if (lastTimeRef.current === 0) lastTimeRef.current = timestamp;
      const dt = (timestamp - lastTimeRef.current) / 1000; // seconds
      lastTimeRef.current = timestamp;

      // Apply emulation speed multiplier
      const clampedDt = Math.min(dt, 0.1) * emulationSpeed;

      // --- PHYSICS & LOGIC UPDATE ---
      
      // 1. Update stars (background parallax)
      starsRef.current.forEach((star) => {
        star.y += star.speed * clampedDt;
        if (star.y > 240) {
          star.y = 0;
          star.x = Math.random() * 320;
        }
      });

      // 2. Control player (Autopilot AI controls player)
      const player = playerRef.current;
      let targetX = 160;
      let closestObj = null;
      let minDistY = 9999;

      enemiesRef.current.forEach((obj) => {
        const distY = player.y - obj.y;
        if (distY > 0 && distY < minDistY) {
          minDistY = distY;
          closestObj = obj;
        }
      });

      if (closestObj) {
        targetX = (closestObj as any).x;
      }

      const diffX = targetX - player.x;
      const moveStep = player.speed * clampedDt;

      if (Math.abs(diffX) > 4) {
        if (diffX < 0) {
          player.x = Math.max(player.width / 2, player.x - moveStep);
        } else {
          player.x = Math.min(320 - player.width / 2, player.x + moveStep);
        }
      }

      // Auto Fire
      const now = Date.now();
      if (now - lastShotRef.current > 350) {
        lasersRef.current.push({ x: player.x, y: player.y - 10, speed: 250 });
        lastShotRef.current = now;
        createExplosion(player.x, player.y - 12, "#22d3ee", 2, 0.5);
      }

      // 3. Update lasers
      lasersRef.current.forEach((laser, idx) => {
        laser.y -= laser.speed * clampedDt;
      });
      lasersRef.current = lasersRef.current.filter((l) => l.y > -10);

      // Spawn Enemies/Asteroids
      if (enemiesRef.current.length < 4 && Math.random() < 0.03) {
        const type = Math.random() > 0.4 ? "enemy" : "asteroid";
        enemiesRef.current.push({
          x: Math.random() * 280 + 20,
          y: -20,
          width: type === "enemy" ? 18 : 22,
          height: type === "enemy" ? 18 : 22,
          speed: Math.random() * 40 + 55,
          type,
        });
      }

      // 4. Update Enemies
      enemiesRef.current.forEach((enemy) => {
        enemy.y += enemy.speed * clampedDt;
        if (enemy.type === "enemy") {
          enemy.x += Math.sin(timestamp / 300 + enemy.y) * 20 * clampedDt;
          enemy.x = Math.max(15, Math.min(305, enemy.x));
        }
      });
      enemiesRef.current = enemiesRef.current.filter((e) => e.y < 260);

      // 5. Update particles
      particlesRef.current.forEach((p) => {
        p.x += p.vx * clampedDt;
        p.y += p.vy * clampedDt;
        p.life -= clampedDt;
      });
      particlesRef.current = particlesRef.current.filter((p) => p.life > 0);

      // --- COLLISIONS ---
      lasersRef.current.forEach((laser, lIdx) => {
        enemiesRef.current.forEach((enemy, eIdx) => {
          const dx = laser.x - enemy.x;
          const dy = laser.y - enemy.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const collisionDist = (enemy.width + 4) / 2;

          if (dist < collisionDist) {
            lasersRef.current.splice(lIdx, 1);
            enemiesRef.current.splice(eIdx, 1);
            setScore((prev) => prev + (enemy.type === "enemy" ? 100 : 50));
            const color = enemy.type === "enemy" ? "#ef4444" : "#a1a1aa";
            createExplosion(enemy.x, enemy.y, color, 12, 1.2);
          }
        });
      });

      enemiesRef.current.forEach((enemy, eIdx) => {
        const dx = player.x - enemy.x;
        const dy = player.y - enemy.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        const collisionDist = (player.width + enemy.width) / 2 - 2;

        if (dist < collisionDist) {
          enemiesRef.current.splice(eIdx, 1);
          setShield((prev) => {
            const next = Math.max(0, prev - (enemy.type === "enemy" ? 25 : 15));
            if (next === 0) {
              setTimeout(() => setShield(100), 1500);
            }
            return next;
          });
          createExplosion(enemy.x, enemy.y, "#f59e0b", 15, 1.5);
          createExplosion(player.x, player.y, "#ef4444", 8, 1.0);
        }
      });

      function createExplosion(x: number, y: number, color: string, count: number, speedMultiplier: number) {
        for (let i = 0; i < count; i++) {
          const angle = Math.random() * Math.PI * 2;
          const speed = (Math.random() * 80 + 30) * speedMultiplier;
          particlesRef.current.push({
            x,
            y,
            vx: Math.cos(angle) * speed,
            vy: Math.sin(angle) * speed,
            color,
            life: Math.random() * 0.4 + 0.2,
            maxLife: 0.6,
            size: Math.random() * 2.5 + 1,
          });
        }
      }

      // --- RENDERING ---
      ctx.fillStyle = "#0c0a09"; 
      ctx.fillRect(0, 0, 320, 240);

      // Render stars
      starsRef.current.forEach((star) => {
        ctx.fillStyle = `rgba(255, 255, 255, ${star.speed / 100})`;
        ctx.fillRect(star.x, star.y, star.size, star.size);
      });

      // Render enemies
      enemiesRef.current.forEach((enemy) => {
        ctx.save();
        ctx.translate(enemy.x, enemy.y);

        if (enemy.type === "enemy") {
          ctx.shadowBlur = graphicsFilter === "nearest" ? 0 : 8;
          ctx.shadowColor = "#f43f5e";
          ctx.fillStyle = "#ef4444";
          ctx.beginPath();
          ctx.moveTo(0, 10);      
          ctx.lineTo(-8, -4);     
          ctx.lineTo(-4, -8);     
          ctx.lineTo(4, -8);      
          ctx.lineTo(8, -4);      
          ctx.closePath();
          ctx.fill();

          ctx.fillStyle = "#fb7185";
          ctx.fillRect(-2, -10, 4, 2);
        } else {
          ctx.fillStyle = "#78716c";
          ctx.strokeStyle = "#44403c";
          ctx.lineWidth = 1.5;
          ctx.beginPath();
          ctx.moveTo(-10, -5);
          ctx.lineTo(-5, -10);
          ctx.lineTo(5, -10);
          ctx.lineTo(10, -4);
          ctx.lineTo(8, 6);
          ctx.lineTo(0, 10);
          ctx.lineTo(-8, 6);
          ctx.closePath();
          ctx.fill();
          ctx.stroke();
        }
        ctx.restore();
      });

      // Render lasers
      lasersRef.current.forEach((laser) => {
        ctx.save();
        ctx.shadowBlur = graphicsFilter === "nearest" ? 0 : 6;
        ctx.shadowColor = "#06b6d4";
        ctx.fillStyle = "#22d3ee";
        ctx.fillRect(laser.x - 1, laser.y - 6, 2, 8);
        ctx.restore();
      });

      // Render particles
      particlesRef.current.forEach((p) => {
        ctx.fillStyle = p.color;
        ctx.globalAlpha = p.life / p.maxLife;
        ctx.fillRect(p.x, p.y, p.size, p.size);
      });
      ctx.globalAlpha = 1.0;

      // Render Player Ship
      if (shield > 0) {
        ctx.save();
        ctx.translate(player.x, player.y);

        const flameHeight = Math.random() * 8 + 4;
        ctx.fillStyle = "#f97316";
        ctx.beginPath();
        ctx.moveTo(-4, 8);
        ctx.lineTo(0, 8 + flameHeight);
        ctx.lineTo(4, 8);
        ctx.closePath();
        ctx.fill();

        ctx.fillStyle = "#facc15";
        ctx.fillRect(-2, 8, 4, 4);

        ctx.shadowBlur = graphicsFilter === "nearest" ? 0 : 10;
        ctx.shadowColor = "#3b82f6";
        ctx.fillStyle = "#3b82f6";
        ctx.beginPath();
        ctx.moveTo(0, -10);     
        ctx.lineTo(-10, 8);     
        ctx.lineTo(-4, 4);      
        ctx.lineTo(4, 4);       
        ctx.lineTo(10, 8);      
        ctx.closePath();
        ctx.fill();

        ctx.fillStyle = "#67e8f9";
        ctx.beginPath();
        ctx.moveTo(0, -6);
        ctx.lineTo(-3, 0);
        ctx.lineTo(3, 0);
        ctx.closePath();
        ctx.fill();

        ctx.restore();
      }

      // Draw Scanlines overlay
      if (graphicsFilter === "scanlines") {
        ctx.fillStyle = "rgba(0, 0, 0, 0.15)";
        for (let y = 0; y < 240; y += 2) {
          ctx.fillRect(0, y, 320, 0.8);
        }
      }

      // Draw LCD grid overlay
      if (graphicsFilter === "lcd") {
        ctx.fillStyle = "rgba(0, 0, 0, 0.08)";
        for (let x = 0; x < 320; x += 3) {
          ctx.fillRect(x, 0, 0.8, 240);
        }
        for (let y = 0; y < 240; y += 3) {
          ctx.fillRect(0, y, 320, 0.8);
        }
      }

      // Display Stats HUD (scaled for 320px width)
      ctx.fillStyle = "rgba(255,255,255,0.7)";
      ctx.font = "8px monospace";
      ctx.fillText(`SCORE: ${score}`, 8, 14);
      
      ctx.fillText("GPRS", 245, 14);
      ctx.fillRect(280, 7, 2, 7);
      ctx.fillRect(284, 9, 2, 5);
      ctx.fillRect(288, 11, 2, 3);
      ctx.strokeRect(295, 7, 12, 6);
      ctx.fillRect(297, 9, 8, 2);

      ctx.fillStyle = "#444";
      ctx.fillRect(8, 20, 60, 4);
      ctx.fillStyle = shield > 30 ? "#10b981" : "#ef4444";
      ctx.fillRect(8, 20, (shield / 100) * 60, 4);

      animationFrameRef.current = requestAnimationFrame(updateAndRender);
    };

    animationFrameRef.current = requestAnimationFrame(updateAndRender);

    return () => {
      isDestroyed = true;
      if (animationFrameRef.current) cancelAnimationFrame(animationFrameRef.current);
    };
  }, [appState, graphicsFilter, emulationSpeed]);



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
                      <div className="relative w-[320px] h-[240px] overflow-hidden">
                        <canvas
                          ref={canvasRef}
                          width={320}
                          height={240}
                          className={`w-full h-full block bg-black transition-all ${
                            graphicsFilter === "bilinear" ? "image-render-auto" : "image-render-pixelated"
                          }`}
                          style={{ imageRendering: graphicsFilter === "nearest" ? "pixelated" : "auto" }}
                        />

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
                            { id: "gof2", filename: "galaxy_on_fire_2.jar", size: "942 KB" },
                            { id: "bounce", filename: "bounce_tales.jar", size: "384 KB" },
                            { id: "snake", filename: "snake_2.jar", size: "128 KB" },
                            { id: "diamond", filename: "diamond_rush.jar", size: "512 KB" }
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
                      ? `Running: Galaxy on Fire 2 (${emulationSpeed.toFixed(1)}x)` 
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
