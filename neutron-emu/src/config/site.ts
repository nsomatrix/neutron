export const siteConfig = {
  name: "Neutron",
  title: "Neutron — The Modern Java ME Emulator",
  description:
    "Fast, accurate, and lightweight Java ME emulator. Open source and cross-platform.",
  url: "https://neutron-emu.vercel.app",
  ogImage: "/og-image.png",
  creator: "Manish K",
  github: "https://github.com/nsomatrix/neutron",
  download: "https://github.com/nsomatrix/neutron/releases",

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
      { title: "Build", href: "/docs/development/building-from-source" },
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
    enabled: false,
    message: "Neutron v1.0.0 is now available — the modern Java ME emulator.",
    link: "/changelog",
    linkText: "Read more →",
  },

  features: [
    {
      title: "Blazing Fast",
      description:
        "Direct execution on modern Java VMs delivers native performance for Java ME applications.",
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
        "Built-in logging, CLI support, and easy-to-use configuration settings.",
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
        "Free and open source under the Apache license.",
      icon: "GitHubIcon",
    },
  ],

  stats: [
    { label: "Profile Support", value: "MIDP 2.0" },
    { label: "Configuration", value: "CLDC 1.1" },
    { label: "Java Requirement", value: "JDK 8+" },
    { label: "Open Source License", value: "Apache" },
  ],
} as const;

export type SiteConfig = typeof siteConfig;
