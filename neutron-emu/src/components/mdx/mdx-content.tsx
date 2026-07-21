import { MDXRemote } from "next-mdx-remote/rsc";
import remarkGfm from "remark-gfm";
import rehypeSlug from "rehype-slug";
import rehypePrettyCode from "rehype-pretty-code";
import { useMDXComponents } from "@/components/mdx/mdx-components";

interface MdxContentProps {
  source: string;
}

export async function MdxContent({ source }: MdxContentProps) {
  const components = useMDXComponents();

  return (
    <div className="prose">
      <MDXRemote
        source={source}
        components={components}
        options={{
          mdxOptions: {
            remarkPlugins: [remarkGfm],
            rehypePlugins: [
              rehypeSlug,
              [
                rehypePrettyCode,
                {
                  theme: {
                    dark: "dracula",
                    light: "github-light",
                  },
                  defaultColor: false,
                },
              ],
            ],
          },
        }}
      />
    </div>
  );
}
