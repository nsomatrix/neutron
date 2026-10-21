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
import javax.microedition.lcdui.Image;
import org.neutron.MIDletEntry;

public class LauncherCanvas extends Canvas {

    // Layout Constants
    private static final int HEADER_HEIGHT = 36;
    private static final int LIST_ITEM_HEIGHT = 44;
    private static final int LIST_ITEM_GAP = 8;
    private static final int LIST_PADDING = 12;
    private static final int LAUNCH_BTN_HEIGHT = 38;
    private static final int DOUBLE_TAP_TIMEOUT_MS = 500;

    private final Launcher launcher;
    private int selectedIndex = 0;
    
    // Logo assets
    private Image logo48;
    
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
        loadLogoImages();
    }

    private void loadLogoImages() {
        try {
            logo48 = Image.createImage("/org/neutron/app/launcher/logo_48.png");
        } catch (Throwable t) {
            logo48 = null;
        }
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

    // Helper to blend two RGB colors
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

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // Query active theme colors dynamically from the Display
        Display d = Display.getDisplay(launcher);
        int bgColor = d.getColor(Display.COLOR_BACKGROUND);
        int fgColor = d.getColor(Display.COLOR_FOREGROUND);

        // Decompose background to compute luminance for dark/light mode detection
        int bgR = (bgColor >> 16) & 0xFF;
        int bgG = (bgColor >> 8) & 0xFF;
        int bgB = bgColor & 0xFF;
        double luminance = (0.299 * bgR + 0.587 * bgG + 0.114 * bgB) / 255.0;
        boolean isDark = (luminance < 0.5);

        // Draw solid flat background matching active theme
        g.setColor(bgColor);
        g.fillRect(0, 0, w, h);

        // Draw Header
        int headerHeight = drawHeader(g, w, bgColor, fgColor);

        // Draw Content Area
        Vector entries = Launcher.midletEntries;
        if (entries == null || entries.size() == 0) {
            drawEmptyState(g, w, h, headerHeight, bgColor, fgColor, isDark);
        } else {
            drawMIDletList(g, entries, w, h, headerHeight, bgColor, fgColor, isDark);
        }

        // Draw Launch Button at the bottom
        drawLaunchButton(g, w, h, bgColor, fgColor, isDark);
    }

    private int drawHeader(Graphics g, int w, int bgColor, int fgColor) {
        // Clean, minimalist centered title
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        g.setColor(fgColor);
        g.drawString("N E U T R O N", w / 2, 10, Graphics.HCENTER | Graphics.TOP);

        return HEADER_HEIGHT;
    }

    private void drawEmptyState(Graphics g, int w, int h, int startY, int bgColor, int fgColor, boolean isDark) {
        int availableH = h - startY - 20;
        
        // Glass Card Container Dimensions
        int cardW = Math.min(w - 40, 360);
        int cardH = Math.min(availableH - 24, 230);
        int cardX = (w - cardW) / 2;
        int cardY = startY + (availableH - cardH) / 2;

        // Subtle theme-matched flat card fill & crisp border
        int cardBgColor = blend(bgColor, fgColor, isDark ? 0.06f : 0.04f);
        int cardBorderColor = blend(bgColor, fgColor, isDark ? 0.16f : 0.14f);

        // Single Flat Card Fill
        g.setColor(cardBgColor);
        g.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        // Crisp Single Border Overlay
        g.setColor(cardBorderColor);
        g.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        // Official Neutron Emulator Brand Emblem
        int iconCenterX = cardX + cardW / 2;
        int iconCenterY = cardY + 48;
        
        if (logo48 != null) {
            // Draw official high-res Neutron atomic emblem
            g.drawImage(logo48, iconCenterX, iconCenterY, Graphics.HCENTER | Graphics.VCENTER);
        } else {
            // Minimalist atomic fallback icon
            g.setColor(blend(bgColor, fgColor, isDark ? 0.20f : 0.16f));
            g.drawArc(iconCenterX - 18, iconCenterY - 18, 36, 36, 0, 360);
            g.setColor(fgColor);
            g.fillArc(iconCenterX - 5, iconCenterY - 5, 10, 10, 0, 360);
        }

        // Typography Hierarchy
        int textY = iconCenterY + 32;

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

        // Dynamic Engine Status Badges / Pills (Bottom of Card)
        Font pillFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        String label1 = "Neutron Engine";
        String label2 = "Ready";
        
        int pill1W = pillFont.stringWidth(label1) + 14;
        int pill2W = pillFont.stringWidth(label2) + 24;
        int pillGap = 12;
        int totalPillWidth = pill1W + pillGap + pill2W;
        
        int startPillX = iconCenterX - totalPillWidth / 2;
        int pillY = cardY + cardH - 32;

        drawStatusPill(g, startPillX, pillY, pill1W, label1, pillFont, bgColor, fgColor, isDark);
        drawStatusPillWithDot(g, startPillX + pill1W + pillGap, pillY, pill2W, label2, pillFont, bgColor, fgColor, isDark);
    }

    private void drawStatusPill(Graphics g, int x, int y, int pillW, String label, Font font, int bgColor, int fgColor, boolean isDark) {
        int pillH = 18;

        g.setColor(blend(bgColor, fgColor, isDark ? 0.10f : 0.06f));
        g.fillRoundRect(x, y, pillW, pillH, 8, 8);
        g.setColor(blend(bgColor, fgColor, isDark ? 0.20f : 0.15f));
        g.drawRoundRect(x, y, pillW, pillH, 8, 8);

        g.setFont(font);
        g.setColor(blend(bgColor, fgColor, 0.65f));
        g.drawString(label, x + 7, y + (pillH - font.getHeight()) / 2 + 1, Graphics.LEFT | Graphics.TOP);
    }

    private void drawStatusPillWithDot(Graphics g, int x, int y, int pillW, String label, Font font, int bgColor, int fgColor, boolean isDark) {
        int pillH = 18;

        g.setColor(blend(bgColor, fgColor, isDark ? 0.10f : 0.06f));
        g.fillRoundRect(x, y, pillW, pillH, 8, 8);
        g.setColor(blend(bgColor, fgColor, isDark ? 0.22f : 0.16f));
        g.drawRoundRect(x, y, pillW, pillH, 8, 8);

        // Neutral monochrome status pip
        g.setColor(blend(bgColor, fgColor, 0.70f));
        g.fillArc(x + 7, y + 5, 7, 7, 0, 360);

        g.setFont(font);
        g.setColor(fgColor);
        g.drawString(label, x + 18, y + (pillH - font.getHeight()) / 2 + 1, Graphics.LEFT | Graphics.TOP);
    }

    private void drawMIDletList(Graphics g, Vector entries, int w, int h, int startY, int bgColor, int fgColor, boolean isDark) {
        int listStartY = startY + 10;
        int itemH = LIST_ITEM_HEIGHT;
        int gap = LIST_ITEM_GAP;
        int padding = LIST_PADDING;
        
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

            // Clean monochrome card colors
            if (isSelected) {
                // Subtle elevated card fill & crisp border
                g.setColor(blend(bgColor, fgColor, isDark ? 0.14f : 0.08f));
                g.fillRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
                g.setColor(blend(bgColor, fgColor, isDark ? 0.38f : 0.28f));
                g.drawRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
            } else {
                // Subtle card background based on foreground contrast
                g.setColor(blend(bgColor, fgColor, isDark ? 0.05f : 0.03f));
                g.fillRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
                g.setColor(blend(bgColor, fgColor, isDark ? 0.12f : 0.08f));
                g.drawRoundRect(padding, itemY, w - padding * 2, itemH, 8, 8);
            }

            // Minimalist Modern Icon
            int iconSize = 26;
            int iconX = padding + 8;
            int iconY = itemY + (itemH - iconSize) / 2;
            
            if (isSelected) {
                g.setColor(blend(bgColor, fgColor, isDark ? 0.25f : 0.18f));
            } else {
                g.setColor(blend(bgColor, fgColor, isDark ? 0.12f : 0.08f));
            }
            g.fillRoundRect(iconX, iconY, iconSize, iconSize, 6, 6);
            
            // Draw minimalist play icon inside card icon
            g.setColor(isSelected ? fgColor : blend(bgColor, fgColor, 0.50f));
            int triX = iconX + 10;
            int triY = iconY + 8;
            g.fillTriangle(triX, triY, triX + 9, triY + 5, triX, triY + 10);

            // Title Text
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            g.setColor(fgColor);
            g.drawString(entry.getName(), iconX + iconSize + 10, itemY + (itemH - 16) / 2, Graphics.LEFT | Graphics.TOP);

            // Arrow Chevron
            int arrowX = w - padding - 16;
            g.setColor(isSelected ? fgColor : blend(bgColor, fgColor, 0.35f));
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
            g.setColor(blend(bgColor, fgColor, 0.35f));
            g.fillRect(scrollbarX, thumbY, 4, thumbH);
        }
    }

    private void drawLaunchButton(Graphics g, int w, int h, int bgColor, int fgColor, boolean isDark) {
        boolean hasMidlets = (Launcher.midletEntries != null && Launcher.midletEntries.size() > 0);
        if (!hasMidlets) {
            return;
        }

        btnW = w - 24;
        btnH = LAUNCH_BTN_HEIGHT;
        btnX = 12;
        btnY = h - btnH - 12;

        // Clean monochrome action button (charcoal graphite in dark mode, crisp black in light mode)
        int btnBg = isDark ? blend(bgColor, fgColor, 0.18f) : blend(bgColor, fgColor, 0.88f);
        int btnBorder = blend(bgColor, fgColor, isDark ? 0.35f : 0.25f);
        int btnText = isDark ? fgColor : bgColor;

        g.setColor(btnBg);
        g.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
        g.setColor(btnBorder);
        g.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
        
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.setColor(btnText);
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

        int listStartY = HEADER_HEIGHT + 10;
        int itemH = LIST_ITEM_HEIGHT;
        int gap = LIST_ITEM_GAP;
        int padding = LIST_PADDING;
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
                
                if (clickedIndex == selectedIndex && clickedIndex == lastSelectedTapIndex && (clickTime - lastTapTime < DOUBLE_TAP_TIMEOUT_MS)) {
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
