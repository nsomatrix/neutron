import { getAllDocs, type Doc } from "./docs";

export interface SearchResult {
  title: string;
  description: string;
  href: string;
  section?: string;
  content: string;
}

export function buildSearchIndex(): SearchResult[] {
  const docs = getAllDocs();

  return docs.map((doc: Doc) => ({
    title: doc.meta.title,
    description: doc.meta.description,
    href: doc.meta.slug === "" ? "/docs" : `/docs/${doc.meta.slug}`,
    content: doc.content
      .replace(/^---[\s\S]*?---/, "")
      .replace(/#{1,6}\s/g, "")
      .replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
      .replace(/[`*_~]/g, "")
      .replace(/<[^>]*>/g, "")
      .replace(/\n{2,}/g, " ")
      .trim()
      .slice(0, 500),
  }));
}

export function searchDocs(query: string, index: SearchResult[]): SearchResult[] {
  if (!query.trim()) return [];

  const terms = query.toLowerCase().split(/\s+/);

  return index
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
    .slice(0, 10);
}
