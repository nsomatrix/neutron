# JSR-135 Mobile Media API (MMAPI 1.1) Specification Summary

## 1. Scope
JSR-135 defines multimedia audio, tone, and video playback and capture mechanisms across mobile terminals.

## 2. Mandatory Core Classes & Interfaces
- **`javax.microedition.media.Manager`**:
  - `createPlayer(String locator)`: Create player from URI (`capture://audio`, `file://`, `http://`).
  - `createPlayer(InputStream stream, String type)`: Create player from stream and MIME type (`audio/x-wav`, `audio/midi`, `audio/amr`, `audio/basic`).
  - `playTone(int note, int duration, int volume)`: Direct synchronous frequency tone synthesis.
  - `getSupportedContentTypes(String protocol)`.
  - `getSupportedProtocols(String contentType)`.
- **`javax.microedition.media.Player`**:
  - Extends `Controllable`.
  - Lifecycle states: `UNREALIZED` (100) -> `REALIZED` (200) -> `PREFETCHED` (300) -> `STARTED` (400) -> `CLOSED` (0).
  - State methods: `realize()`, `prefetch()`, `start()`, `stop()`, `deallocate()`, `close()`.
  - Media timing: `setMediaTime(long now)`, `getMediaTime()`, `getDuration()`.
  - Looping: `setLoopCount(int count)` (-1 for infinite).
  - Listeners: `addPlayerListener(PlayerListener)`, `removePlayerListener(PlayerListener)`.
  - Events: `STARTED`, `STOPPED`, `END_OF_MEDIA`, `DURATION_UPDATED`, `DEVICE_UNAVAILABLE`, `DEVICE_AVAILABLE`, `VOLUME_CHANGED`, `ERROR`, `CLOSED`.
- **Controls (`javax.microedition.media.control.*`)**:
  - `VolumeControl`: `setLevel(int level)` (0-100), `getLevel()`, `setMute(boolean mute)`, `isMuted()`.
  - `ToneControl`: `setSequence(byte[] sequence)`: Play structured J2ME tone sequences with tempo, volume, notes, and loops.
  - `MIDIControl`: `send(byte[] data, int offset, int length)`, `setChannelVolume`, `getChannelVolume`, `shortMidiEvent`.
  - `StopTimeControl`: `setStopTime(long stopTime)`, `getStopTime()`.
  - `VideoControl`: `initDisplayMode(int mode, Object arg)`, `setDisplaySize`, `setDisplayLocation`, `setVisible`.

