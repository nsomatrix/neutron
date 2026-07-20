"use client";

import { useState, useEffect } from "react";
import { usePathname } from "next/navigation";
import { Menu, X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { DocsSidebar } from "@/components/docs-sidebar";

export function MobileSidebar() {
  const [open, setOpen] = useState(false);
  const pathname = usePathname();

  useEffect(() => {
    setOpen(false);
  }, [pathname]);

  useEffect(() => {
    if (open) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

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

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="fixed inset-0 z-50 lg:hidden"
          >
            <div
              className="absolute inset-0 bg-background/80 backdrop-blur-sm"
              onClick={() => setOpen(false)}
            />
            <motion.div
              initial={{ x: "-100%" }}
              animate={{ x: 0 }}
              exit={{ x: "-100%" }}
              transition={{ type: "spring", damping: 30, stiffness: 300 }}
              className="absolute left-0 top-0 h-full w-72 border-r border-border bg-background p-6 shadow-2xl overflow-y-auto"
            >
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-sm font-semibold text-foreground">Navigation</h2>
                <button
                  onClick={() => setOpen(false)}
                  className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:text-foreground"
                  aria-label="Close sidebar"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>
              <DocsSidebar />
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
