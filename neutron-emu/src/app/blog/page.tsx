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
    title: "Introducing Neutron v1.0.0",
    description:
      "The official initial release of Neutron, a modern open-source J2ME emulator for modern platforms.",
    date: "2026-07-21",
    tag: "Release",
    readTime: "3 min read",
  },
  {
    title: "A Clean Swing Interface with FlatLaf",
    description:
      "A deep dive into our custom Java Swing UI redesign, integrating FlatLaf Light, Dark, macOS, and system themes.",
    date: "2026-07-15",
    tag: "Design",
    readTime: "5 min read",
  },
  {
    title: "Implementing Network Access Controls",
    description:
      "How we added security to Java ME networking with proxy authentication (SOCKS5/HTTP) and global network toggles.",
    date: "2026-07-10",
    tag: "Engineering",
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
