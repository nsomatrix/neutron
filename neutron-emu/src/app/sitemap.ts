import type { MetadataRoute } from "next";
import { siteConfig } from "@/config/site";
import { getAllDocs } from "@/lib/docs";

export default function sitemap(): MetadataRoute.Sitemap {
  const docs = getAllDocs();
  const docRoutes = docs.map((doc) => ({
    url: `${siteConfig.url}/docs/${doc.meta.slug}`,
    lastModified: new Date(),
    changeFrequency: "weekly" as const,
    priority: 0.8,
  }));

  return [
    {
      url: siteConfig.url,
      lastModified: new Date(),
      changeFrequency: "monthly",
      priority: 1,
    },
    {
      url: `${siteConfig.url}/docs`,
      lastModified: new Date(),
      changeFrequency: "weekly",
      priority: 0.9,
    },
    {
      url: `${siteConfig.url}/download`,
      lastModified: new Date(),
      changeFrequency: "monthly",
      priority: 0.8,
    },
    {
      url: `${siteConfig.url}/blog`,
      lastModified: new Date(),
      changeFrequency: "weekly",
      priority: 0.7,
    },
    {
      url: `${siteConfig.url}/changelog`,
      lastModified: new Date(),
      changeFrequency: "weekly",
      priority: 0.7,
    },
    ...docRoutes,
  ];
}
