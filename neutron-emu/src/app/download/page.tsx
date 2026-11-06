import { Metadata } from "next";
import DownloadClient from "./download-client";

export const metadata: Metadata = {
  title: "Download Neutron — Modern Java ME Emulator",
  description:
    "Download Neutron v1.4.0 for Windows, macOS, and Linux. Choose between Neutron Express (Full GUI) and Neutron Core (Ultra-compact).",
};

export default function DownloadPage() {
  return <DownloadClient />;
}
