import fs from "fs";
import path from "path";
import matter from "gray-matter";

const contentDir = path.join(/*turbopackIgnore: true*/ process.cwd(), "src/content/docs");

export interface DocMeta {
  title: string;
  description: string;
  slug: string;
  order?: number;
}

export interface Doc {
  meta: DocMeta;
  content: string;
}

export function getDocBySlug(slugParts: string[]): Doc | null {
  const filePath = path.join(contentDir, ...slugParts) + ".mdx";
  const indexPath = path.join(contentDir, ...slugParts, "index.mdx");

  let fullPath: string;

  if (fs.existsSync(filePath)) {
    fullPath = filePath;
  } else if (fs.existsSync(indexPath)) {
    fullPath = indexPath;
  } else {
    return null;
  }

  const fileContents = fs.readFileSync(fullPath, "utf8");
  const { data, content } = matter(fileContents);

  return {
    meta: {
      title: (data.title as string) || "Untitled",
      description: (data.description as string) || "",
      slug: slugParts.join("/"),
      order: data.order as number | undefined,
    },
    content,
  };
}

export function getAllDocs(): Doc[] {
  const docs: Doc[] = [];

  function walkDir(dir: string, slugPrefix: string[] = []) {
    if (!fs.existsSync(dir)) return;

    const entries = fs.readdirSync(dir, { withFileTypes: true });

    for (const entry of entries) {
      if (entry.isDirectory()) {
        walkDir(path.join(dir, entry.name), [...slugPrefix, entry.name]);
      } else if (entry.name.endsWith(".mdx")) {
        const name = entry.name.replace(/\.mdx$/, "");
        const slugParts =
          name === "index" ? slugPrefix : [...slugPrefix, name];
        const doc = getDocBySlug(slugParts);
        if (doc) docs.push(doc);
      }
    }
  }

  walkDir(contentDir);
  return docs;
}

export function getDocSlugs(): string[][] {
  return getAllDocs().map((doc) => doc.meta.slug.split("/"));
}
