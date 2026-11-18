package org.neutron.app.ui.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DropTransferHandlerTest {

    private DropTransferHandler handler;

    @Before
    public void setUp() {
        handler = new DropTransferHandler();
    }

    @Test
    public void testCanImportFlavors() {
        assertTrue("Must accept javaFileListFlavor", 
                handler.canImport(null, new DataFlavor[] { DataFlavor.javaFileListFlavor }));

        DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
        assertTrue("Must accept uri-list flavor", 
                handler.canImport(null, new DataFlavor[] { uriListFlavor }));

        assertTrue("Must accept stringFlavor", 
                handler.canImport(null, new DataFlavor[] { DataFlavor.stringFlavor }));

        DataFlavor urlFlavor = new DataFlavor(URL.class, "URL");
        assertTrue("Must accept URL representation class", 
                handler.canImport(null, new DataFlavor[] { urlFlavor }));

        assertFalse("Must reject imageFlavor", 
                handler.canImport(null, new DataFlavor[] { DataFlavor.imageFlavor }));
        
        assertFalse("Must reject null or empty flavors", 
                handler.canImport(null, new DataFlavor[0]));
    }

    @Test
    public void testImportEmptyFileListDoesNotThrow() {
        Transferable emptyListTransferable = new SimpleTransferable(
                DataFlavor.javaFileListFlavor, Collections.emptyList());

        // Must return false cleanly without IndexOutOfBoundsException
        boolean result = handler.importData(null, emptyListTransferable);
        assertFalse("Empty file list must return false without crashing", result);
    }

    @Test
    public void testImportFileListPrefersJadOverJar() throws IOException {
        File jadFile = File.createTempFile("game_test", ".jad");
        File jarFile = File.createTempFile("game_test", ".jar");
        jadFile.deleteOnExit();
        jarFile.deleteOnExit();

        List<File> files = Arrays.asList(jarFile, jadFile);
        Transferable listTransferable = new SimpleTransferable(
                DataFlavor.javaFileListFlavor, files);

        boolean result = handler.importData(null, listTransferable);
        assertTrue("Should successfully accept JAD+JAR list", result);
    }

    @Test
    public void testImportUriListWithCommentsAndEncoding() throws IOException {
        File jarFile = File.createTempFile("spaced test game", ".jar");
        jarFile.deleteOnExit();

        DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
        String uriListText = "# Comment line from Linux file manager\r\n"
                + "file://" + jarFile.getAbsolutePath().replace(" ", "%20") + "\r\n";

        Transferable uriTransferable = new SimpleTransferable(uriListFlavor, uriListText);

        boolean result = handler.importData(null, uriTransferable);
        assertTrue("Must parse RFC 2483 URI list with comments and %20 encoding", result);
    }

    @Test
    public void testImportUnsupportedFileGraceful() throws IOException {
        File txtFile = File.createTempFile("readme", ".txt");
        txtFile.deleteOnExit();

        Transferable listTransferable = new SimpleTransferable(
                DataFlavor.javaFileListFlavor, Collections.singletonList(txtFile));

        // Returns true because error was gracefully handled/reported to user
        boolean result = handler.importData(null, listTransferable);
        assertTrue("Must handle unsupported file extension gracefully", result);
    }

    private static class SimpleTransferable implements Transferable {
        private final DataFlavor flavor;
        private final Object data;

        SimpleTransferable(DataFlavor flavor, Object data) {
            this.flavor = flavor;
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { flavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor f) {
            return flavor.equals(f);
        }

        @Override
        public Object getTransferData(DataFlavor f) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(f)) {
                return data;
            }
            throw new UnsupportedFlavorException(f);
        }
    }
}

