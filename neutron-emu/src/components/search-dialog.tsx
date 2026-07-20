"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import { Search, FileText, ArrowRight, Command } from "lucide-react";
import { cn } from "@/lib/utils";
import { type SearchResult } from "@/lib/search";

export function SearchDialog() {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<SearchResult[]>([]);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [index, setIndex] = useState<SearchResult[]>([]);
  const inputRef = useRef<HTMLInputElement>(null);
  const router = useRouter();

  // Load search index
  useEffect(() => {
    fetch("/api/search")
      .then((r) => r.json())
      .then((data: SearchResult[]) => setIndex(data))
      .catch(() => {});
  }, []);

  // Keyboard shortcut
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if ((e.metaKey || e.ctrlKey) && e.key === "k") {
        e.preventDefault();
        setOpen(true);
      }
      if (e.key === "Escape") {
        setOpen(false);
      }
    }
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  // Focus input when open
  useEffect(() => {
    if (open) {
      setTimeout(() => inputRef.current?.focus(), 50);
      setQuery("");
      setResults([]);
      setSelectedIndex(0);
    }
  }, [open]);

  // Search
  const handleSearch = useCallback(
    (q: string) => {
      setQuery(q);
      setSelectedIndex(0);
      if (!q.trim()) {
        setResults([]);
        return;
      }

      const terms = q.toLowerCase().split(/\s+/);
      const scored = index
        .map((item) => {
          const text =
            `${item.title} ${item.description} ${item.content}`.toLowerCase();
          let score = 0;
          for (const term of terms) {
            if (item.title.toLowerCase().includes(term)) score += 10;
            if (item.description.toLowerCase().includes(term)) score += 5;
            if (text.includes(term)) score += 1;
          }
          return { ...item, score };
        })
        .filter((item) => item.score > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, 8);

      setResults(scored);
    },
    [index]
  );

  function handleSelect(href: string) {
    setOpen(false);
    router.push(href);
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setSelectedIndex((i) => Math.min(i + 1, results.length - 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setSelectedIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === "Enter" && results[selectedIndex]) {
      handleSelect(results[selectedIndex].href);
    }
  }

  function highlightMatch(text: string, q: string) {
    if (!q.trim()) return text;
    const regex = new RegExp(
      `(${q.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")})`,
      "gi"
    );
    const parts = text.split(regex);
    return parts.map((part, i) =>
      regex.test(part) ? (
        <mark key={i} className="bg-primary/20 text-primary rounded px-0.5">
          {part}
        </mark>
      ) : (
        part
      )
    );
  }

  return (
    <>
      {/* Trigger button */}
      <button
        onClick={() => setOpen(true)}
        className="flex h-9 items-center gap-2 rounded-lg border border-border bg-muted/50 px-3 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground md:w-64"
        aria-label="Search documentation"
        id="search-trigger"
      >
        <Search className="h-4 w-4 shrink-0" />
        <span className="hidden md:inline">Search docs...</span>
        <kbd className="ml-auto hidden items-center gap-0.5 rounded border border-border bg-background px-1.5 py-0.5 font-mono text-xs text-muted-foreground md:flex">
          <Command className="h-3 w-3" />K
        </kbd>
      </button>

      {/* Dialog */}
      {open && (
        <div className="fixed inset-0 z-[100]" role="dialog" aria-modal="true" aria-label="Search documentation">
          {/* Backdrop */}
          <div
            className="absolute inset-0 bg-background/80 backdrop-blur-sm"
            onClick={() => setOpen(false)}
          />

          {/* Panel */}
          <div className="relative mx-auto mt-[10vh] w-full max-w-xl px-4 animate-slide-down">
            <div className="overflow-hidden rounded-xl border border-border bg-card shadow-2xl">
              {/* Input */}
              <div className="flex items-center gap-3 border-b border-border px-4">
                <Search className="h-4 w-4 shrink-0 text-muted-foreground" />
                <input
                  ref={inputRef}
                  type="text"
                  placeholder="Search documentation..."
                  value={query}
                  onChange={(e) => handleSearch(e.target.value)}
                  onKeyDown={handleKeyDown}
                  className="h-12 w-full bg-transparent text-sm text-foreground placeholder:text-muted-foreground focus:outline-none"
                  aria-label="Search query"
                />
                <kbd className="rounded border border-border bg-muted px-1.5 py-0.5 font-mono text-xs text-muted-foreground">
                  Esc
                </kbd>
              </div>

              {/* Results */}
              <div className="max-h-80 overflow-y-auto p-2">
                {query && results.length === 0 && (
                  <div className="px-4 py-8 text-center text-sm text-muted-foreground">
                    No results found for &ldquo;{query}&rdquo;
                  </div>
                )}

                {results.map((result, i) => (
                  <button
                    key={result.href}
                    onClick={() => handleSelect(result.href)}
                    onMouseEnter={() => setSelectedIndex(i)}
                    className={cn(
                      "flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left transition-colors",
                      i === selectedIndex
                        ? "bg-primary/10 text-foreground"
                        : "text-muted-foreground hover:bg-muted"
                    )}
                  >
                    <FileText className="h-4 w-4 shrink-0 text-primary" />
                    <div className="min-w-0 flex-1">
                      <div className="truncate text-sm font-medium">
                        {highlightMatch(result.title, query)}
                      </div>
                      {result.description && (
                        <div className="truncate text-xs text-muted-foreground">
                          {highlightMatch(result.description, query)}
                        </div>
                      )}
                    </div>
                    <ArrowRight className="h-3.5 w-3.5 shrink-0 opacity-0 group-hover:opacity-100" />
                  </button>
                ))}

                {!query && (
                  <div className="px-4 py-8 text-center text-sm text-muted-foreground">
                    Type to search the documentation
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="flex items-center justify-between border-t border-border px-4 py-2 text-xs text-muted-foreground">
                <div className="flex items-center gap-2">
                  <kbd className="rounded border border-border bg-muted px-1 py-0.5 font-mono">↑↓</kbd>
                  <span>Navigate</span>
                  <kbd className="rounded border border-border bg-muted px-1 py-0.5 font-mono">↵</kbd>
                  <span>Select</span>
                </div>
                <span>Powered by Neutron</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
