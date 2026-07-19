/*
 *  Neutron Launcher Canvas
 *  A modernized, responsive, theme-aware homescreen for the Neutron J2ME emulator.
 */

package org.neutron.app.launcher;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import org.neutron.MIDletEntry;

public class LauncherCanvas extends Canvas {

    private final Launcher launcher;
    private int selectedIndex = 0;
    
    // Scroll offset for lists larger than screen height
    private int scrollOffset = 0;
    
    // Bottom button bounds for click detection
    private int btnX, btnY, btnW, btnH;
    
    // Track double tap / click to launch
    private int lastSelectedTapIndex = -1;
    private long lastTapTime = 0;

    // Animation variables
    private Thread animThread;
    private boolean animating = false;
    private double animFrame = 0;

    public LauncherCanvas(Launcher launcher) {
        this.launcher = launcher;
        setFullScreenMode(true);
    }

    public MIDletEntry getSelectedMidletEntry() {
        Vector entries = Launcher.midletEntries;
        if (entries != null && selectedIndex >= 0 && selectedIndex < entries.size()) {
            return (MIDletEntry) entries.elementAt(selectedIndex);
        }
        return null;
    }

    protected void showNotify() {
        // Start the animation thread when the canvas is shown
        animating = true;
        animThread = new Thread(new Runnable() {
            public void run() {
                while (animating) {
                    animFrame += 0.05;
                    repaint();
                    try {
                        Thread.sleep(30); // ~33 FPS for smooth animations
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "NeutronLauncherAnim");
        animThread.start();
    }

    protected void hideNotify() {
        // Stop the animation thread completely to free up all CPU when games are running
        animating = false;
        if (animThread != null) {
            animThread.interrupt();
            animThread = null;
        }
    }

    // Helper to blend two colors
    private int blend(int c1, int c2, float ratio) {
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;
        
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;
        
        int r = (int) (r1 * (1 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1 - ratio) + b2 * ratio);
        
        return (r << 16) | (g << 8) | b;
    }

    // Helper to adjust brightness of a color
    private int adjustBrightness(int color, float factor) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        r = Math.min(255, Math.max(0, (int) (r * factor)));
        g = Math.min(255, Math.max(0, (int) (g * factor)));
        b = Math.min(255, Math.max(0, (int) (b * factor)));
        
        return (r << 16) | (g << 8) | b;
    }

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // Query active theme colors dynamically from the Display (ties into Swing UIManager / FlatLaf)
        Display d = Display.getDisplay(launcher);
        int bgColor = d.getColor(Display.COLOR_BACKGROUND);
        int fgColor = d.getColor(Display.COLOR_FOREGROUND);
        int selectBg = d.getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND);
        int selectFg = d.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND);

        // Decompose background to compute luminance
        int bgR = (bgColor >> 16) & 0xFF;
        int bgG = (bgColor >> 8) & 0xFF;
        int bgB = bgColor & 0xFF;
        double luminance = (0.299 * bgR + 0.587 * bgG + 0.114 * bgB) / 255.0;
        boolean isDark = (luminance < 0.5);

        // Calculate dynamic gradient colors
        int grad1, grad2;
        if (isDark) {
            grad1 = blend(bgColor, selectBg, 0.15f); // subtle tint of highlight color at the top
            grad2 = adjustBrightness(bgColor, 0.75f); // deeper shade at the bottom
        } else {
            grad1 = adjustBrightness(bgColor, 1.05f); // softer light color at the top
            grad2 = adjustBrightness(bgColor, 0.92f); // slightly darker light color at the bottom
        }

        // Draw dynamic theme gradient background
        drawBackgroundGradient(g, w, h, grad1, grad2);

        // Draw elegant theme-matching grid pattern
        int gridColor = isDark ? adjustBrightness(bgColor, 1.15f) : adjustBrightness(bgColor, 0.94f);
        drawBackgroundGrid(g, w, h, gridColor);

        // Draw Header
        int headerHeight = drawHeader(g, w, fgColor, selectBg);

        // Draw Content Area
        Vector entries = Launcher.midletEntries;
        if (entries == null || entries.size() == 0) {
            drawEmptyState(g, w, h, headerHeight, bgColor, fgColor, selectBg);
        } else {
            drawMIDletList(g, entries, w, h, headerHeight, bgColor, fgColor, selectBg);
        }

        // Draw Launch Button at the bottom
        drawLaunchButton(g, w, h, bgColor, fgColor, selectBg, selectFg);
    }

