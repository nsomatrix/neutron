export interface DocsSidebarItem {
  title: string;
  href?: string;
  items?: DocsSidebarItem[];
  label?: string;
}

export const docsConfig: DocsSidebarItem[] = [
  {
    title: "Getting Started",
    items: [
      { title: "Introduction", href: "/docs" },
      { title: "Installation", href: "/docs/getting-started/installation" },
      { title: "Requirements", href: "/docs/getting-started/requirements" },
      { title: "Quick Start", href: "/docs/getting-started/quick-start" },
      { title: "Configuration", href: "/docs/getting-started/configuration" },
    ],
  },
  {
    title: "User Guide",
    items: [
      { title: "Features", href: "/docs/user-guide/features" },
      { title: "Graphics", href: "/docs/user-guide/graphics" },
      { title: "Audio", href: "/docs/user-guide/audio" },
      { title: "Input", href: "/docs/user-guide/input" },
      { title: "Networking", href: "/docs/user-guide/networking" },
      { title: "Filesystem", href: "/docs/user-guide/filesystem" },
    ],
  },
  {
    title: "Advanced",
    items: [
      { title: "Performance", href: "/docs/advanced/performance" },
      { title: "CLI", href: "/docs/advanced/cli" },
    ],
  },
  {
    title: "Development",
    items: [
      { title: "Architecture", href: "/docs/development/architecture" },
      {
        title: "Building From Source",
        href: "/docs/development/building-from-source",
      },
      { title: "Contributing", href: "/docs/development/contributing" },
    ],
  },
  {
    title: "Changelog",
    href: "/changelog",
  },
];
