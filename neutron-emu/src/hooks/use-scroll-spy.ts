"use client";

import { useSyncExternalStore, useCallback } from "react";

export function useScrollSpy(ids: string[], offset = 80) {
  const getSnapshot = useCallback(() => {
    if (typeof window === "undefined") return ids[0] || "";
    const scrollY = window.scrollY;

    for (let i = ids.length - 1; i >= 0; i--) {
      const element = document.getElementById(ids[i]);
      if (element) {
        const top = element.offsetTop - offset;
        if (scrollY >= top) {
          return ids[i];
        }
      }
    }

    return ids[0] || "";
  }, [ids, offset]);

  const subscribe = useCallback(
    (onStoreChange: () => void) => {
      window.addEventListener("scroll", onStoreChange, { passive: true });
      return () => window.removeEventListener("scroll", onStoreChange);
    },
    []
  );

  return useSyncExternalStore(subscribe, getSnapshot, () => ids[0] || "");
}

