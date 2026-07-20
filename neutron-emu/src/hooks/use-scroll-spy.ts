"use client";

import { useState, useEffect, useCallback } from "react";

export function useScrollSpy(ids: string[], offset = 80) {
  const [activeId, setActiveId] = useState<string>("");

  const handleScroll = useCallback(() => {
    const scrollY = window.scrollY;

    for (let i = ids.length - 1; i >= 0; i--) {
      const element = document.getElementById(ids[i]);
      if (element) {
        const top = element.offsetTop - offset;
        if (scrollY >= top) {
          setActiveId(ids[i]);
          return;
        }
      }
    }

    setActiveId(ids[0] || "");
  }, [ids, offset]);

  useEffect(() => {
    handleScroll();
    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  return activeId;
}
