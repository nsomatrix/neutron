import { getAllDocs, type Doc } from "./docs";
import type { SearchResult } from "./search-shared";

export { getSearchExcerpt, type SearchResult } from "./search-shared";

function cleanMdxText(raw: string): string {
  return raw
    .replace(/^---[\s\S]*?---/, "")
    .replace(/```[^\n]*\n([\s\S]*?)```/g, " $1 ")
    .replace(/#{1,6}\s+/g, "")
    .replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
    .replace(/[`*_~|]/g, " ")
    .replace(/<[^>]*>/g, " ")
    .replace(/\s+:?-{2,}:?\s+/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

export function buildSearchIndex(): SearchResult[] {
  const docs = getAllDocs();

  return docs.map((doc: Doc) => {
    const firstSegment = doc.meta.slug.split("/")[0] || "Documentation";
    const section = firstSegment
      .replace(/-/g, " ")
      .replace(/\b\w/g, (c) => c.toUpperCase());

    return {
      title: doc.meta.title,
      description: doc.meta.description,
      href: doc.meta.slug === "" ? "/docs" : `/docs/${doc.meta.slug}`,
      section: doc.meta.slug === "" ? "Overview" : section,
      content: cleanMdxText(doc.content),
    };
  });
}