    private void drawBackgroundGradient(Graphics g, int w, int h, int c1, int c2) {
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int step = 2;
        for (int y = 0; y < h; y += step) {
            int r = r1 + (r2 - r1) * y / h;
            int gr = g1 + (g2 - g1) * y / h;
            int b = b1 + (b2 - b1) * y / h;
            g.setColor((r << 16) | (gr << 8) | b);
            g.fillRect(0, y, w, step + 1);
        }
    }

    private void drawBackgroundGrid(Graphics g, int w, int h, int gridColor) {
        g.setColor(gridColor);
        int gap = 30;
        // Vertical lines
        for (int x = gap; x < w; x += gap) {
            g.drawLine(x, 0, x, h);
        }
        // Horizontal lines
        for (int y = gap; y < h; y += gap) {
            g.drawLine(0, y, w, y);
        }
    }

    private int drawHeader(Graphics g, int w, int fgColor, int accentColor) {
        // Logo
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        g.setColor(fgColor);
        g.drawString("N E U T R O N", w / 2, 8, Graphics.HCENTER | Graphics.TOP);

        // Subtitle
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(accentColor);
        g.drawString("E M U L A T O R", w / 2, 28, Graphics.HCENTER | Graphics.TOP);

        // Accent line
        g.setColor(accentColor);
        g.fillRect(15, 42, w - 30, 2);

        return 48;
    }

    private void drawEmptyState(Graphics g, int w, int h, int startY, int bgColor, int fgColor, int selectBg) {
        int cx = w / 2;
        int cy = startY + (h - startY - 50) / 2 - 20;

        // Draw orbital Atomic/Neutron Model (Dynamic & theme-integrated vector structure)
        
        // 1. Draw outer orbit ring
        g.setColor(blend(bgColor, selectBg, 0.2f));
        g.drawArc(cx - 45, cy - 45, 90, 90, 0, 360);
        
        // 2. Draw two intersecting elliptical orbits
        g.setColor(blend(bgColor, selectBg, 0.5f));
        g.drawArc(cx - 50, cy - 22, 100, 44, 0, 360); // horizontal ellipse
        g.drawArc(cx - 22, cy - 50, 44, 100, 0, 360); // vertical ellipse
        
        // 3. Draw glowing nucleus core (pulsating dynamically)
        double pulse = Math.sin(animFrame * 2.0);
        int pulseOffset = (int) (pulse * 3.0);
        
        g.setColor(blend(bgColor, selectBg, 0.3f));
        g.fillArc(cx - 18 - pulseOffset / 2, cy - 18 - pulseOffset / 2, 36 + pulseOffset, 36 + pulseOffset, 0, 360); // outer core glow
        
        g.setColor(selectBg);
        g.fillArc(cx - 11, cy - 11, 22, 22, 0, 360); // nucleus center
        
        g.setColor(fgColor);
        g.fillArc(cx - 4, cy - 4, 8, 8, 0, 360); // bright center nucleus highlight
        
        // 4. Draw orbiting electrons moving dynamically along the elliptical paths
        g.setColor(fgColor);
        
        // Horizontal orbit calculations (rx=50, ry=22)
        int hx1 = cx + (int) (50 * Math.cos(animFrame));
        int hy1 = cy + (int) (22 * Math.sin(animFrame));
        int hx2 = cx + (int) (50 * Math.cos(animFrame + Math.PI));
        int hy2 = cy + (int) (22 * Math.sin(animFrame + Math.PI));
        
        g.fillArc(hx1 - 3, hy1 - 3, 6, 6, 0, 360);
        g.fillArc(hx2 - 3, hy2 - 3, 6, 6, 0, 360);
        
        // Vertical orbit calculations (rx=22, ry=50) (moves in reverse direction)
        int vx1 = cx + (int) (22 * Math.cos(-animFrame));
        int vy1 = cy + (int) (50 * Math.sin(-animFrame));
        int vx2 = cx + (int) (22 * Math.cos(-animFrame + Math.PI));
        int vy2 = cy + (int) (50 * Math.sin(-animFrame + Math.PI));
        
        g.fillArc(vx1 - 3, vy1 - 3, 6, 6, 0, 360);
        g.fillArc(vx2 - 3, vy2 - 3, 6, 6, 0, 360);

        // Title and subtitles
        int textY = cy + 65;
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.setColor(fgColor);
        g.drawString("No MIDlets Loaded", w / 2, textY, Graphics.HCENTER | Graphics.TOP);

        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(blend(bgColor, fgColor, 0.6f));
        g.drawString("Open a JAD/JAR from File menu", w / 2, textY + 18, Graphics.HCENTER | Graphics.TOP);
        g.drawString("Or drag & drop files here", w / 2, textY + 32, Graphics.HCENTER | Graphics.TOP);
    }

