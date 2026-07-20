import { DocsSidebar } from "@/components/docs-sidebar";
import { MobileSidebar } from "@/components/mobile-sidebar";
import { type ReactNode } from "react";

export default function DocsLayout({ children }: { children: ReactNode }) {
  return (
    <div className="mx-auto max-w-screen-2xl">
      <div className="flex">
        {/* Desktop sidebar */}
        <div className="hidden w-64 shrink-0 lg:block">
          <div className="sticky top-14 h-[calc(100vh-3.5rem)] overflow-y-auto border-r border-border px-4 py-8">
            <DocsSidebar />
          </div>
        </div>

        {/* Main content */}
        <div className="min-w-0 flex-1">
          {/* Mobile sidebar trigger */}
          <div className="sticky top-14 z-30 flex items-center gap-4 border-b border-border bg-background/80 backdrop-blur-sm px-4 py-3 lg:hidden">
            <MobileSidebar />
          </div>

          {children}
        </div>
      </div>
    </div>
  );
}
