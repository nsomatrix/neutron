"use client";

import { useState, useEffect } from "react";
import { usePathname } from "next/navigation";
import { Menu, X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { DocsSidebar } from "@/components/docs-sidebar";
import { createPortal } from "react-dom";

export function MobileSidebar() {
  const [open, setOpen] = useState(false);
  const [mounted, setMounted] = useState(false);
  const pathname = usePathname();

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    setOpen(false);
  }, [pathname]);

  // Bulletproof body scroll lock for mobile and desktop browsers
  useEffect(() => {
    if (open) {
      // Save current scroll position and set overflow/height locks
      document.body.style.overflow = "hidden";
      document.body.style.height = "100%";
      document.documentElement.style.overflow = "hidden";
      document.documentElement.style.height = "100%";
    } else {
      // Reset locks
      document.body.style.overflow = "";
      document.body.style.height = "";
      document.documentElement.style.overflow = "";
      document.documentElement.style.height = "";
    }
    return () => {
      document.body.style.overflow = "";
      document.body.style.height = "";
      document.documentElement.style.overflow = "";
      document.documentElement.style.height = "";
    };
  }, [open]);

  const sidebarPortalContent = (
    <AnimatePresence>
      {open && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.2, ease: "easeOut" }}
          className="fixed inset-0 z-[100] lg:hidden"
        >
          {/* Backdrop overlay */}
          <div
            className="absolute inset-0 bg-background/80 backdrop-blur-sm cursor-pointer"
            onClick={() => setOpen(false)}
          />
          {/* Sidebar Drawer - Absolute positioned to avoid layout recalculation latency */}
          <motion.div
            initial={{ x: "-105%" }}
            animate={{ x: 0 }}
            exit={{ x: "-105%" }}
            transition={{ duration: 0.25, ease: "easeOut" }}
            style={{ overscrollBehavior: "contain" }}
            className="absolute inset-y-0 left-0 z-50 w-72 border-r border-border bg-background p-6 shadow-2xl overflow-y-auto"
          >
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-sm font-semibold text-foreground">Navigation</h2>
              <button
                onClick={() => setOpen(false)}
                className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:text-foreground transition-colors"
                aria-label="Close sidebar"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
            <DocsSidebar onItemClick={() => setOpen(false)} />
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );

  return (
    <>
      <button
        onClick={() => setOpen(true)}
        className="flex h-9 items-center gap-2 rounded-lg border border-border px-3 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground lg:hidden"
        aria-label="Open sidebar"
      >
        <Menu className="h-4 w-4" />
        <span>Menu</span>
      </button>

      {mounted && typeof document !== "undefined"
        ? createPortal(sidebarPortalContent, document.body)
        : null}
    </>
  );
}
