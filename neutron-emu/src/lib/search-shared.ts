export interface SearchResult {
  title: string;
  description: string;
  href: string;
  section?: string;
  content: string;
}

export function getSearchExcerpt(content: string, query: string, maxLen = 110): string {
  if (!content) return "";
  const terms = query
    .toLowerCase()
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  if (terms.length === 0) return content.slice(0, maxLen);

  const lower = content.toLowerCase();
  let firstIdx = -1;
  for (const term of terms) {
    const idx = lower.indexOf(term);
    if (idx !== -1 && (firstIdx === -1 || idx < firstIdx)) {
      firstIdx = idx;
    }
  }

  if (firstIdx === -1) {
    return content.length > maxLen ? `${content.slice(0, maxLen).trim()}…` : content;
  }

  const start = Math.max(0, firstIdx - 36);
  const end = Math.min(content.length, start + maxLen);
  const slice = content.slice(start, end).trim();
  return `${start > 0 ? "…" : ""}${slice}${end < content.length ? "…" : ""}`;
}

