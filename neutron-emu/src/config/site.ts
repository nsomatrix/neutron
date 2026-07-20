export const siteConfig = {
  name: "Neutron",
  title: "Neutron — The Modern Java ME Emulator",
  description:
    "Fast, accurate, and lightweight Java ME emulator. Open source and cross-platform.",
  url: "https://neutron-emu.vercel.app",
  ogImage: "/og-image.png",
  creator: "Neutron Team",
  github: "https://github.com/nicokosi/neutron",
  download: "https://github.com/nicokosi/neutron/releases",
  twitter: "@neutron_emu",

  nav: {
    main: [
      { title: "Documentation", href: "/docs" },
      { title: "Download", href: "/download" },
      { title: "Blog", href: "/blog" },
      { title: "Changelog", href: "/changelog" },
    ],
    mobile: [
      { title: "Home", href: "/" },
      { title: "Documentation", href: "/docs" },
      { title: "Download", href: "/download" },
      { title: "Blog", href: "/blog" },
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
    ],
    community: [
      { title: "GitHub", href: "https://github.com/nicokosi/neutron" },
      {
        title: "Contributing",
        href: "/docs/development/contributing",
      },
      {
        title: "Bug Reports",
        href: "https://github.com/nicokosi/neutron/issues",
      },
    ],
    legal: [
      { title: "License", href: "/license" },
    ],
  },

  announcement: {
    enabled: false,
    message: "Neutron v2.0 is here — rebuilt from the ground up.",
    link: "/changelog",
    linkText: "Read more →",
  },

  features: [
    {
      title: "Blazing Fast",
      description:
        "Optimized JIT compilation delivers near-native performance for Java ME applications.",
      icon: "Zap",
    },
    {
      title: "Pixel-Perfect",
      description:
        "Accurate MIDP 2.0 and CLDC 1.1 implementation with full graphics and audio support.",
      icon: "Monitor",
    },
    {
      title: "Cross Platform",
      description:
        "Runs on Windows, macOS, and Linux. Built with Java for maximum compatibility.",
      icon: "Globe",
    },
    {
      title: "Developer Friendly",
      description:
        "Built-in debugging tools, CLI support, and plugin architecture for extensibility.",
      icon: "Code",
    },
    {
      title: "Lightweight",
      description:
        "Minimal resource footprint. Starts in milliseconds with under 50MB memory usage.",
      icon: "Feather",
    },
    {
      title: "Open Source",
      description:
        "Free and open source under the LGPL license. Community-driven development.",
      icon: "GitHubIcon",
    },
  ],

  stats: [
    { label: "GitHub Stars", value: "2.4K+" },
    { label: "Downloads", value: "50K+" },
    { label: "MIDlets Tested", value: "1,200+" },
    { label: "Contributors", value: "40+" },
  ],
} as const;

export type SiteConfig = typeof siteConfig;