    private void drawMIDletList(Graphics g, Vector entries, int w, int h, int startY, int bgColor, int fgColor, int selectBg) {
        int listStartY = startY + 10;
        int itemH = 44;
        int gap = 8;
        int padding = 12;
        
        int availableHeight = h - listStartY - 56;
        int maxVisibleItems = availableHeight / (itemH + gap);

        // Scroll logic
        if (selectedIndex >= scrollOffset + maxVisibleItems) {
            scrollOffset = selectedIndex - maxVisibleItems + 1;
        } else if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        }
        
        if (scrollOffset < 0) scrollOffset = 0;
        int maxOffset = entries.size() - maxVisibleItems;
        if (maxOffset < 0) maxOffset = 0;
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;

        int drawCount = Math.min(entries.size() - scrollOffset, maxVisibleItems);

        for (int i = 0; i < drawCount; i++) {
            int entryIdx = scrollOffset + i;
            MIDletEntry entry = (MIDletEntry) entries.elementAt(entryIdx);
            
            int itemY = listStartY + i * (itemH + gap);
            boolean isSelected = (entryIdx == selectedIndex);

            // Dynamic card colors matching the active theme
            if (isSelected) {
                // Glow selection background
                g.setColor(blend(bgColor, selectBg, 0.20f));
                g.fillRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
                // Active accent border
                g.setColor(selectBg);
                g.drawRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
            } else {
                // Subtle card background based on foreground contrast
                g.setColor(blend(bgColor, fgColor, 0.06f));
                g.fillRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
                g.setColor(blend(bgColor, fgColor, 0.12f));
                g.drawRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
            }

            // Minimalist Modern Icon
            int iconSize = 26;
            int iconX = padding + 8;
            int iconY = itemY + (itemH - iconSize) / 2;
            
            if (isSelected) {
                g.setColor(selectBg);
            } else {
                g.setColor(blend(bgColor, fgColor, 0.22f));
            }
            g.fillRoundRect(iconX, iconY, iconSize, iconSize, 6, 6);
            
            // Draw minimalist play icon inside card icon
            g.setColor(isSelected ? bgColor : fgColor);
            int triX = iconX + 10;
            int triY = iconY + 8;
            g.fillTriangle(triX, triY, triX + 9, triY + 5, triX, triY + 10);

            // Title Text
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            g.setColor(fgColor);
            g.drawString(entry.getName(), iconX + iconSize + 10, itemY + (itemH - 16) / 2, Graphics.LEFT | Graphics.TOP);

            // Arrow Chevron
            int arrowX = w - padding - 16;
            g.setColor(isSelected ? selectBg : blend(bgColor, fgColor, 0.35f));
            g.drawLine(arrowX, itemY + 17, arrowX + 4, itemY + 21);
            g.drawLine(arrowX + 4, itemY + 21, arrowX, itemY + 25);
        }

