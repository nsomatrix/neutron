"use client";

import { useState, useEffect } from "react";

export function useReadingProgress() {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    function handleScroll() {
      const scrollHeight =
        document.documentElement.scrollHeight - window.innerHeight;
      if (scrollHeight > 0) {
        setProgress(Math.min((window.scrollY / scrollHeight) * 100, 100));
      }
    }

    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return progress;
}
