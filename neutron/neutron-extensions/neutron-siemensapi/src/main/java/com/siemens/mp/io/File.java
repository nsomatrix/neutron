/*
 *  Siemens API for Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
package com.siemens.mp.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import org.neutron.MIDletBridge;

/**
 *
 * @author  markus
 * @version
 */
public class File {
    static Vector files = new Vector();
    static Hashtable data = new Hashtable();

    static class OpenFileHandle {
        String fileName;
        int seek;

        OpenFileHandle(String fileName) {
            this.fileName = fileName;
            this.seek = 0;
        }
    }
    
    public int close(int fileDescriptor) {
        if (fileDescriptor >= 0 && fileDescriptor < files.size() && files.elementAt(fileDescriptor) != null) {
            files.setElementAt(null, fileDescriptor);
            return 1;
        }
        return -1;
    }
    
    public static int copy(java.lang.String source, java.lang.String dest) {
        return 1;
    }
    
    public static int debugWrite(java.lang.String fileName, java.lang.String infoString) {
        return 1;
    }
    
    public static int delete(java.lang.String fileName) {
        if (fileName != null) {
            data.remove(fileName.intern());
            data.remove(fileName);
        }
        return 1;
    }
    
    public static int exists(java.lang.String fileName) {
        if (fileName == null) return -1;
        if (data.containsKey(fileName.intern()) || data.containsKey(fileName)) {
            return 1;
        }
        MIDlet current = MIDletBridge.getCurrentMIDlet();
        if (current != null) {
            try (InputStream is = current.getClass().getResourceAsStream(fileName)) {
                if (is != null) {
                    return 1;
                }
            } catch (Exception ignored) {
            }
        }
        return -1;
    }
    
    public static boolean isDirectory(java.lang.String pathName) {
        return false;
    }
    
    public int length(int fileDescriptor) {
        if (fileDescriptor < 0 || fileDescriptor >= files.size()) return -1;
        OpenFileHandle handle = (OpenFileHandle) files.elementAt(fileDescriptor);
        if (handle == null) return -1;
        byte[] bytes = (byte[]) data.get(handle.fileName);
        if (bytes == null) return 0;
        return bytes.length;
    }
    
    public static java.lang.String[] list(java.lang.String pathName) {
        return null;
    }
    
    public int open(java.lang.String fileName) {
        if (fileName == null) return -1;
        String key = fileName.intern();
        if (!data.containsKey(key)) {
            byte[] s = new byte[0];
            MIDlet current = MIDletBridge.getCurrentMIDlet();
            if (current != null) {
                try (InputStream is = current.getClass().getResourceAsStream(fileName)) {
                    if (is != null) {
                        try (BufferedInputStream bis = new BufferedInputStream(is)) {
                            int l;
                            byte[] b = new byte[1024];
                            while ((l = bis.read(b)) != -1) {
                                byte[] t = new byte[l + s.length];
                                System.arraycopy(s, 0, t, 0, s.length);
                                System.arraycopy(b, 0, t, s.length, l);
                                s = t;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            data.put(key, s);
        }

        OpenFileHandle handle = new OpenFileHandle(key);
        int fd = -1;
        for (int i = 0; i < files.size(); i++) {
            if (files.elementAt(i) == null) {
                fd = i;
                files.setElementAt(handle, i);
                break;
            }
        }
        if (fd == -1) {
            fd = files.size();
            files.addElement(handle);
        }
        return fd;
    }
    
    public int read(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        if (fileDescriptor < 0 || fileDescriptor >= files.size() || buf == null || offset < 0 || numBytes < 0) {
            return -1;
        }
        OpenFileHandle handle = (OpenFileHandle) files.elementAt(fileDescriptor);
        if (handle == null) {
            return -1;
        }
        byte[] fileBytes = (byte[]) data.get(handle.fileName);
        if (fileBytes == null) {
            return 0;
        }
        if (handle.seek >= fileBytes.length) {
            return 0;
        }
        if (numBytes > (fileBytes.length - handle.seek)) {
            numBytes = fileBytes.length - handle.seek;
        }
        System.arraycopy(fileBytes, handle.seek, buf, offset, numBytes);
        handle.seek += numBytes;
        return numBytes;
    }
    
    public static int rename(java.lang.String source, java.lang.String dest) {
        return 1;
    }
    
    public int seek(int fileDescriptor, int seekpos) {
        if (fileDescriptor < 0 || fileDescriptor >= files.size() || seekpos < 0) {
            return -1;
        }
        OpenFileHandle handle = (OpenFileHandle) files.elementAt(fileDescriptor);
        if (handle == null) {
            return -1;
        }
        handle.seek = seekpos;
        return 1;
    }
    
    public static int spaceAvailable() {
        return 1000000;
    }
    
    public static void truncate(int fileDescriptor, int size) {
    }
    
    public int write(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        if (fileDescriptor < 0 || fileDescriptor >= files.size() || buf == null || offset < 0 || numBytes < 0) {
            return -1;
        }
        OpenFileHandle handle = (OpenFileHandle) files.elementAt(fileDescriptor);
        if (handle == null) {
            return -1;
        }
        byte[] fileBytes = (byte[]) data.get(handle.fileName);
        if (fileBytes == null) {
            fileBytes = new byte[0];
        }
        if ((handle.seek + numBytes) > fileBytes.length) {
            byte[] t = new byte[handle.seek + numBytes];
            System.arraycopy(fileBytes, 0, t, 0, fileBytes.length);
            fileBytes = t;
        }
        System.arraycopy(buf, offset, fileBytes, handle.seek, numBytes);
        handle.seek += numBytes;
        data.put(handle.fileName, fileBytes);
        return 1;
    }
}