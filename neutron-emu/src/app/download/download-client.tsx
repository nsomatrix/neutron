"use client";

import { useState } from "react";
import Image from "next/image";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  Download,
  Monitor,
  Apple,
  Terminal,
  Copy,
  Check,
  ShieldCheck,
  HelpCircle,
  ExternalLink,
  Cpu,
  ChevronDown,
  Info,
  ArrowRight,
  BookOpen,
  Cloud,
  Server,
  Loader2
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import logoImg from "@/assets/ntn.png";

const EXPRESS_JAR_SHA256 = "e6be9934225f9753f05029cf546b16c0853e98a282cb06cbe523c399a13bd513";
const EXPRESS_DOWNLOAD_URL = "https://github.com/nsomatrix/neutron/releases/download/1.0.0/neutron.jar";

const CORE_JAR_SHA256 = "c9693b17ec79e54dc925340ef6f8aed4423f4855dafa3e77fc9c9bef7a60a17b";
const CORE_DOWNLOAD_URL = "https://github.com/nsomatrix/neutron/releases/download/1.0.0/neutron-core.jar";

interface DownloadButtonProps {
  href: string;
  downloadName: string;
  className?: string;
}

function InteractiveDownloadButton({ href, downloadName, className = "" }: DownloadButtonProps) {
  const [status, setStatus] = useState<"idle" | "downloading" | "completed">("idle");

  const handleClick = () => {
    if (status !== "idle") return;

    setStatus("downloading");

    setTimeout(() => {
      setStatus("completed");
    }, 1200);

    setTimeout(() => {
      setStatus("idle");
    }, 3500);
  };

  return (
    <motion.div
      whileHover={{ scale: status === "idle" ? 1.02 : 1 }}
      whileTap={{ scale: status === "idle" ? 0.98 : 1 }}
      className="w-full sm:w-auto"
    >
      <Button
        asChild
        size="lg"
        className={`relative overflow-hidden w-full sm:w-64 h-14 text-base font-semibold shadow-lg transition-all duration-300 ${className}`}
      >
        <a href={href} download={downloadName} onClick={handleClick}>
          <AnimatePresence mode="wait">
            {status === "idle" && (
              <motion.span
                key="idle"
                initial={{ opacity: 0, y: 6 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -6 }}
                transition={{ duration: 0.15 }}
                className="flex items-center justify-center gap-2"
              >
                <Download className="h-5 w-5" />
                <span>Download</span>
              </motion.span>
            )}

            {status === "downloading" && (
              <motion.span
                key="downloading"
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8 }}
                transition={{ duration: 0.15 }}
                className="flex items-center justify-center"
              >
                <Loader2 className="h-6 w-6 animate-spin" />
              </motion.span>
            )}

            {status === "completed" && (
              <motion.span
                key="completed"
                initial={{ opacity: 0, scale: 0.6 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, y: 6 }}
                transition={{ type: "spring", stiffness: 450, damping: 25 }}
                className="flex items-center justify-center"
              >
                <motion.div
                  initial={{ scale: 0, rotate: -45 }}
                  animate={{ scale: 1, rotate: 0 }}
                  transition={{ type: "spring", stiffness: 500, damping: 20 }}
                >
                  <Check className="h-6 w-6 stroke-[2.5]" />
                </motion.div>
              </motion.span>
            )}
          </AnimatePresence>
        </a>
      </Button>
    </motion.div>
  );
}

const platforms = [
  {
    id: "windows",
    name: "Windows",
    icon: Monitor,
    commands: [
      {
        label: "Launch GUI directly",
        code: "java -jar neutron.jar"
      }
    ],
    instructions: [
      "Ensure you have Java 8 or later installed on your system.",
      "Double-click neutron.jar to start the graphical emulator immediately.",
      "Alternatively, open Command Prompt or PowerShell, navigate to the folder, and run the command below to launch it via terminal."
    ]
  },
  {
    id: "macos",
    name: "macOS",
    icon: Apple,
    commands: [
      {
        label: "Launch emulator",
        code: "java -jar neutron.jar"
      },
      {
        label: "Run a MIDlet from terminal",
        code: "java -jar neutron.jar path/to/game.jar"
      }
    ],
    instructions: [
      "Ensure Java 8 or later is installed (Temurin OpenJDK is recommended).",
      "Open Terminal, navigate to the download folder, and launch using the command below.",
      "If macOS Gatekeeper blocks execution, right-click neutron.jar, select Open, and approve the security prompt, or go to System Settings > Privacy & Security."
    ]
  },
  {
    id: "linux",
    name: "Linux",
    icon: Terminal,
    commands: [
      {
        label: "Make executable (optional)",
        code: "chmod +x neutron.jar"
      },
      {
        label: "Launch via terminal",
        code: "java -jar neutron.jar"
      }
    ],
    instructions: [
      "Ensure your package manager has default-jre or openjdk-8-jre installed.",
      "Open Terminal and run the chmod command to ensure the file is executable.",
      "Run the jar file directly using the command-line to start the emulator."
    ]
  }
];

