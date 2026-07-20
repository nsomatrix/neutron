import type { Metadata } from "next";
import { siteConfig } from "@/config/site";
import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { ExternalLink } from "lucide-react";

export const metadata: Metadata = {
  title: "Blog",
  description: "News, updates, and technical articles about Neutron.",
};

const posts = [
  {
    title: "Introducing Neutron v2.0",
    description:
      "A complete rewrite with a modern UI, plugin system, and dramatically improved performance.",
    date: "2024-01-15",
    tag: "Release",
    readTime: "5 min read",
  },
  {
    title: "How We Achieved 60fps Java ME Emulation",
    description:
      "A deep dive into the rendering optimizations that make Neutron the fastest Java ME emulator.",
    date: "2023-12-01",
    tag: "Engineering",
    readTime: "8 min read",
  },
  {
    title: "Building a Plugin System for Java Emulators",
    description:
      "The architecture decisions behind Neutron's extensible plugin framework.",
    date: "2023-10-15",
    tag: "Engineering",
    readTime: "6 min read",
  },
  {
    title: "The State of Java ME in 2024",
    description:
      "Why Java ME still matters and how Neutron is preserving mobile gaming history.",
    date: "2023-09-01",
    tag: "Community",
    readTime: "4 min read",
  },
];

export default function BlogPage() {
  return (
    <div className="mx-auto max-w-3xl px-4 py-16">
      <h1 className="text-4xl font-bold tracking-tight">Blog</h1>
      <p className="mt-4 text-lg text-muted-foreground">
        News, updates, and technical articles about Neutron.
      </p>

      <div className="mt-12 space-y-6">
        {posts.map((post) => (
          <Card key={post.title} hover className="group cursor-pointer">
            <div className="flex items-start justify-between gap-4">
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <Badge variant="secondary">{post.tag}</Badge>
                  <span className="text-xs text-muted-foreground">
                    {post.date}
                  </span>
                  <span className="text-xs text-muted-foreground">
                    · {post.readTime}
                  </span>
                </div>
                <h2 className="text-lg font-semibold text-foreground group-hover:text-primary transition-colors">
                  {post.title}
                </h2>
                <p className="mt-1 text-sm text-muted-foreground">
                  {post.description}
                </p>
              </div>
              <ExternalLink className="h-4 w-4 shrink-0 text-muted-foreground opacity-0 group-hover:opacity-100 transition-opacity mt-1" />
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
