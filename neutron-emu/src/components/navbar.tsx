"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState, useEffect } from "react";
import { Menu, X } from "lucide-react";
import { GitHubIcon } from "@/components/icons";
import { cn } from "@/lib/utils";
import { siteConfig } from "@/config/site";
import { Logo } from "@/components/logo";
import { ThemeSwitcher } from "@/components/theme-switcher";
import { SearchDialog } from "@/components/search-dialog";
import { useReadingProgress } from "@/hooks/use-reading-progress";
import { motion, AnimatePresence } from "framer-motion";

export function Navbar() {
  const pathname = usePathname();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const progress = useReadingProgress();
  const isDocsPage = pathname.startsWith("/docs");

  useEffect(() => {
    function handleScroll() {
      setScrolled(window.scrollY > 10);
    }
    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Close mobile menu on route change
  useEffect(() => {
    setMobileOpen(false);
  }, [pathname]);

  // Prevent scroll when mobile menu is open
  useEffect(() => {
    if (mobileOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [mobileOpen]);

  return (
    <>
      <header
        className={cn(
          "sticky top-0 z-50 w-full border-b transition-all duration-300",
          scrolled
            ? "border-border bg-background/80 backdrop-blur-xl shadow-sm"
            : "border-transparent bg-background/60 backdrop-blur-md"
        )}
      >
        <nav
          className="mx-auto flex h-14 max-w-screen-2xl items-center justify-between px-4 lg:px-8"
          aria-label="Main navigation"
        >
          {/* Left */}
          <div className="flex items-center gap-6">
            <Logo />
            <div className="hidden items-center gap-1 md:flex">
              {siteConfig.nav.main.map((item) => (
                <Link
                  key={item.href}
                  href={item.href}
                  className={cn(
                    "rounded-md px-3 py-1.5 text-sm font-medium transition-colors",
                    pathname === item.href || pathname.startsWith(item.href + "/")
                      ? "text-foreground"
                      : "text-muted-foreground hover:text-foreground"
                  )}
                >
                  {item.title}
                </Link>
              ))}
            </div>
          </div>

          {/* Right */}
          <div className="flex items-center gap-3">
            <SearchDialog />
            <a
              href={siteConfig.github}
              target="_blank"
              rel="noopener noreferrer"
              className="hidden h-9 w-9 items-center justify-center rounded-lg border border-border text-muted-foreground transition-colors hover:bg-muted hover:text-foreground md:flex"
              aria-label="GitHub repository"
            >
              <GitHubIcon className="h-4 w-4" />
            </a>
            <div className="hidden md:flex">
              <ThemeSwitcher />
            </div>
            <button
              onClick={() => setMobileOpen(!mobileOpen)}
              className="flex h-9 w-9 items-center justify-center rounded-lg border border-border text-muted-foreground transition-colors hover:bg-muted hover:text-foreground md:hidden"
              aria-label={mobileOpen ? "Close menu" : "Open menu"}
              aria-expanded={mobileOpen}
            >
              {mobileOpen ? (
                <X className="h-4 w-4" />
              ) : (
                <Menu className="h-4 w-4" />
              )}
            </button>
          </div>
        </nav>

        {/* Reading progress */}
        {isDocsPage && (
          <div className="absolute bottom-0 left-0 h-0.5 w-full">
            <div
              className="h-full bg-gradient-to-r from-blue-accent via-cyan-accent to-purple-accent transition-[width] duration-150"
              style={{ width: `${progress}%` }}
              role="progressbar"
              aria-valuenow={Math.round(progress)}
              aria-valuemin={0}
              aria-valuemax={100}
              aria-label="Reading progress"
            />
          </div>
        )}
      </header>

      {/* Mobile menu */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="fixed inset-0 z-40 md:hidden"
          >
            <div
              className="absolute inset-0 bg-background/80 backdrop-blur-sm"
              onClick={() => setMobileOpen(false)}
            />
            <motion.div
              initial={{ x: "100%" }}
              animate={{ x: 0 }}
              exit={{ x: "100%" }}
              transition={{ type: "spring", damping: 30, stiffness: 300 }}
              className="absolute right-0 top-0 h-full w-72 border-l border-border bg-background p-6 shadow-2xl"
            >
              <div className="flex items-center justify-between mb-8">
                <Logo showText={false} />
                <button
                  onClick={() => setMobileOpen(false)}
                  className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:text-foreground"
                  aria-label="Close menu"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>

              <div className="flex flex-col gap-1">
                {siteConfig.nav.mobile.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={cn(
                      "rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                      pathname === item.href ||
                        pathname.startsWith(item.href + "/")
                        ? "bg-primary/10 text-primary"
                        : "text-muted-foreground hover:bg-muted hover:text-foreground"
                    )}
                  >
                    {item.title}
                  </Link>
                ))}
              </div>

              <div className="mt-6 border-t border-border pt-6">
                <div className="flex items-center gap-3">
                  <a
                    href={siteConfig.github}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex h-9 w-9 items-center justify-center rounded-lg border border-border text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                    aria-label="GitHub repository"
                  >
                    <GitHubIcon className="h-4 w-4" />
                  </a>
                  <ThemeSwitcher />
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
