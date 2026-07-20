import type { Metadata } from "next";
import Link from "next/link";
import { Download, Monitor, Apple, Terminal } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { siteConfig } from "@/config/site";

export const metadata: Metadata = {
  title: "Download",
  description: "Download Neutron for Windows, macOS, or Linux.",
};

const platforms = [
  {
    name: "Windows",
    icon: Monitor,
    format: ".exe installer",
    size: "~25 MB",
    requirements: "Windows 10 or later",
    link: `${siteConfig.download}`,
  },
  {
    name: "macOS",
    icon: Apple,
    format: ".dmg disk image",
    size: "~30 MB",
    requirements: "macOS 11 or later",
    link: `${siteConfig.download}`,
  },
  {
    name: "Linux",
    icon: Terminal,
    format: ".tar.gz archive",
    size: "~20 MB",
    requirements: "Ubuntu 20.04 or equivalent",
    link: `${siteConfig.download}`,
  },
];

export default function DownloadPage() {
  return (
    <div className="mx-auto max-w-4xl px-4 py-16">
      <div className="text-center mb-16">
        <h1 className="text-4xl font-bold tracking-tight">Download Neutron</h1>
        <p className="mt-4 text-lg text-muted-foreground">
          Choose your platform and get started in seconds.
        </p>
        <Badge className="mt-4">v2.0.0 — Latest Release</Badge>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        {platforms.map((platform) => (
          <Card key={platform.name} hover className="flex flex-col">
            <div className="flex items-center gap-3 mb-4">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                <platform.icon className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-lg font-semibold">{platform.name}</h2>
            </div>
            <div className="space-y-2 text-sm text-muted-foreground flex-1">
              <p>Format: {platform.format}</p>
              <p>Size: {platform.size}</p>
              <p>Requires: {platform.requirements}</p>
            </div>
            <Button asChild className="mt-6 w-full">
              <a href={platform.link} target="_blank" rel="noopener noreferrer">
                <Download className="h-4 w-4" />
                Download
              </a>
            </Button>
          </Card>
        ))}
      </div>

      <div className="mt-16 rounded-xl border border-border bg-card p-8 text-center">
        <h2 className="text-xl font-semibold mb-2">Universal JAR</h2>
        <p className="text-muted-foreground mb-6">
          Works on any platform with Java 11+. No installation required.
        </p>
        <div className="flex flex-col items-center gap-4 sm:flex-row sm:justify-center">
          <Button asChild variant="outline">
            <a href={siteConfig.download} target="_blank" rel="noopener noreferrer">
              <Download className="h-4 w-4" />
              Download JAR (~15 MB)
            </a>
          </Button>
          <Button asChild variant="ghost">
            <Link href="/docs/getting-started/installation">
              Installation Guide
            </Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