const faqs = [
  {
    question: "Which version of Java do I need to run Neutron?",
    answer: "Neutron requires Java Runtime Environment (JRE) or Java Development Kit (JDK) 8 or higher. We highly recommend using a modern LTS release, such as Eclipse Temurin (OpenJDK) 17 or 21, for optimal performance and security."
  },
  {
    question: "How do I download and install Java?",
    answer: "You can download pre-built, free OpenJDK binaries from Adoptium (Eclipse Temurin) at adoptium.net. Choose the installer for your operating system, run it, and make sure to select the option to 'Add to PATH' during the installation process."
  },
  {
    question: "Double-clicking the JAR file doesn't open the app. What should I do?",
    answer: "On Windows, this is often caused by incorrect file associations (e.g., zip extractors taking over .jar files). You can run a tool like 'Jarfix' to repair the file association, or launch it directly from the command line: open your terminal/command prompt and run 'java -jar neutron.jar'."
  },
  {
    question: "Are MIDlet sound and internet connectivity supported?",
    answer: "Yes! Neutron fully implements MIDP 2.0 audio APIs and handles sound playbacks. It also features internet connection bridging, allowing emulated J2ME apps to access the network directly, with support for customizable SOCKS5 proxies."
  },
  {
    question: "Where are emulated files and configs saved?",
    answer: "Neutron stores its system files, emulated filesystems (JSR-75), and preferences in your user home directory under a '.neutron' folder. This ensures configuration and save data persist across runs and version updates."
  }
];

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1
    }
  }
} as const;

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.5, ease: "easeOut" }
  }
} as const;


