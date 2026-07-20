import { notFound } from "next/navigation";
import type { Metadata } from "next";
import { getDocBySlug, getDocSlugs } from "@/lib/docs";
import { extractToc } from "@/lib/toc";
import { MdxContent } from "@/components/mdx/mdx-content";
import { TableOfContents } from "@/components/table-of-contents";
import { Breadcrumb } from "@/components/ui/breadcrumb";
import { Pagination } from "@/components/ui/pagination";
import { docsConfig, type DocsSidebarItem } from "@/config/docs";
import { siteConfig } from "@/config/site";

interface DocPageProps {
  params: Promise<{ slug?: string[] }>;
}

// Flatten docs config for prev/next navigation
function flattenDocs(
  items: DocsSidebarItem[]
): { title: string; href: string }[] {
  const flat: { title: string; href: string }[] = [];
  for (const item of items) {
    if (item.href) {
      flat.push({ title: item.title, href: item.href });
    }
    if (item.items) {
      flat.push(...flattenDocs(item.items));
    }
  }
  return flat;
}

export async function generateStaticParams() {
  const slugs = getDocSlugs();
  return [{ slug: [] }, ...slugs.map((slug) => ({ slug }))];
}

export async function generateMetadata({ params }: DocPageProps): Promise<Metadata> {
  const { slug } = await params;
  const slugParts = slug || [];
  const doc = getDocBySlug(slugParts.length === 0 ? ["index"] : slugParts);

  if (!doc) {
    return { title: "Not Found" };
  }

  return {
    title: doc.meta.title,
    description: doc.meta.description,
    openGraph: {
      title: `${doc.meta.title} | ${siteConfig.name}`,
      description: doc.meta.description,
      type: "article",
      url: `${siteConfig.url}/docs/${doc.meta.slug}`,
    },
  };
}

export default async function DocPage({ params }: DocPageProps) {
  const { slug } = await params;
  const slugParts = slug || [];
  const doc = getDocBySlug(slugParts.length === 0 ? ["index"] : slugParts);

  if (!doc) {
    notFound();
  }

  const toc = extractToc(doc.content);
  const allDocs = flattenDocs(docsConfig);
  const currentPath = slugParts.length === 0 ? "/docs" : `/docs/${slugParts.join("/")}`;
  const currentIndex = allDocs.findIndex((d) => d.href === currentPath);

  const prev = currentIndex > 0 ? allDocs[currentIndex - 1] : undefined;
  const next =
    currentIndex < allDocs.length - 1 ? allDocs[currentIndex + 1] : undefined;

  // Build breadcrumb items
  const breadcrumbItems = slugParts.map((part, i) => ({
    title: part
      .replace(/-/g, " ")
      .replace(/\b\w/g, (c) => c.toUpperCase()),
    href:
      i < slugParts.length - 1
        ? `/docs/${slugParts.slice(0, i + 1).join("/")}`
        : undefined,
  }));

  return (
    <div className="flex">
      {/* Content */}
      <div className="min-w-0 flex-1 px-4 py-8 lg:px-12 lg:py-10">
        {breadcrumbItems.length > 0 && (
          <Breadcrumb items={breadcrumbItems} className="mb-6" />
        )}

        <article>
          <h1 className="text-3xl font-bold tracking-tight lg:text-4xl mb-2">
            {doc.meta.title}
          </h1>
          {doc.meta.description && (
            <p className="text-lg text-muted-foreground mb-8">
              {doc.meta.description}
            </p>
          )}

          <MdxContent source={doc.content} />
        </article>

        <Pagination prev={prev} next={next} />
      </div>

      {/* Table of contents */}
      {toc.length > 0 && (
        <div className="hidden w-56 shrink-0 xl:block">
          <div className="sticky top-14 h-[calc(100vh-3.5rem)] overflow-y-auto py-10 pr-4">
            <TableOfContents items={toc} />
          </div>
        </div>
      )}
    </div>
  );
}
