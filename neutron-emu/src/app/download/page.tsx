import type { Metadata } from "next";
import DownloadPageClient from "./download-client";

export const metadata: Metadata = {
  title: "Download",
  description:
    "Download Neutron, the modern cross-platform Java ME emulator for Windows, macOS, and Linux.",
};

export default function DownloadPage() {
  return <DownloadPageClient />;
}

