import type { MDXComponents } from "mdx/types";
import Link from "next/link";
import { Callout } from "@/components/mdx/callout";
import { CodeBlock } from "@/components/mdx/code-block";
import { TabGroup, TabList, Tab, TabPanel } from "@/components/mdx/tabs";
import { AccordionGroup, AccordionPanel } from "@/components/mdx/accordion";

export function useMDXComponents(): MDXComponents {
  return {
    // Override HTML elements — rehype-slug adds IDs, headings just need scroll-margin
    h1: ({ children, id, ...props }) => (
      <h1 id={id} className="scroll-mt-20" {...props}>
        {children}
      </h1>
    ),
    h2: ({ children, id, ...props }) => (
      <h2 id={id} className="scroll-mt-20 group" {...props}>
        {children}
      </h2>
    ),
    h3: ({ children, id, ...props }) => (
      <h3 id={id} className="scroll-mt-20 group" {...props}>
        {children}
      </h3>
    ),
    h4: ({ children, id, ...props }) => (
      <h4 id={id} className="scroll-mt-20 group" {...props}>
        {children}
      </h4>
    ),
    a: ({ href, children, ...props }) => {
      if (href?.startsWith("/")) {
        return (
          <Link href={href} {...props}>
            {children}
          </Link>
        );
      }
      if (href?.startsWith("#")) {
        return (
          <a href={href} {...props}>
            {children}
          </a>
        );
      }
      return (
        <a href={href} target="_blank" rel="noopener noreferrer" {...props}>
          {children}
        </a>
      );
    },
    pre: ({ children, ...props }) => (
      <CodeBlock {...props}>{children}</CodeBlock>
    ),
    table: ({ children, ...props }) => (
      <div className="my-6 overflow-x-auto rounded-lg border border-border">
        <table {...props}>{children}</table>
      </div>
    ),
    // Custom components
    Callout,
    CodeBlock,
    TabGroup,
    TabList,
    Tab,
    TabPanel,
    AccordionGroup,
    AccordionPanel,
  };
}