        // Scrollbar thumb matching theme color
        if (entries.size() > maxVisibleItems) {
            int scrollbarX = w - 6;
            int scrollbarY = listStartY;
            int scrollbarH = availableHeight;
            g.setColor(blend(bgColor, fgColor, 0.08f));
            g.fillRect(scrollbarX, scrollbarY, 4, scrollbarH);

            int thumbH = scrollbarH * maxVisibleItems / entries.size();
            int thumbY = scrollbarY + scrollOffset * (scrollbarH - thumbH) / (entries.size() - maxVisibleItems);
            g.setColor(selectBg);
            g.fillRect(scrollbarX, thumbY, 4, thumbH);
        }
    }

    private void drawLaunchButton(Graphics g, int w, int h, int bgColor, int fgColor, int selectBg, int selectFg) {
        boolean hasMidlets = (Launcher.midletEntries != null && Launcher.midletEntries.size() > 0);
        if (!hasMidlets) {
            return;
        }

        btnW = w - 24;
        btnH = 38;
        btnX = 12;
        btnY = h - btnH - 12;

        // Theme accent background, selection foreground text
        g.setColor(selectBg);
        g.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
        
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.setColor(selectFg);
        g.drawString("LAUNCH APP", w / 2, btnY + (btnH - 16) / 2, Graphics.HCENTER | Graphics.TOP);
    }

    protected void keyPressed(int keyCode) {
        int action = getGameAction(keyCode);
        Vector entries = Launcher.midletEntries;
        
        if (entries != null && entries.size() > 0) {
            if (action == Canvas.UP) {
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = entries.size() - 1;
                }
                repaint();
            } else if (action == Canvas.DOWN) {
                selectedIndex++;
                if (selectedIndex >= entries.size()) {
                    selectedIndex = 0;
                }
                repaint();
            } else if (action == Canvas.FIRE) {
                launchSelected();
            }
        }
    }

    protected void pointerPressed(int x, int y) {
        Vector entries = Launcher.midletEntries;
        if (entries == null || entries.size() == 0) return;

        int listStartY = 48 + 10;
        int itemH = 44;
        int gap = 8;
        int padding = 12;
        int w = getWidth();
        int h = getHeight();

        // 1. Check if launch button is clicked
        if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
            launchSelected();
            return;
        }

        // 2. Check if a card is clicked
        int availableHeight = h - listStartY - 56;
        int maxVisibleItems = availableHeight / (itemH + gap);
        int drawCount = Math.min(entries.size() - scrollOffset, maxVisibleItems);

        for (int i = 0; i < drawCount; i++) {
            int itemY = listStartY + i * (itemH + gap);
            if (x >= padding && x <= w - padding && y >= itemY && y <= itemY + itemH) {
                int clickedIndex = scrollOffset + i;
                long clickTime = System.currentTimeMillis();
                
                if (clickedIndex == selectedIndex && clickedIndex == lastSelectedTapIndex && (clickTime - lastTapTime < 500)) {
                    launchSelected();
                } else {
                    selectedIndex = clickedIndex;
                    lastSelectedTapIndex = clickedIndex;
                    lastTapTime = clickTime;
                    repaint();
                }
                break;
            }
        }
    }

    private void launchSelected() {
        MIDletEntry entry = getSelectedMidletEntry();
        if (entry != null) {
            launcher.common.initMIDlet(true, entry);
        }
    }
}
