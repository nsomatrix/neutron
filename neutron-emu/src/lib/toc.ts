export interface TocItem {
  id: string;
  title: string;
  level: number;
}

export function extractToc(content: string): TocItem[] {
  const headingRegex = /^(#{2,4})\s+(.+)$/gm;
  const toc: TocItem[] = [];
  let match;

  while ((match = headingRegex.exec(content)) !== null) {
    const level = match[1].length;
    const title = match[2]
      .replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
      .replace(/[`*_~]/g, "")
      .trim();
    const id = title
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/(^-|-$)/g, "");

    toc.push({ id, title, level });
  }

  return toc;
}
