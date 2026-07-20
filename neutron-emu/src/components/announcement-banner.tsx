"use client";

import Link from "next/link";
import { siteConfig } from "@/config/site";
import { X } from "lucide-react";
import { useState } from "react";

export function AnnouncementBanner() {
  const [visible, setVisible] = useState<boolean>(siteConfig.announcement.enabled);

  if (!visible) return null;

  return (
    <div className="relative z-50 flex items-center justify-center gap-2 border-b border-border bg-gradient-to-r from-blue-accent/10 via-purple-accent/10 to-cyan-accent/10 px-4 pr-10 py-2 text-sm">
      <p className="text-muted-foreground text-center">
        {siteConfig.announcement.message}{" "}
        <Link
          href={siteConfig.announcement.link}
          className="font-medium text-primary hover:text-cyan-accent transition-colors"
        >
          {siteConfig.announcement.linkText}
        </Link>
      </p>
      <button
        onClick={() => setVisible(false)}
        className="absolute right-3 top-1/2 -translate-y-1/2 rounded-md p-1 text-muted-foreground hover:text-foreground transition-colors"
        aria-label="Dismiss announcement"
      >
        <X className="h-3.5 w-3.5" />
      </button>
    </div>
  );
}
