"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import * as VisuallyHidden from "@radix-ui/react-visually-hidden";
import { Search, FileText, ArrowRight, Command, X } from "lucide-react";
import { cn } from "@/lib/utils";
import { getSearchExcerpt, type SearchResult } from "@/lib/search-shared";

interface RankedSearchResult extends SearchResult {
  score: number;
}

export function SearchDialog() {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [index, setIndex] = useState<SearchResult[]>([]);
  const [isLoadingIndex, setIsLoadingIndex] = useState(false);
  const hasFetchedRef = useRef(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const itemRefs = useRef<(HTMLButtonElement | null)[]>([]);
  const router = useRouter();

  const ensureIndexLoaded = useCallback(async () => {
    if (hasFetchedRef.current) return;
    hasFetchedRef.current = true;
    setIsLoadingIndex(true);
    try {
      const res = await fetch("/api/search");
      if (res.ok) {
        const data = (await res.json()) as SearchResult[];
        setIndex(data);
      } else {
        hasFetchedRef.current = false;
      }
    } catch {
      hasFetchedRef.current = false;
    } finally {
      setIsLoadingIndex(false);
    }
  }, []);

  const handleOpenChange = useCallback(
    (nextOpen: boolean) => {
      if (nextOpen) {
        setQuery("");
        setSelectedIndex(0);
        void ensureIndexLoaded();
      }
      setOpen(nextOpen);
    },
    [ensureIndexLoaded]
  );

  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === "k") {
        e.preventDefault();
        handleOpenChange(!open);
      }
    }
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [open, handleOpenChange]);

  const terms = query
    .toLowerCase()
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  const results: RankedSearchResult[] =
    terms.length === 0
      ? index.slice(0, 6).map((item) => ({ ...item, score: 0 }))
      : index
          .map((item) => {
            const titleLower = item.title.toLowerCase();
            const descLower = item.description.toLowerCase();
            const contentLower = item.content.toLowerCase();
            let score = 0;

            for (const term of terms) {
              const inTitle = titleLower.includes(term);
              const inDesc = descLower.includes(term);
              const inContent = contentLower.includes(term);
              if (!inTitle && !inDesc && !inContent) {
                return { ...item, score: 0 };
              }
              if (inTitle) score += 12;
              if (inDesc) score += 5;
              if (inContent) score += 1;
            }

            if (titleLower.startsWith(query.toLowerCase().trim())) {
              score += 8;
            }
            return { ...item, score };
          })
          .filter((item) => item.score > 0)
          .sort((a, b) => b.score - a.score)
          .slice(0, 10);

  const activeIndex = results.length > 0 ? Math.min(selectedIndex, results.length - 1) : -1;

  useEffect(() => {
    if (activeIndex >= 0 && itemRefs.current[activeIndex]) {
      itemRefs.current[activeIndex]?.scrollIntoView({ block: "nearest" });
    }
  }, [activeIndex]);

  function handleSelect(href: string) {
    setOpen(false);
    router.push(href);
  }

  function handleInputKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (results.length === 0) return;

    if (e.key === "ArrowDown") {
      e.preventDefault();
      setSelectedIndex((i) => (i + 1 >= results.length ? 0 : i + 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setSelectedIndex((i) => (i - 1 < 0 ? results.length - 1 : i - 1));
    } else if (e.key === "Enter" && activeIndex >= 0 && results[activeIndex]) {
      e.preventDefault();
      handleSelect(results[activeIndex].href);
    }
  }

  function highlightMatch(text: string) {
    if (terms.length === 0 || !text) return text;
    const escaped = terms.map((t) => t.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"));
    const splitRegex = new RegExp(`(${escaped.join("|")})`, "gi");
    const parts = text.split(splitRegex);

    return parts.map((part, i) =>
      i % 2 === 1 ? (
        <mark key={i} className="rounded bg-primary/15 px-0.5 font-medium text-primary">
          {part}
        </mark>
      ) : (
        part
      )
    );
  }

  return (
    <DialogPrimitive.Root open={open} onOpenChange={handleOpenChange}>
      <DialogPrimitive.Trigger asChild>
        <button
          type="button"
          onMouseEnter={() => void ensureIndexLoaded()}
          onFocus={() => void ensureIndexLoaded()}
          className="flex h-9 w-9 items-center justify-center rounded-lg border border-border bg-muted/50 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground sm:w-52 sm:justify-start sm:gap-2 sm:px-3 md:w-64"
          aria-label="Search documentation"
          id="search-trigger"
        >
          <Search className="h-4 w-4 shrink-0" />
          <span className="hidden sm:inline truncate">Search docs...</span>
          <kbd className="ml-auto hidden items-center gap-0.5 rounded border border-border bg-background px-1.5 py-0.5 font-mono text-xs text-muted-foreground sm:inline-flex">
            <Command className="h-3 w-3" />K
          </kbd>
        </button>
      </DialogPrimitive.Trigger>

      <DialogPrimitive.Portal>
        <DialogPrimitive.Overlay className="fixed inset-0 z-[100] bg-background/75 backdrop-blur-sm animate-[fade-in_150ms_ease-out]" />

        <DialogPrimitive.Content
          onOpenAutoFocus={(e) => {
            e.preventDefault();
            inputRef.current?.focus();
          }}
          className="fixed left-1/2 top-[max(0.75rem,env(safe-area-inset-top))] z-[101] flex max-h-[calc(100dvh-1.5rem)] w-[calc(100%-1.5rem)] max-w-xl -translate-x-1/2 flex-col overflow-hidden rounded-xl border border-border bg-card text-card-foreground shadow-2xl focus:outline-none sm:top-[12vh] sm:max-h-[75dvh]"
        >
          <VisuallyHidden.Root>
            <DialogPrimitive.Title>Search documentation</DialogPrimitive.Title>
            <DialogPrimitive.Description>
              Search Neutron guides, CLI flags, configuration options, and references.
            </DialogPrimitive.Description>
          </VisuallyHidden.Root>

          {/* Search Input Header */}
          <div className="flex shrink-0 items-center gap-2.5 border-b border-border px-3.5 sm:px-4">
            <Search className="h-4 w-4 shrink-0 text-muted-foreground" />
            <input
              ref={inputRef}
              type="search"
              placeholder="Search documentation..."
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setSelectedIndex(0);
              }}
              onKeyDown={handleInputKeyDown}
              autoComplete="off"
              autoCorrect="off"
              spellCheck={false}
              className="h-12 min-w-0 flex-1 bg-transparent text-sm text-foreground placeholder:text-muted-foreground focus:outline-none [&::-webkit-search-cancel-button]:hidden"
              aria-label="Search query"
              aria-controls="search-results-listbox"
              aria-activedescendant={
                activeIndex >= 0 ? `search-option-${activeIndex}` : undefined
              }
            />

            {query && (
              <button
                type="button"
                onClick={() => {
                  setQuery("");
                  setSelectedIndex(0);
                  inputRef.current?.focus();
                }}
                className="flex h-7 w-7 shrink-0 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                aria-label="Clear search query"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            )}

            <DialogPrimitive.Close asChild>
              <button
                type="button"
                className="inline-flex h-7 shrink-0 items-center justify-center rounded-md border border-border bg-muted/60 px-2 font-mono text-xs text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                aria-label="Close search"
              >
                <span className="hidden sm:inline">Esc</span>
                <X className="h-3.5 w-3.5 sm:hidden" />
              </button>
            </DialogPrimitive.Close>
          </div>

          {/* Results List */}
          <div
            id="search-results-listbox"
            role="listbox"
            aria-label="Search results"
            className="min-h-0 flex-1 overflow-y-auto p-2"
          >
            {isLoadingIndex && index.length === 0 && (
              <div className="px-4 py-8 text-center text-sm text-muted-foreground">
                Loading documentation index...
              </div>
            )}

            {!isLoadingIndex && terms.length > 0 && results.length === 0 && (
              <div className="px-4 py-8 text-center text-sm text-muted-foreground">
                No results found for &ldquo;{query}&rdquo;
              </div>
            )}

            {!isLoadingIndex && terms.length === 0 && results.length > 0 && (
              <div className="px-3 py-1.5 text-xs font-medium text-muted-foreground">
                Quick links
              </div>
            )}

            {results.map((result, i) => {
              const isSelected = i === activeIndex;
              const excerpt =
                terms.length > 0
                  ? getSearchExcerpt(result.content, query)
                  : result.description;

              return (
                <button
                  key={result.href}
                  id={`search-option-${i}`}
                  ref={(el) => {
                    itemRefs.current[i] = el;
                  }}
                  type="button"
                  role="option"
                  aria-selected={isSelected}
                  onClick={() => handleSelect(result.href)}
                  onMouseEnter={() => setSelectedIndex(i)}
                  className={cn(
                    "group flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left transition-colors",
                    isSelected
                      ? "bg-primary/10 text-foreground"
                      : "text-muted-foreground hover:bg-muted/70 hover:text-foreground"
                  )}
                >
                  <FileText
                    className={cn(
                      "h-4 w-4 shrink-0",
                      isSelected ? "text-primary" : "text-muted-foreground group-hover:text-primary"
                    )}
                  />
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <span className="truncate text-sm font-medium text-foreground">
                        {highlightMatch(result.title)}
                      </span>
                      {result.section && (
                        <span className="hidden shrink-0 rounded bg-muted px-1.5 py-0.5 text-[10px] font-medium text-muted-foreground sm:inline-block">
                          {result.section}
                        </span>
                      )}
                    </div>
                    {excerpt && (
                      <div className="mt-0.5 truncate text-xs text-muted-foreground">
                        {highlightMatch(excerpt)}
                      </div>
                    )}
                  </div>
                  <ArrowRight
                    className={cn(
                      "h-3.5 w-3.5 shrink-0 transition-opacity",
                      isSelected ? "opacity-100 text-primary" : "opacity-0 group-hover:opacity-70"
                    )}
                  />
                </button>
              );
            })}
          </div>

          {/* Desktop Keyboard Footer */}
          <div className="hidden shrink-0 items-center justify-between border-t border-border bg-muted/20 px-4 py-2 text-xs text-muted-foreground sm:flex">
            <div className="flex items-center gap-2">
              <kbd className="rounded border border-border bg-muted px-1.5 py-0.5 font-mono text-[11px]">↑↓</kbd>
              <span>Navigate</span>
              <kbd className="rounded border border-border bg-muted px-1.5 py-0.5 font-mono text-[11px]">↵</kbd>
              <span>Select</span>
              <kbd className="rounded border border-border bg-muted px-1.5 py-0.5 font-mono text-[11px]">Esc</kbd>
              <span>Close</span>
            </div>
            <span>{results.length > 0 ? `${results.length} results` : "Neutron Docs"}</span>
          </div>
        </DialogPrimitive.Content>
      </DialogPrimitive.Portal>
    </DialogPrimitive.Root>
  );
}
