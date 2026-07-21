"use client";

import Link from "next/link";
import Image from "next/image";
import { motion } from "framer-motion";
import logoImg from "@/assets/ntn.png";
import { SpotlightCard } from "@/components/ui/spotlight-card";
import {
  ArrowRight,
  Download,
  Zap,
  Monitor,
  Globe,
  Code,
  Feather,
  Terminal,
  ChevronRight,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { siteConfig } from "@/config/site";
import { GitHubIcon } from "@/components/icons";
import { CinematicTerminal } from "@/components/cinematic-terminal";

const iconMap: Record<string, React.ComponentType<{ className?: string }>> = {
  Zap,
  Monitor,
  Globe,
  Code,
  Feather,
  GitHubIcon,
};

const fadeUp = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0 },
};

const stagger = {
  animate: {
    transition: {
      staggerChildren: 0.1,
    },
  },
};

export default function HomePage() {
  return (
    <div className="relative">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        {/* Background gradient */}
        <div className="absolute inset-0 -z-10">
          <div className="absolute inset-0 bg-gradient-to-b from-blue-accent/5 via-transparent to-transparent" />
          <div className="absolute left-1/2 top-0 h-[600px] w-[800px] rounded-full bg-gradient-to-br from-blue-accent/10 via-purple-accent/5 to-transparent blur-3xl animate-float-left" />
          <div className="absolute right-0 top-1/4 h-[400px] w-[400px] rounded-full bg-cyan-accent/5 blur-3xl animate-float-right" />
          {/* Grid pattern */}
          <div
            className="absolute inset-0 opacity-[0.02]"
            style={{
              backgroundImage: `linear-gradient(rgba(255,255,255,.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.1) 1px, transparent 1px)`,
              backgroundSize: "64px 64px",
            }}
          />
        </div>

        <div className="mx-auto max-w-screen-xl px-4 pt-10 pb-20 sm:pt-16 sm:pb-28 lg:pt-20 lg:pb-36">
          <motion.div
            className="mx-auto max-w-3xl text-center"
            initial="initial"
            animate="animate"
            variants={stagger}
          >
            {/* Badge */}
            <motion.div variants={fadeUp} transition={{ duration: 0.5 }}>
              <Link
                href="/changelog"
                className="group inline-flex items-center gap-2 rounded-full border border-border bg-muted/50 px-4 py-1.5 text-sm text-muted-foreground transition-colors hover:border-primary/30 hover:text-foreground"
              >
                <span className="flex h-2 w-2 rounded-full bg-emerald-500 animate-pulse shrink-0" />
                <span>
                  Neutron v1.0 is now available
                </span>
                <ChevronRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-0.5 shrink-0" />
              </Link>
            </motion.div>

            {/* Logo */}
            <motion.div
              variants={fadeUp}
              transition={{ duration: 0.5, delay: 0.05 }}
              className="mt-8 flex justify-center"
            >
              <div className="relative h-16 w-16 overflow-hidden rounded-2xl border border-border bg-card p-2.5 shadow-xl shadow-primary/5">
                <Image
                  src={logoImg}
                  alt="Neutron Logo"
                  width={64}
                  height={64}
                  className="h-full w-full object-contain"
                  priority
                />
              </div>
            </motion.div>

            {/* Headline */}
            <motion.h1
              className="mt-8 text-4xl font-bold tracking-tight sm:text-6xl lg:text-7xl"
              variants={fadeUp}
              transition={{ duration: 0.5, delay: 0.1 }}
            >
              <span className="block text-foreground">Neutron</span>
              <span className="block bg-gradient-to-r from-blue-accent via-cyan-accent to-purple-accent bg-clip-text text-transparent">
                Java ME Emulator
              </span>
            </motion.h1>

            {/* Subtitle */}
            <motion.p
              className="mt-6 text-lg text-muted-foreground sm:text-xl max-w-2xl mx-auto"
              variants={fadeUp}
              transition={{ duration: 0.5, delay: 0.2 }}
            >
              Fast. Isolated. Lightweight.
              <br />
              Run your Java ME applications on any modern platform.
            </motion.p>

            {/* CTA Buttons */}
            <motion.div
              className="mt-10 flex flex-col items-center gap-4 sm:flex-row sm:justify-center"
              variants={fadeUp}
              transition={{ duration: 0.5, delay: 0.3 }}
            >
              <Button asChild variant="outline" size="lg" className="w-full sm:w-auto">
                <Link href="/docs/getting-started/quick-start">
                  Get Started
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
              <Button
                asChild
                size="lg"
                className="w-full sm:w-auto"
              >
                <Link href="/download">
                  <Download className="h-4 w-4" />
                  Download
                </Link>
              </Button>
              <Button
                asChild
                variant="ghost"
                size="lg"
                className="w-full sm:w-auto"
              >
                <a
                  href={siteConfig.github}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <GitHubIcon className="h-4 w-4" />
                  GitHub
                </a>
              </Button>
            </motion.div>
          </motion.div>

          {/* Cinematic Terminal Experience */}
          <motion.div
            className="mt-16 mx-auto w-full max-w-[322px] relative"
            initial="initial"
            animate="animate"
            variants={fadeUp}
            transition={{ duration: 0.5, delay: 0.4 }}
          >
            {/* Workstation Frame Border Glow */}
            <div className="absolute -inset-1.5 rounded-2xl bg-gradient-to-r from-blue-accent/30 via-cyan-accent/25 to-purple-accent/30 opacity-75 blur-md" />
            <div className="absolute -inset-px rounded-xl bg-gradient-to-r from-blue-accent/20 to-purple-accent/20" />
            
            <div className="relative">
              <CinematicTerminal />
            </div>
          </motion.div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="border-y border-border bg-muted/30">
        <div className="mx-auto max-w-screen-xl px-4 py-12">
          <motion.div
            className="grid grid-cols-2 gap-4 sm:gap-8 md:grid-cols-4"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            {siteConfig.stats.map((stat) => (
              <motion.div
                key={stat.label}
                className="text-center"
                variants={fadeUp}
                transition={{ duration: 0.5 }}
              >
                <p className="text-3xl font-bold text-foreground sm:text-4xl">
                  {stat.value}
                </p>
                <p className="mt-1 text-sm text-muted-foreground">
                  {stat.label}
                </p>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 sm:py-32">
        <div className="mx-auto max-w-screen-xl px-4">
          <motion.div
            className="mx-auto max-w-2xl text-center mb-16"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            <motion.h2
              className="text-3xl font-bold tracking-tight sm:text-4xl"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Everything you need to run{" "}
              <span className="bg-gradient-to-r from-blue-accent to-cyan-accent bg-clip-text text-transparent">
                Java ME
              </span>
            </motion.h2>
            <motion.p
              className="mt-4 text-lg text-muted-foreground"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Neutron provides a complete Java ME runtime environment with
              modern tooling and developer-friendly features.
            </motion.p>
          </motion.div>

          <motion.div
            className="grid gap-6 md:grid-cols-2 lg:grid-cols-3"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            {siteConfig.features.map((feature) => {
              const Icon = iconMap[feature.icon] || Zap;
              return (
                <motion.div
                  key={feature.title}
                  variants={fadeUp}
                  transition={{ duration: 0.5 }}
                >
                  <SpotlightCard className="h-full">
                    <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                      <Icon className="h-5 w-5 text-primary" />
                    </div>
                    <h3 className="text-base font-semibold text-foreground">
                      {feature.title}
                    </h3>
                    <p className="mt-2 text-sm text-muted-foreground leading-relaxed">
                      {feature.description}
                    </p>
                  </SpotlightCard>
                </motion.div>
              );
            })}
          </motion.div>
        </div>
      </section>

      {/* Documentation Preview */}
      <section className="border-t border-border bg-muted/20 py-24 sm:py-32">
        <div className="mx-auto max-w-screen-xl px-4">
          <motion.div
            className="mx-auto max-w-2xl text-center mb-16"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            <motion.h2
              className="text-3xl font-bold tracking-tight sm:text-4xl"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Comprehensive{" "}
              <span className="bg-gradient-to-r from-purple-accent to-blue-accent bg-clip-text text-transparent">
                documentation
              </span>
            </motion.h2>
            <motion.p
              className="mt-4 text-lg text-muted-foreground"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Get started in minutes with our detailed guides, docs , and
              examples.
            </motion.p>
          </motion.div>

          <motion.div
            className="grid gap-6 md:grid-cols-3"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            {[
              {
                title: "Quick Start",
                description:
                  "Get up and running in under 5 minutes with our step-by-step guide.",
                href: "/docs/getting-started/quick-start",
                icon: Zap,
              },
              {
                title: "Configuration",
                description:
                  "Customize every aspect of Neutron to match your workflow.",
                href: "/docs/getting-started/configuration",
                icon: Code,
              },
              {
                title: "CLI Reference",
                description:
                  "Full command-line interface documentation for power users.",
                href: "/docs/advanced/cli",
                icon: Terminal,
              },
            ].map((item) => (
              <motion.div key={item.title} variants={fadeUp} transition={{ duration: 0.5 }}>
                <Link href={item.href} className="block h-full group">
                  <SpotlightCard className="h-full flex flex-col justify-between">
                    <div>
                      <item.icon className="h-5 w-5 text-primary mb-4" />
                      <h3 className="text-base font-semibold text-foreground group-hover:text-primary transition-colors">
                        {item.title}
                      </h3>
                      <p className="mt-2 text-sm text-muted-foreground leading-relaxed">
                        {item.description}
                      </p>
                    </div>
                    <div className="mt-4 flex items-center gap-1 text-sm font-medium text-primary">
                      Learn more
                      <ArrowRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-0.5" />
                    </div>
                  </SpotlightCard>
                </Link>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24 sm:py-32">
        <div className="mx-auto max-w-screen-xl px-4">
          <motion.div
            className="mx-auto max-w-2xl text-center"
            initial="initial"
            whileInView="animate"
            viewport={{ once: true, margin: "-100px" }}
            variants={stagger}
          >
            <motion.h2
              className="text-3xl font-bold tracking-tight sm:text-4xl"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Ready to get started?
            </motion.h2>
            <motion.p
              className="mt-4 text-lg text-muted-foreground"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              Download Neutron and start running your Java ME applications
              today.
            </motion.p>
            <motion.div
              className="mt-8 flex flex-col items-center gap-4 sm:flex-row sm:justify-center"
              variants={fadeUp}
              transition={{ duration: 0.5 }}
            >
              <Button asChild size="lg">
                <Link href="/docs/getting-started/quick-start">
                  Read the docs
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
              <Button asChild variant="outline" size="lg">
                <Link href="/download">
                  <Download className="h-4 w-4" />
                  Download Neutron
                </Link>
              </Button>
            </motion.div>
          </motion.div>
        </div>
      </section>
    </div>
  );
}
