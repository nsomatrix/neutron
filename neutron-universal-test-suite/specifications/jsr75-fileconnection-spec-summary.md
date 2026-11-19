# JSR-75 FileConnection Specification Summary

## 1. Scope
JSR-75 defines standard access to mobile file systems, removable flash memory cards (MMC, SD), internal memory, and directory hierarchies.

## 2. Package `javax.microedition.io.file`
- **`FileConnection`** (extends `javax.microedition.io.StreamConnection`):
  - URL Format: `file://localhost/root/path/file.txt` or `file:///root/path/file.txt`.
  - Modes: `Connector.READ` or `Connector.READ_WRITE`.
  - File/Directory Inspection: `exists()`, `isDirectory()`, `canRead()`, `canWrite()`, `isHidden()`, `isReadable()`, `isWritable()`, `fileSize()`, `directorySize(boolean includeSubDirs)`, `lastModified()`.
  - File Creation/Deletion: `create()`, `mkdir()`, `delete()`, `rename(String newName)`, `truncate(long byteOffset)`.
  - Directory Listing: `list()`, `list(String filter, boolean hiddenFiles)`.
  - Stream I/O: `openInputStream()`, `openOutputStream()`, `openOutputStream(long byteOffset)`, `openDataInputStream()`, `openDataOutputStream()`.
  - Storage Stats: `totalSize()`, `availableSize()`, `usedSize()`.
- **`FileSystemRegistry`**:
  - `listRoots()`: Returns `Enumeration` of available mounted virtual roots (e.g., `root/`, `memory_card/`, `c:/`).
  - `addFileSystemListener(FileSystemListener listener)`: Removable card insertion/removal events.
  - `removeFileSystemListener(FileSystemListener listener)`.

