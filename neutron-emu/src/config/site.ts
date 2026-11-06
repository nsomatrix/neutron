export const siteConfig = {
  name: "Neutron",
  title: "Neutron — The Modern Java ME Emulator",
  description:
    "Fast, isolated, and lightweight Java ME emulator with MascotCapsule 3D, M3G, and xBRZ scaling. Open source and cross-platform.",
  url: "https://neutron-emu.vercel.app",
  ogImage: "/og-image.png?v=2",
  creator: "Manish K",
  github: "https://github.com/nsomatrix/neutron",
  download: "https://github.com/nsomatrix/neutron/releases",

  nav: {
    main: [
      { title: "Documentation", href: "/docs" },
      { title: "Download", href: "/download" },
      { title: "Changelog", href: "/changelog" },
    ],
    mobile: [
      { title: "Home", href: "/" },
      { title: "Documentation", href: "/docs" },
      { title: "Download", href: "/download" },
      { title: "Changelog", href: "/changelog" },
    ],
  },

  footer: {
    product: [
      { title: "Features", href: "/docs/user-guide/features" },
      { title: "Download", href: "/download" },
      { title: "Changelog", href: "/changelog" },
      { title: "Roadmap", href: "/docs/development/contributing" },
    ],
    resources: [
      { title: "Documentation", href: "/docs" },
      { title: "Quick Start", href: "/docs/getting-started/quick-start" },
      { title: "Build from Source", href: "/docs/development/building-from-source" },
    ],
    community: [
      { title: "GitHub", href: "https://github.com/nsomatrix/neutron" },
      {
        title: "Bug Reports",
        href: "https://github.com/nsomatrix/neutron/issues",
      },
    ],
  },

  announcement: {
    enabled: true,
    message: "Neutron v1.4.0 is live with JSR-184 M3G, Micro3D, and xBRZ HD filters!",
    link: "/changelog",
    linkText: "View v1.4.0 Release →",
  },

  features: [
    {
      title: "Blazing Fast Execution",
      description:
        "Direct JIT execution on modern Java Virtual Machines delivers native speed for Java ME applications.",
      icon: "Zap",
    },
    {
      title: "Pixel-Perfect & xBRZ HD",
      description:
        "Accurate MIDP 2.0 / CLDC 1.1 LCDUI with xBRZ (up to 6x), Scale2x, CRT scanlines, and authentic LCD grid shaders.",
      icon: "Monitor",
    },
    {
      title: "Cross-Platform Java",
      description:
        "Runs out-of-the-box across Windows, macOS, and Linux with modern FlatLaf themes and system menus.",
      icon: "Globe",
    },
    {
      title: "3D Mobile Graphics",
      description:
        "Full support for MascotCapsule Micro3D v3, JSR-184 (M3G), and Nokia UI DirectGraphics rasterization.",
      icon: "Code",
    },
    {
      title: "Ultra-Lightweight Core",
      description:
        "Neutron Core binary is under 850 KB with fast startup and low RAM usage, packaged with LZMA2/XZ compression.",
      icon: "Feather",
    },
    {
      title: "Dual Licensed Open Source",
      description:
        "Free and open source software under the GNU LGPL v2.1 and Apache License 2.0.",
      icon: "GitHubIcon",
    },
  ],

  stats: [
    { label: "Profile Support", value: "MIDP 2.0" },
    { label: "Configuration", value: "CLDC 1.1" },
    { label: "Java Baseline", value: "JDK 8+ (17+ Rec.)" },
    { label: "Open Source License", value: "LGPL 2.1 / Apache 2.0" },
  ],
} as const;

export type SiteConfig = typeof siteConfig;
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
      { title: "Graphics & Display", href: "/docs/user-guide/graphics" },
      { title: "Audio Subsystem", href: "/docs/user-guide/audio" },
      { title: "Input & Controls", href: "/docs/user-guide/input" },
      { title: "Networking & Proxy", href: "/docs/user-guide/networking" },
      { title: "Filesystem & RMS", href: "/docs/user-guide/filesystem" },
    ],
  },
  {
    title: "Advanced",
    items: [
      { title: "Performance & Optimization", href: "/docs/advanced/performance" },
      { title: "CLI Reference", href: "/docs/advanced/cli" },
    ],
  },
  {
    title: "Development",
    items: [
      { title: "Architecture & Design", href: "/docs/development/architecture" },
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