export default function DownloadPageClient() {
  const [activeTab, setActiveTab] = useState("windows");
  const [copiedText, setCopiedText] = useState<string | null>(null);
  const [openFaq, setOpenFaq] = useState<number | null>(null);

  const fallbackCopy = (text: string, id: string) => {
    const textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position = "fixed";
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.opacity = "0";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
      document.execCommand("copy");
      setCopiedText(id);
      setTimeout(() => setCopiedText(null), 2000);
    } catch (err) {
      console.error("Fallback copy failed", err);
    }
    document.body.removeChild(textArea);
  };

  const handleCopy = (text: string, id: string) => {
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text)
        .then(() => {
          setCopiedText(id);
          setTimeout(() => setCopiedText(null), 2000);
        })
        .catch(() => {
          fallbackCopy(text, id);
        });
    } else {
      fallbackCopy(text, id);
    }
  };

  const currentPlatform = platforms.find((p) => p.id === activeTab) || platforms[0];

  return (
    <div className="relative mx-auto max-w-5xl px-4 py-16 sm:px-6 lg:px-8">
      {/* Decorative background glow */}
      <div className="absolute inset-0 -z-10 overflow-hidden">
        <div className="absolute left-1/2 top-0 -translate-x-1/2 h-[450px] w-[600px] rounded-full bg-gradient-to-br from-blue-accent/10 via-purple-accent/5 to-transparent blur-3xl opacity-70" />
      </div>

      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="space-y-16"
      >
        {/* Hero Section */}
        <motion.div variants={itemVariants} className="text-center space-y-6">
          <div className="flex justify-center">
            <div className="relative group">
              {/* Logo Glow */}
              <div className="absolute -inset-1.5 rounded-2xl bg-gradient-to-r from-blue-accent via-cyan-accent to-purple-accent opacity-75 blur-xl group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-pulse" />
              {/* Logo Container */}
              <div className="relative h-20 w-20 overflow-hidden rounded-2xl border border-border bg-card p-3 shadow-2xl">
                <Image
                  src={logoImg}
                  alt="Neutron Logo"
                  width={80}
                  height={80}
                  className="h-full w-full object-contain"
                  priority
                />
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <h1 className="text-4xl font-extrabold tracking-tight sm:text-5xl bg-gradient-to-r from-foreground via-foreground to-muted-foreground bg-clip-text text-transparent">
              Download Neutron
            </h1>
            <p className="mx-auto max-w-xl text-base sm:text-lg text-muted-foreground leading-relaxed">
              Fast, isolated, and lightweight Java ME emulation. Experience your favorite classic J2ME applications and games on modern operating systems.
            </p>
          </div>

          <div className="flex justify-center gap-2">
            <Badge variant="outline" className="border-emerald-500/30 bg-emerald-500/5 text-emerald-400 px-3 py-1 text-xs font-semibold rounded-full flex items-center gap-1.5">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 animate-pulse" />
              v1.0.0 Stable
            </Badge>
            <Badge variant="outline" className="border-primary/20 bg-primary/5 text-primary px-3 py-1 text-xs font-semibold rounded-full">
              Universal Cross-Platform
            </Badge>
          </div>
        </motion.div>

        {/* Express Edition Download Card */}
        <motion.div variants={itemVariants}>
          <div className="relative rounded-2xl border border-border bg-card/65 backdrop-blur-md overflow-hidden shadow-2xl">
            {/* Header background accents */}
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-primary/30 to-transparent" />
            <div className="absolute top-0 right-0 h-40 w-40 bg-purple-accent/5 rounded-full blur-3xl" />
            <div className="absolute bottom-0 left-0 h-40 w-40 bg-blue-accent/5 rounded-full blur-3xl" />

            <div className="p-8 sm:p-10 flex flex-col lg:flex-row gap-10 items-center justify-between">
              <div className="space-y-6 text-center lg:text-left max-w-xl">
                <div className="space-y-2">
                  <span className="text-xs font-bold tracking-widest text-primary uppercase">Standard Release File</span>
                  <h2 className="text-2xl sm:text-3xl font-bold tracking-tight">Express Edition JAR</h2>
                  <p className="text-sm sm:text-base text-muted-foreground leading-relaxed">
                    This standalone application runs seamlessly across Windows, macOS, and Linux. Built completely with Java, it includes the core graphical interface, configurations, and emulator controls.
                  </p>
                </div>

                <div className="grid grid-cols-3 gap-4 border-y border-border py-4 text-center lg:text-left">
                  <div>
                    <div className="text-xs text-muted-foreground">File Size</div>
                    <div className="text-sm font-semibold mt-1">1.6 MB</div>
                  </div>
                  <div>
                    <div className="text-xs text-muted-foreground">Requirement</div>
                    <div className="text-sm font-semibold mt-1">Java 8+</div>
                  </div>
                  <div>
                    <div className="text-xs text-muted-foreground">Format</div>
                    <div className="text-sm font-semibold mt-1">.jar Executable</div>
                  </div>
                </div>

                <div className="flex flex-wrap justify-center lg:justify-start items-center gap-3">
                  <span className="text-xs text-muted-foreground font-medium">Compatible with:</span>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Monitor className="h-3 w-3" /> Windows
                  </Badge>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Apple className="h-3 w-3" /> macOS
                  </Badge>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Terminal className="h-3 w-3" /> Linux
                  </Badge>
                </div>
              </div>

              <div className="flex flex-col items-center justify-center gap-4 w-full lg:w-auto shrink-0">
                <InteractiveDownloadButton
                  href={EXPRESS_DOWNLOAD_URL}
                  downloadName="neutron.jar"
                  className="shadow-primary/20 bg-primary hover:bg-primary/95 text-primary-foreground"
                />

                <div className="flex flex-col sm:flex-row gap-2 w-full justify-center">
                  <Button asChild variant="ghost" size="sm" className="text-muted-foreground hover:text-foreground">
                    <Link href="/docs/getting-started/quick-start" className="flex items-center gap-1 text-xs">
                      <BookOpen className="h-3.5 w-3.5" />
                      Quick Start Guide
                    </Link>
                  </Button>
                  <Button asChild variant="ghost" size="sm" className="text-muted-foreground hover:text-foreground">
                    <Link href="/changelog" className="flex items-center gap-1 text-xs">
                      Changelog
                      <ArrowRight className="h-3.5 w-3.5" />
                    </Link>
                  </Button>
                </div>
              </div>
            </div>

            {/* Checksum details */}
            <div className="border-t border-border bg-muted/20 px-8 py-4 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs">
              <div className="flex items-center gap-2 text-muted-foreground">
                <ShieldCheck className="h-4 w-4 text-emerald-400 shrink-0" />
                <span className="font-medium text-foreground">SHA-256 Checksum:</span>
                <code className="bg-muted px-1.5 py-0.5 rounded text-[10px] sm:text-xs font-mono break-all max-w-[250px] sm:max-w-none">
                  {EXPRESS_JAR_SHA256}
                </code>
              </div>
              <Button
                variant="outline"
                size="sm"
                className="h-8 text-[11px] gap-1.5 px-3 hover:bg-card w-full sm:w-auto text-muted-foreground hover:text-foreground shrink-0"
                onClick={() => handleCopy(EXPRESS_JAR_SHA256, "express-checksum")}
              >
                {copiedText === "express-checksum" ? (
                  <>
                    <Check className="h-3 w-3 text-emerald-400 animate-in fade-in zoom-in duration-200" />
                    Copied!
                  </>
                ) : (
                  <>
                    <Copy className="h-3 w-3" />
                    Copy SHA-256
                  </>
                )}
              </Button>
            </div>
          </div>
        </motion.div>

        {/* Cloud Edition Download Card */}
        <motion.div variants={itemVariants}>
          <div className="relative rounded-2xl border border-border bg-card/65 backdrop-blur-md overflow-hidden shadow-2xl">
            {/* Header background accents */}
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-cyan-accent/30 to-transparent" />
            <div className="absolute top-0 right-0 h-40 w-40 bg-blue-accent/5 rounded-full blur-3xl" />
            <div className="absolute bottom-0 left-0 h-40 w-40 bg-purple-accent/5 rounded-full blur-3xl" />

            <div className="p-8 sm:p-10 flex flex-col lg:flex-row gap-10 items-center justify-between">
              <div className="space-y-6 text-center lg:text-left max-w-xl">
                <div className="space-y-2">
                  <span className="text-xs font-bold tracking-widest text-cyan-accent uppercase">Core Release File</span>
                  <h2 className="text-2xl sm:text-3xl font-bold tracking-tight">Cloud Edition JAR</h2>
                  <p className="text-sm sm:text-base text-muted-foreground leading-relaxed">
                    Specifically designed for cloud servers and VPS environments. Built completely with Java, it includes the core headless emulation engine, server configurations, and remote controls.
                  </p>
                </div>

                <div className="grid grid-cols-3 gap-4 border-y border-border py-4 text-center lg:text-left">
                  <div>
                    <div className="text-xs text-muted-foreground">File Size</div>
                    <div className="text-sm font-semibold mt-1">486 KB</div>
                  </div>
                  <div>
                    <div className="text-xs text-muted-foreground">Requirement</div>
                    <div className="text-sm font-semibold mt-1">Java 8+</div>
                  </div>
                  <div>
                    <div className="text-xs text-muted-foreground">Format</div>
                    <div className="text-sm font-semibold mt-1">.jar Executable</div>
                  </div>
                </div>

                <div className="flex flex-wrap justify-center lg:justify-start items-center gap-3">
                  <span className="text-xs text-muted-foreground font-medium">Optimized for:</span>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Cloud className="h-3 w-3" /> Cloud Servers
                  </Badge>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Server className="h-3 w-3" /> VPS Instances
                  </Badge>
                  <Badge variant="secondary" className="text-xs bg-muted/60 text-foreground flex items-center gap-1">
                    <Terminal className="h-3 w-3" /> Headless Linux
                  </Badge>
                </div>
              </div>

              <div className="flex flex-col items-center justify-center gap-4 w-full lg:w-auto shrink-0">
                <InteractiveDownloadButton
                  href={CORE_DOWNLOAD_URL}
                  downloadName="neutron-core.jar"
                  className="border border-cyan-accent/50 bg-cyan-accent/5 hover:bg-cyan-accent/15 text-cyan-accent hover:border-cyan-accent hover:shadow-lg hover:shadow-cyan-accent/20 transition-all duration-300"
                />

                <div className="flex flex-col sm:flex-row gap-2 w-full justify-center">
                  <Button asChild variant="ghost" size="sm" className="text-muted-foreground hover:text-foreground">
                    <Link href="/docs/getting-started/quick-start" className="flex items-center gap-1 text-xs">
                      <BookOpen className="h-3.5 w-3.5" />
                      Quick Start Guide
                    </Link>
                  </Button>
                  <Button asChild variant="ghost" size="sm" className="text-muted-foreground hover:text-foreground">
                    <Link href="/docs/advanced/cli" className="flex items-center gap-1 text-xs">
                      CLI Docs
                      <ArrowRight className="h-3.5 w-3.5" />
                    </Link>
                  </Button>
                </div>
              </div>
            </div>

            {/* Checksum details */}
            <div className="border-t border-border bg-muted/20 px-8 py-4 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs">
              <div className="flex items-center gap-2 text-muted-foreground">
                <ShieldCheck className="h-4 w-4 text-emerald-400 shrink-0" />
                <span className="font-medium text-foreground">SHA-256 Checksum:</span>
                <code className="bg-muted px-1.5 py-0.5 rounded text-[10px] sm:text-xs font-mono break-all max-w-[250px] sm:max-w-none">
                  {CORE_JAR_SHA256}
                </code>
              </div>
              <Button
                variant="outline"
                size="sm"
                className="h-8 text-[11px] gap-1.5 px-3 hover:bg-card w-full sm:w-auto text-muted-foreground hover:text-foreground shrink-0"
                onClick={() => handleCopy(CORE_JAR_SHA256, "core-checksum")}
              >
                {copiedText === "core-checksum" ? (
                  <>
                    <Check className="h-3 w-3 text-emerald-400 animate-in fade-in zoom-in duration-200" />
                    Copied!
                  </>
                ) : (
                  <>
                    <Copy className="h-3 w-3" />
                    Copy SHA-256
                  </>
                )}
              </Button>
            </div>
          </div>
        </motion.div>

        {/* Operating System Setup Instructions */}
        <motion.div variants={itemVariants} className="space-y-6">
          <div className="text-center sm:text-left space-y-2">
            <h2 className="text-2xl font-bold tracking-tight">How to run the JAR file</h2>
            <p className="text-muted-foreground text-sm">
              Different operating systems require slightly different steps to launch the emulator.
            </p>
          </div>

          <Card className="p-0 border border-border overflow-hidden bg-card/40">
            {/* Sliding tab selector */}
            <div className="flex border-b border-border bg-muted/30 p-1">
              {platforms.map((plat) => {
                const Icon = plat.icon;
                const isActive = activeTab === plat.id;
                return (
                  <button
                    key={plat.id}
                    onClick={() => setActiveTab(plat.id)}
                    className={`relative flex-1 flex items-center justify-center gap-2 py-3 text-sm font-medium transition-all duration-300 rounded-lg outline-none ${
                      isActive
                        ? "text-foreground shadow-sm bg-card border border-border/80"
                        : "text-muted-foreground hover:text-foreground hover:bg-muted/10"
                    }`}
                  >
                    <Icon className={`h-4 w-4 ${isActive ? "text-primary" : ""}`} />
                    <span>{plat.name}</span>
                    {isActive && (
                      <motion.div
                        layoutId="activeTabGlow"
                        className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-8 h-[2px] bg-primary rounded-full"
                        transition={{ type: "spring", stiffness: 380, damping: 30 }}
                      />
                    )}
                  </button>
                );
              })}
            </div>

            {/* Tab content */}
            <div className="p-6 sm:p-8 space-y-6">
              {/* Instructions list */}
              <div className="space-y-3">
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">Setup Steps</h3>
                <ol className="list-decimal list-inside space-y-2 text-sm text-foreground/90">
                  {currentPlatform.instructions.map((step, idx) => (
                    <li key={idx} className="leading-relaxed pl-1">
                      <span className="text-muted-foreground ml-1">{step}</span>
                    </li>
                  ))}
                </ol>
              </div>

              {/* Commands */}
              <div className="space-y-4 pt-4 border-t border-border">
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                  <Terminal className="h-4 w-4 text-primary" />
                  Terminal / Command Prompt Snippets
                </h3>

                <div className="grid gap-4 md:grid-cols-2">
                  {currentPlatform.commands.map((cmd, idx) => {
                    const cmdId = `${currentPlatform.id}-cmd-${idx}`;
                    return (
                      <div key={idx} className="space-y-2">
                        <span className="text-xs font-medium text-muted-foreground">{cmd.label}</span>
                        <div className="relative group rounded-lg border border-border bg-black/40 p-3 font-mono text-xs flex items-center justify-between gap-4">
                          <code className="text-emerald-400 break-all select-all pr-8">
                            {cmd.code}
                          </code>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="absolute right-2 top-2 h-7 w-7 text-muted-foreground hover:text-foreground hover:bg-muted/40 transition-colors opacity-80 group-hover:opacity-100"
                            onClick={() => handleCopy(cmd.code, cmdId)}
                          >
                            {copiedText === cmdId ? (
                              <Check className="h-3.5 w-3.5 text-emerald-400 animate-in fade-in zoom-in duration-200" />
                            ) : (
                              <Copy className="h-3.5 w-3.5" />
                            )}
                          </Button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          </Card>
        </motion.div>

        {/* Requirements and Downloads Grid */}
        <motion.div variants={itemVariants} className="grid md:grid-cols-2 gap-8">
          {/* System Requirements */}
          <Card className="flex flex-col border border-border bg-card/30 p-6 sm:p-8 space-y-6">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-primary/10">
                <Cpu className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-xl font-bold tracking-tight">System Requirements</h2>
            </div>
            <div className="space-y-4 text-sm flex-1">
              <div className="grid grid-cols-2 gap-2 border-b border-border/50 pb-3">
                <span className="text-muted-foreground">Java runtime</span>
                <span className="font-semibold text-right">Java 8 or higher</span>
              </div>
              <div className="grid grid-cols-2 gap-2 border-b border-border/50 pb-3">
                <span className="text-muted-foreground">Architecture</span>
                <span className="font-semibold text-right">x86_64, ARM64 (Apple Silicon)</span>
              </div>
              <div className="grid grid-cols-2 gap-2 border-b border-border/50 pb-3">
                <span className="text-muted-foreground">Graphics</span>
                <span className="font-semibold text-right">OpenGL / Java Swing supported driver</span>
              </div>
              <div className="grid grid-cols-2 gap-2 pb-1">
                <span className="text-muted-foreground">Memory footprint</span>
                <span className="font-semibold text-right">&lt; 50 MB standard active heap</span>
              </div>
            </div>
            <div className="bg-muted/30 rounded-lg p-4 flex items-start gap-3">
              <Info className="h-5 w-5 text-blue-accent mt-0.5 shrink-0" />
              <p className="text-xs text-muted-foreground leading-relaxed">
                Java Runtime Environment (JRE) is mandatory. If you do not have Java installed, you will receive a command-line or system error stating <code className="text-[10px] bg-muted px-1 py-0.5 rounded font-mono font-bold">java: command not found</code>.
              </p>
            </div>
          </Card>

          {/* Need Java? Card */}
          <Card className="flex flex-col border border-border bg-card/30 p-6 sm:p-8 space-y-6">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-purple-accent/10">
                <Terminal className="h-5 w-5 text-purple-accent" />
              </div>
              <h2 className="text-xl font-bold tracking-tight">Need Java?</h2>
            </div>
            <p className="text-sm text-muted-foreground leading-relaxed">
              To run the universal JAR, you must have a Java Runtime Environment (JRE) installed. Use the quick commands below to install the default runtime for your operating system:
            </p>
            <div className="space-y-4 flex-1">
              {/* Windows */}
              <div className="space-y-1.5">
                <div className="flex items-center gap-1.5 text-xs font-semibold text-foreground">
                  <Monitor className="h-3.5 w-3.5 text-primary" />
                  Windows (Winget)
                </div>
                <div className="relative group rounded-md border border-border bg-black/40 px-3 py-2 font-mono text-[11px] flex items-center justify-between gap-2">
                  <code className="text-purple-accent">winget install Microsoft.OpenJDK.17</code>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-6 w-6 text-muted-foreground hover:text-foreground opacity-80 group-hover:opacity-100"
                    onClick={() => handleCopy("winget install Microsoft.OpenJDK.17", "install-win")}
                  >
                    {copiedText === "install-win" ? (
                      <Check className="h-3 w-3 text-emerald-400" />
                    ) : (
                      <Copy className="h-3 w-3" />
                    )}
                  </Button>
                </div>
              </div>

              {/* macOS */}
              <div className="space-y-1.5">
                <div className="flex items-center gap-1.5 text-xs font-semibold text-foreground">
                  <Apple className="h-3.5 w-3.5 text-primary" />
                  macOS (Homebrew)
                </div>
                <div className="relative group rounded-md border border-border bg-black/40 px-3 py-2 font-mono text-[11px] flex items-center justify-between gap-2">
                  <code className="text-purple-accent">brew install openjdk@17</code>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-6 w-6 text-muted-foreground hover:text-foreground opacity-80 group-hover:opacity-100"
                    onClick={() => handleCopy("brew install openjdk@17", "install-mac")}
                  >
                    {copiedText === "install-mac" ? (
                      <Check className="h-3 w-3 text-emerald-400" />
                    ) : (
                      <Copy className="h-3 w-3" />
                    )}
                  </Button>
                </div>
              </div>

              {/* Linux */}
              <div className="space-y-1.5">
                <div className="flex items-center gap-1.5 text-xs font-semibold text-foreground">
                  <Terminal className="h-3.5 w-3.5 text-primary" />
                  Linux (Ubuntu / Debian / Mint)
                </div>
                <div className="relative group rounded-md border border-border bg-black/40 px-3 py-2 font-mono text-[11px] flex items-center justify-between gap-2">
                  <code className="text-purple-accent">sudo apt install default-jre</code>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-6 w-6 text-muted-foreground hover:text-foreground opacity-80 group-hover:opacity-100"
                    onClick={() => handleCopy("sudo apt install default-jre", "install-linux")}
                  >
                    {copiedText === "install-linux" ? (
                      <Check className="h-3 w-3 text-emerald-400" />
                    ) : (
                      <Copy className="h-3 w-3" />
                    )}
                  </Button>
                </div>
              </div>
            </div>
          </Card>
        </motion.div>

        {/* FAQ Section */}
        <motion.div variants={itemVariants} className="space-y-6 pt-4 border-t border-border">
          <div className="text-center space-y-2 max-w-xl mx-auto">
            <div className="inline-flex p-2 rounded-full bg-primary/10 mb-2">
              <HelpCircle className="h-5 w-5 text-primary" />
            </div>
            <h2 className="text-2xl font-bold tracking-tight">Frequently Asked Questions</h2>
            <p className="text-muted-foreground text-sm leading-relaxed">
              Find answers to commonly encountered setup issues, configurations, and compatibility questions.
            </p>
          </div>

          <div className="max-w-3xl mx-auto space-y-4">
            {faqs.map((faq, idx) => {
              const isOpen = openFaq === idx;
              return (
                <div
                  key={idx}
                  className="rounded-xl border border-border/80 bg-card/25 overflow-hidden transition-all duration-300"
                >
                  <button
                    onClick={() => setOpenFaq(isOpen ? null : idx)}
                    className="w-full px-6 py-4 flex items-center justify-between gap-4 text-left font-medium hover:text-primary transition-colors outline-none"
                  >
                    <span className="text-sm sm:text-base font-semibold">{faq.question}</span>
                    <ChevronDown className={`h-4 w-4 text-muted-foreground shrink-0 transition-transform duration-300 ${isOpen ? "rotate-180 text-primary" : ""}`} />
                  </button>

                  <AnimatePresence initial={false}>
                    {isOpen && (
                      <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: "auto", opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.3, ease: "easeInOut" }}
                      >
                        <div className="px-6 pb-5 pt-1 text-sm text-muted-foreground leading-relaxed border-t border-border/30 bg-muted/10">
                          {faq.answer}
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              );
            })}
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
}
