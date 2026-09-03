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
        repaint();
    }

    protected void hideNotify() {
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

        // Draw solid flat background matching active theme
        g.setColor(bgColor);
        g.fillRect(0, 0, w, h);

        // Draw Header
        int headerHeight = drawHeader(g, w, fgColor, selectBg);

        // Draw Content Area
        Vector entries = Launcher.midletEntries;
        if (entries == null || entries.size() == 0) {
            drawEmptyState(g, w, h, headerHeight, bgColor, fgColor, selectBg, isDark);
        } else {
            drawMIDletList(g, entries, w, h, headerHeight, bgColor, fgColor, selectBg);
        }

        // Draw Launch Button at the bottom
        drawLaunchButton(g, w, h, bgColor, fgColor, selectBg, selectFg);
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

        return 44;
    }

    private void drawEmptyState(Graphics g, int w, int h, int startY, int bgColor, int fgColor, int selectBg, boolean isDark) {
        int availableH = h - startY - 20;
        
        // Glass Card Container Dimensions
        int cardW = Math.min(w - 40, 360);
        int cardH = Math.min(availableH - 24, 230);
        int cardX = (w - cardW) / 2;
        int cardY = startY + (availableH - cardH) / 2;

        // Layered theme blend colors for depth
        int cardBgColor = blend(bgColor, fgColor, isDark ? 0.06f : 0.04f);
        int cardBorderColor = blend(bgColor, fgColor, isDark ? 0.16f : 0.14f);
        int cardGlowColor = blend(cardBgColor, selectBg, 0.10f);

        // Subtle outer glass shadow / glow
        g.setColor(cardGlowColor);
        g.fillRoundRect(cardX - 2, cardY - 2, cardW + 4, cardH + 4, 16, 16);

        // Card Fill
        g.setColor(cardBgColor);
        g.fillRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Glass Border
        g.setColor(cardBorderColor);
        g.drawRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Vector Gaming Console Icon
        int iconCenterX = cardX + cardW / 2;
        int iconCenterY = cardY + 48;
        
        // Handheld Console Body
        int frameW = 58;
        int frameH = 34;
        int frameX = iconCenterX - frameW / 2;
        int frameY = iconCenterY - frameH / 2;

        // Console Outer Body (theme accent blend)
        g.setColor(blend(bgColor, selectBg, 0.25f));
        g.fillRoundRect(frameX, frameY, frameW, frameH, 10, 10);
        g.setColor(blend(bgColor, selectBg, 0.40f));
        g.drawRoundRect(frameX, frameY, frameW, frameH, 10, 10);

        // Center Screen Bezel
        int screenW = 24;
        int screenH = 18;
        int screenX = iconCenterX - screenW / 2;
        int screenY = iconCenterY - screenH / 2;
        
        g.setColor(blend(bgColor, fgColor, isDark ? 0.08f : 0.95f));
        g.fillRoundRect(screenX, screenY, screenW, screenH, 4, 4);
        g.setColor(blend(bgColor, selectBg, 0.30f));
        g.drawRoundRect(screenX, screenY, screenW, screenH, 4, 4);

        // Play Triangle Icon inside Console Screen
        g.setColor(selectBg);
        int triX = screenX + 9;
        int triY = screenY + 4;
        g.fillTriangle(triX, triY, triX + 8, triY + 5, triX, triY + 10);

        // D-Pad (Left side)
        int dpadX = frameX + 9;
        int dpadY = iconCenterY;
        g.setColor(fgColor);
        g.fillRect(dpadX - 4, dpadY - 1, 9, 3); // Horizontal arm
        g.fillRect(dpadX - 1, dpadY - 4, 3, 9); // Vertical arm

        // Action Buttons (Right side - A/B diamond arrangement)
        int btnX = frameX + frameW - 9;
        int btnY = iconCenterY;
        g.setColor(selectBg);
        g.fillArc(btnX - 2, btnY - 5, 4, 4, 0, 360); // Top button
        g.fillArc(btnX + 1, btnY - 2, 4, 4, 0, 360); // Right button
        g.fillArc(btnX - 5, btnY - 2, 4, 4, 0, 360); // Left button
        g.fillArc(btnX - 2, btnY + 1, 4, 4, 0, 360); // Bottom button

        // Typography Hierarchy
        int textY = iconCenterY + frameH / 2 + 16;

        // Main Drop Header
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.setColor(fgColor);
        g.drawString("Drop J2ME App Here", iconCenterX, textY, Graphics.HCENTER | Graphics.TOP);

        // Subtitle Instructions
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(blend(bgColor, fgColor, 0.60f));
        g.drawString("Drag & Drop JAR/JAD Files to Launch", iconCenterX, textY + 20, Graphics.HCENTER | Graphics.TOP);
        
        g.setColor(blend(bgColor, fgColor, 0.45f));
        g.drawString("Use Run Menu to Browse", iconCenterX, textY + 35, Graphics.HCENTER | Graphics.TOP);

        // Engine Status Badges / Pills (Bottom of Card)
        int pillY = cardY + cardH - 32;
        
        drawStatusPill(g, iconCenterX - 72, pillY, "Neutron Engine", selectBg, bgColor, fgColor, isDark);
        drawStatusPillWithDot(g, iconCenterX + 28, pillY, "Ready", selectBg, bgColor, fgColor, isDark);
    }

    private void drawStatusPill(Graphics g, int x, int y, String label, int accentColor, int bgColor, int fgColor, boolean isDark) {
        Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        g.setFont(font);
        int strW = font.stringWidth(label);
        int pillW = strW + 14;
        int pillH = 18;

        g.setColor(blend(bgColor, fgColor, isDark ? 0.10f : 0.06f));
        g.fillRoundRect(x, y, pillW, pillH, 8, 8);
        g.setColor(blend(bgColor, fgColor, isDark ? 0.20f : 0.15f));
        g.drawRoundRect(x, y, pillW, pillH, 8, 8);

        g.setColor(blend(bgColor, fgColor, 0.65f));
        g.drawString(label, x + 7, y + (pillH - font.getHeight()) / 2 + 1, Graphics.LEFT | Graphics.TOP);
    }

    private void drawStatusPillWithDot(Graphics g, int x, int y, String label, int accentColor, int bgColor, int fgColor, boolean isDark) {
        Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        g.setFont(font);
        int strW = font.stringWidth(label);
        int pillW = strW + 24;
        int pillH = 18;

        g.setColor(blend(bgColor, accentColor, 0.12f));
        g.fillRoundRect(x, y, pillW, pillH, 8, 8);
        g.setColor(blend(bgColor, accentColor, 0.28f));
        g.drawRoundRect(x, y, pillW, pillH, 8, 8);

        // Vibrant status green dot
        g.setColor(0x00E676);
        g.fillArc(x + 7, y + 5, 7, 7, 0, 360);

        g.setColor(fgColor);
        g.drawString(label, x + 18, y + (pillH - font.getHeight()) / 2 + 1, Graphics.LEFT | Graphics.TOP);
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
