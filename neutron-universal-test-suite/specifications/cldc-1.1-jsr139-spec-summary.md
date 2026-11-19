# CLDC 1.1 (JSR-139) Specification Summary

## 1. Scope and Target
CLDC 1.1 defines the base Java Virtual Machine and standard library environment for memory-constrained, battery-operated mobile devices with 512 KB or more of combined memory.

## 2. Java Virtual Machine Differences from Java SE
- **Floating-point**: Implemented according to IEEE 754 standard for 32-bit `float` and 64-bit `double` types and math operations.
- **Bytecode Verification**: Split verification using class pre-verification attributes (`StackMap`).
- **Object Finalization**: Not supported. `Object.finalize()` is absent.
- **Reflection**: `java.lang.reflect` is not supported.
- **User Classloaders**: Not supported. Classloading is managed exclusively by the system.
- **Weak References**: `java.lang.ref.WeakReference` supported.
- **Thread Management**: Basic thread creation, synchronization, monitors (`wait`, `notify`, `notifyAll`), `Thread.sleep`, `Thread.yield`, `Thread.interrupt`. No thread groups or daemon threads.

## 3. Mandatory Classes & Interfaces
- `java.lang`: `Object`, `Class`, `String`, `StringBuffer`, `Throwable`, `Exception`, `Error`, `Runtime`, `System`, `Thread`, `Runnable`, `Math`, `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Boolean`, `Character`, `Comparable`, `ref.WeakReference`.
- `java.io`: `InputStream`, `OutputStream`, `ByteArrayInputStream`, `ByteArrayOutputStream`, `DataInput`, `DataOutput`, `DataInputStream`, `DataOutputStream`, `Reader`, `Writer`, `InputStreamReader`, `OutputStreamWriter`, `PrintStream`, `IOException`, `EOFException`, `InterruptedIOException`, `UnsupportedEncodingException`, `UTFDataFormatException`.
- `java.util`: `Vector`, `Hashtable`, `Enumeration`, `Stack`, `Date`, `Calendar`, `TimeZone`, `Random`, `Timer`, `TimerTask`.
- `javax.microedition.io`: `Connector`, `Connection`, `InputConnection`, `OutputConnection`, `StreamConnection`, `ContentConnection`, `StreamConnectionNotifier`.

