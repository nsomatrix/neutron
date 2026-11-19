# MIDP 2.0 (JSR-118) Specification Summary

## 1. Scope
Mobile Information Device Profile 2.0 provides application lifecycle, graphical user interface (LCDUI), game engine components, record storage (RMS), and networking across cellular mobile devices.

## 2. Mandatory Packages & Subsystems
1. **Application Lifecycle (`javax.microedition.midlet`)**:
   - `MIDlet`: `startApp()`, `pauseApp()`, `destroyApp(boolean unconditional)`, `notifyDestroyed()`, `notifyPaused()`, `resumeRequest()`, `getAppProperty(String key)`, `platformRequest(String URL)`, `checkPermission(String permission)`.
   - `MIDletStateChangeException`.
2. **User Interface (`javax.microedition.lcdui`)**:
   - `Display`: `getDisplay(MIDlet)`, `setCurrent(Displayable)`, `getCurrent()`, `callSerially(Runnable)`, `vibrate(int duration)`, `flashBacklight(int duration)`, `numColors()`, `isColor()`, `getColor(int)`.
   - `Displayable`: `setTitle(String)`, `getTitle()`, `setTicker(Ticker)`, `addCommand(Command)`, `removeCommand(Command)`, `setCommandListener(CommandListener)`, `getWidth()`, `getHeight()`, `isShown()`.
   - `Canvas`: `paint(Graphics g)`, `repaint()`, `serviceRepaints()`, `setFullScreenMode(boolean)`, `isDoubleBuffered()`, `hasPointerEvents()`, `hasPointerMotionEvents()`, `hasRepeatEvents()`, `getKeyCode(int gameAction)`, `getGameAction(int keyCode)`, `getKeyName(int keyCode)`, `keyPressed`, `keyReleased`, `keyRepeated`, `pointerPressed`, `pointerReleased`, `pointerDragged`, `showNotify`, `hideNotify`, `sizeChanged`.
   - `Graphics`: `setColor(int RGB)`, `setGrayScale(int value)`, `setClip(int x, int y, int w, int h)`, `clipRect`, `translate`, `drawLine`, `drawRect`, `fillRect`, `drawRoundRect`, `fillRoundRect`, `drawArc`, `fillArc`, `drawString`, `drawSubstring`, `drawChar`, `drawChars`, `drawImage`, `drawRegion`, `drawRGB`, `getClipX`, `getClipY`, `getClipWidth`, `getClipHeight`, `getColor`, `getFont`, `setFont`.
   - `Image`: `createImage(String name)`, `createImage(byte[] data, int off, int len)`, `createImage(int width, int height)`, `createImage(Image source)`, `createImage(Image image, int x, int y, int width, int height, int transform)`, `createRGBImage(int[] rgb, int width, int height, boolean processAlpha)`, `getGraphics()`, `getWidth()`, `getHeight()`, `isMutable()`, `getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)`.
   - `Font`: `getFont(int face, int style, int size)`, `getDefaultFont()`, `stringWidth(String)`, `charWidth(char)`, `substringWidth(...)`, `charsWidth(...)`, `getHeight()`, `getBaselinePosition()`, `getAscent()`, `getDescent()`.
   - High-level form items: `Form`, `List`, `TextBox`, `Alert`, `TextField`, `DateField`, `Gauge`, `StringItem`, `ImageItem`, `ChoiceGroup`, `Spacer`, `CustomItem`, `Ticker`.
3. **Game API (`javax.microedition.lcdui.game`)**:
   - `GameCanvas`: Extends `Canvas`, off-screen graphics buffer via `getGraphics()`, `flushGraphics()`, non-blocking key polling via `getKeyStates()`.
   - `Sprite`: Animated multi-frame sprite, 8 transforms (`TRANS_NONE`, `TRANS_ROT90`, `TRANS_ROT180`, `TRANS_ROT270`, `TRANS_MIRROR`, `TRANS_MIRROR_ROT90`, `TRANS_MIRROR_ROT180`, `TRANS_MIRROR_ROT270`), frame sequences, collision detection with sprites, tiled layers, and bounding boxes (`collidesWith(Sprite, boolean pixelLevel)`).
   - `TiledLayer`: Fast background tile map grid, cell dimensions, static tiles, animated dynamic tiles (`createAnimatedTile`, `setAnimatedTile`).
   - `LayerManager`: Viewport management, layer addition/removal, camera translation (`setViewWindow`), layer rendering in z-order (`paint(Graphics, int x, int y)`).
4. **Persistent Storage (`javax.microedition.rms`)**:
   - `RecordStore`: `openRecordStore`, `closeRecordStore`, `deleteRecordStore`, `listRecordStores`, `addRecord`, `setRecord`, `getRecord`, `deleteRecord`, `getRecordSize`, `getNextRecordID`, `getNumRecords`, `getSize`, `getSizeAvailable`, `getVersion`, `getLastModified`.
   - `RecordEnumeration`: `hasNextElement`, `nextRecord`, `nextRecordId`, `hasPreviousElement`, `previousRecord`, `previousRecordId`, `rebuild`, `reset`, `destroy`.
   - `RecordFilter`, `RecordComparator`, `RecordListener`.
5. **Generic Connection Framework Extensions (`javax.microedition.io`)**:
   - `HttpConnection`, `HttpsConnection`, `SocketConnection`, `ServerSocketConnection`, `DatagramConnection`, `Datagram`, `UDPDatagramConnection`, `SecureConnection`, `PushRegistry`.
6. **Media Subset (`javax.microedition.media`)**:
   - `Manager.playTone`, `Manager.createPlayer`, `Player`, `PlayerListener`, `VolumeControl`, `ToneControl`.

