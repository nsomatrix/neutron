"use client";

import { useState, useRef } from "react";
import { Check, Copy } from "lucide-react";
import { cn } from "@/lib/utils";

interface CodeBlockProps {
  children: React.ReactNode;
  className?: string;
  filename?: string;
  showLineNumbers?: boolean;
}

export function CodeBlock({ children, className, filename }: CodeBlockProps) {
  const [copied, setCopied] = useState(false);
  const preRef = useRef<HTMLPreElement>(null);

  async function handleCopy() {
    const code = preRef.current?.textContent || "";
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }

  return (
    <div className={cn("group relative my-6", className)}>
      {filename && (
        <div className="inline-block rounded-t-lg border border-b-0 border-border bg-muted px-3 py-1.5 font-mono text-xs text-muted-foreground">
          {filename}
        </div>
      )}
      <div className="relative">
        <pre
          ref={preRef}
          className={cn(
            "overflow-x-auto rounded-lg border border-border bg-[#0a0a0c] p-4 text-sm",
            filename && "rounded-tl-none"
          )}
        >
          {children}
        </pre>
        <button
          onClick={handleCopy}
          className={cn(
            "absolute right-3 top-3 flex h-7 w-7 items-center justify-center rounded-md border border-border bg-background/80 text-muted-foreground opacity-0 transition-all group-hover:opacity-100",
            "hover:bg-muted hover:text-foreground",
            copied && "text-emerald-500 opacity-100"
          )}
          aria-label={copied ? "Copied" : "Copy code"}
        >
          {copied ? (
            <Check className="h-3.5 w-3.5" />
          ) : (
            <Copy className="h-3.5 w-3.5" />
          )}
        </button>
      </div>
    </div>
  );
}

// Copy button for use with rehype-pretty-code
export function CopyButton({ text }: { text: string }) {
  const [copied, setCopied] = useState(false);

  return (
    <button
      onClick={async () => {
        await navigator.clipboard.writeText(text);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
      }}
      className={cn(
        "absolute right-3 top-3 flex h-7 w-7 items-center justify-center rounded-md border border-border bg-background/80 text-muted-foreground opacity-0 transition-all group-hover:opacity-100",
        "hover:bg-muted hover:text-foreground",
        copied && "text-emerald-500 opacity-100"
      )}
      aria-label={copied ? "Copied" : "Copy code"}
    >
      {copied ? (
        <Check className="h-3.5 w-3.5" />
      ) : (
        <Copy className="h-3.5 w-3.5" />
      )}
    </button>
  );
}
