/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.microemu.app.Common;
import org.microemu.app.ui.Message;
import org.microemu.app.util.IOUtils;
import org.microemu.log.Logger;

public class DropTransferHandler
extends TransferHandler {
    private static final long serialVersionUID = 1L;
    private static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
    private static boolean debugImport = false;
    static /* synthetic */ Class class$java$net$URL;

    public int getSourceActions(JComponent c) {
        return 1;
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; ++i) {
            Class<?> representationclass = transferFlavors[i].getRepresentationClass();
            if (representationclass != null && (class$java$net$URL == null ? DropTransferHandler.class$("java.net.URL") : class$java$net$URL).isAssignableFrom(representationclass)) {
                if (debugImport) {
                    Logger.debug("acepted ", transferFlavors[i]);
                }
                return true;
            }
            if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
                if (debugImport) {
                    Logger.debug("acepted ", transferFlavors[i]);
                }
                return true;
            }
            if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
                if (debugImport) {
                    Logger.debug("acepted ", transferFlavors[i]);
                }
                return true;
            }
            if (uriListFlavor.equals(transferFlavors[i])) {
                if (debugImport) {
                    Logger.debug("acepted ", transferFlavors[i]);
                }
                return true;
            }
            if (!debugImport) continue;
            Logger.debug(i + " unknown import ", transferFlavors[i]);
        }
        if (debugImport) {
            Logger.debug("import rejected");
        }
        return false;
    }

    public boolean importData(JComponent comp, Transferable t) {
        int i;
        DataFlavor[] transferFlavors = t.getTransferDataFlavors();
        for (i = 0; i < transferFlavors.length; ++i) {
            String path;
            Object data;
            if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
                Logger.debug("importing", transferFlavors[i]);
                try {
                    List fileList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                    if (fileList.get(0) instanceof File) {
                        File f = (File)fileList.get(0);
                        if (Common.isMIDletUrlExtension(f.getName())) {
                            Common.openMIDletUrlSafe(IOUtils.getCanonicalFileURL(f));
                        } else {
                            Message.warn("Unable to open " + f.getAbsolutePath() + ", Only JAD files are acepted");
                        }
                    } else {
                        Logger.debug("Unknown object in list ", fileList.get(0));
                    }
                }
                catch (UnsupportedFlavorException e) {
                    Logger.debug(e);
                }
                catch (IOException e) {
                    Logger.debug(e);
                }
                return true;
            }
            if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
                try {
                    data = t.getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException e) {
                    continue;
                }
                catch (IOException e) {
                    continue;
                }
                if (data instanceof String) {
                    Logger.debug("importing", transferFlavors[i]);
                    path = this.getPathString((String)data);
                    if (Common.isMIDletUrlExtension(path)) {
                        Common.openMIDletUrlSafe(path);
                    } else {
                        Message.warn("Unable to open " + path + ", Only JAD files are acepted");
                    }
                    return true;
                }
            }
            if (uriListFlavor.equals(transferFlavors[i])) {
                try {
                    data = t.getTransferData(uriListFlavor);
                }
                catch (UnsupportedFlavorException e) {
                    continue;
                }
                catch (IOException e) {
                    continue;
                }
                if (data instanceof String) {
                    Logger.debug("importing", transferFlavors[i]);
                    path = this.getPathString((String)data);
                    if (Common.isMIDletUrlExtension(path)) {
                        Common.openMIDletUrlSafe(path);
                    } else {
                        Message.warn("Unable to open " + path + ", Only JAD files are acepted");
                    }
                    return true;
                }
            }
            if (!debugImport) continue;
            Logger.debug(i + " unknown importData ", transferFlavors[i]);
        }
        for (i = 0; i < transferFlavors.length; ++i) {
            Class<?> representationclass = transferFlavors[i].getRepresentationClass();
            if (representationclass == null || !(class$java$net$URL == null ? DropTransferHandler.class$("java.net.URL") : class$java$net$URL).isAssignableFrom(representationclass)) continue;
            Logger.debug("importing", transferFlavors[i]);
            try {
                URL jadUrl = (URL)t.getTransferData(transferFlavors[i]);
                String urlString = jadUrl.toExternalForm();
                if (Common.isMIDletUrlExtension(urlString)) {
                    Common.openMIDletUrlSafe(urlString);
                } else {
                    Message.warn("Unable to open " + urlString + ", Only JAD url are acepted");
                }
            }
            catch (UnsupportedFlavorException e) {
                Logger.debug(e);
            }
            catch (IOException e) {
                Logger.debug(e);
            }
            return true;
        }
        return false;
    }

    private String getPathString(String path) {
        if (path == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(path.trim(), "\n\r");
        if (st.hasMoreTokens()) {
            return st.nextToken();
        }
        return path;
    }
}

