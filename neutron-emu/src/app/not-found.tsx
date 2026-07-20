import type { Metadata } from "next";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Home } from "lucide-react";

export const metadata: Metadata = {
  title: "404 — Page Not Found",
  description: "The page you're looking for doesn't exist.",
};

export default function NotFound() {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center px-4 text-center">
      <div className="relative mb-8">
        <span className="text-[10rem] font-bold leading-none text-muted/50 select-none">
          404
        </span>
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="h-16 w-16 rounded-full bg-gradient-to-br from-blue-accent/20 via-cyan-accent/10 to-purple-accent/20 blur-xl" />
        </div>
      </div>

      <h1 className="text-2xl font-bold tracking-tight">Page not found</h1>
      <p className="mt-3 max-w-md text-muted-foreground">
        The page you&apos;re looking for doesn&apos;t exist or has been moved.
        Check the URL or head back to the documentation.
      </p>

      <div className="mt-8 flex gap-4">
        <Button asChild variant="outline">
          <Link href="/">
            <Home className="h-4 w-4" />
            Home
          </Link>
        </Button>
        <Button asChild>
          <Link href="/docs">
            <ArrowLeft className="h-4 w-4" />
            Documentation
          </Link>
        </Button>
      </div>
    </div>
  );
}
