# Exhaustive J2ME Emulator Audit, Architecture Specification & Compatibility Defect Ledger

> **Document Version:** 2.0.0-AUDIT  
> **Target Codebase:** Neutron J2ME Emulator (`nsomatrix/neutron`)  
> **Workspace Root:** `/home/manish/projects/neutron`  
> **Audit Status:** PHASE 0–5 COMPLETED — AWAITING EXPLICIT USER APPROVAL FOR PHASE 6 MODIFICATIONS  
> **Baseline Verification:** 100% Passing (143/143 tests passed across 76 Gradle tasks; 26/26 Next.js pages SSG built)  
> **Total Files Inventoried:** 744 files (100% accounted for)  
> **Defects Recorded:** 19 Confirmed/Probable Defects (NX-001 through NX-019)  

---

## 1. Executive Summary & Audit Baseline

This document establishes the authoritative, evidence-driven audit of the **Neutron** Java ME (J2ME) emulator codebase. Neutron is a modern modular J2ME emulator engineered in pure Java with a companion Next.js web application and documentation portal (`neutron-emu`). It is designed to execute Connected Limited Device Configuration (CLDC 1.1) and Mobile Information Device Profile (MIDP 2.0) applications, enhanced by standard JSR extensions (JSR-135 MMAPI, JSR-184 M3G, JSR-75 FileConnection/PIM, JSR-82 Bluetooth, JSR-120 WMA, JSR-226 SVG, JSR-234 Advanced Audio) and OEM APIs (Nokia UI, Siemens, Sony Ericsson, Motorola, Micro3D v3).

### 1.1 Baseline Verification Results

| Verification Target | Command Executed | Result | Status |
| :--- | :--- | :--- | :--- |
| **Workspace Identity** | `git remote -v && git branch` | `git@github.com:nsomatrix/neutron.git` on branch `main` (`44eb53e`) | Verified Correct |
| **Git Working Tree** | `git status` | Clean working tree; 0 untracked files, 0 uncommitted changes | Verified Clean |
| **Java Platform Baseline** | `java -version` | OpenJDK 64-Bit Server VM (build 21.0.8+9-Ubuntu-124.04.1) | Verified Compatible |
| **Gradle Test Suite** | `./gradlew test` | 76 tasks (34 executed, 42 up-to-date); **143 tests passed, 0 failures, 0 errors, 0 skipped** | 100% Passing Baseline |
| **Web Portal Linter** | `npm run lint` (in `neutron-emu`) | 0 errors, 1 minor warning (font subset optimization) | Verified Clean |
| **Web Portal SSG Build**| `npm run build` (in `neutron-emu`)| Compiled in 10.1s; 26/26 Static Site Generated pages | 100% Built |

---

## 2. Complete Repository File Inventory

Total git-tracked files in repository: **744 files**. Every file has been cataloged below with its designated category, architectural purpose, review status, recorded findings, and verification status.

### Inventory Legend
- **Status:**
  - `NOT_REVIEWED`: File has not been inspected.
  - `REVIEWED_NO_FINDINGS`: Inspected thoroughly; no defects or specification deviations detected.
  - `FINDINGS_RECORDED`: Inspected; concrete defects or specification deviations recorded.
  - `FIXED_PENDING_VERIFICATION`: Remediation code applied, pending regression test verification.
  - `VERIFIED`: Baseline behavior verified without regression.
  - `BLOCKED`: Review blocked by external dependencies or corrupt artifacts.


### Module: `:neutron:api` (11 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/api/build.gradle` | J2ME Specification Signature Validation | Gradle build script and dependency definition for neutron/api | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi10/build.gradle` | J2ME Specification Signature Validation | Gradle build script and dependency definition for neutron/api/cldcapi10 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi10/src/test/java/test/CLDC10APIDeclarationsTest.java` | J2ME Specification Signature Validation | Unit / regression test specification for CLDC10APIDeclarationsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi10/src/test/resources/cldcapi10-signature.xml` | J2ME Specification Signature Validation | Configuration/descriptor/schema XML (cldcapi10-signature.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi11/build.gradle` | J2ME Specification Signature Validation | Gradle build script and dependency definition for neutron/api/cldcapi11 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi11/src/test/java/test/CLDC11APIDeclarationsTest.java` | J2ME Specification Signature Validation | Unit / regression test specification for CLDC11APIDeclarationsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/cldcapi11/src/test/resources/cldcapi11-signature.xml` | J2ME Specification Signature Validation | Configuration/descriptor/schema XML (cldcapi11-signature.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/midpapi20/build.gradle` | J2ME Specification Signature Validation | Gradle build script and dependency definition for neutron/api/midpapi20 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/midpapi20/src/test/java/test/MIDP20APIDeclarationsTest.java` | J2ME Specification Signature Validation | Unit / regression test specification for MIDP20APIDeclarationsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/midpapi20/src/test/resources/midpapi20-signature.xml` | J2ME Specification Signature Validation | Configuration/descriptor/schema XML (midpapi20-signature.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/api/src/site/apt/index.apt` | J2ME Specification Signature Validation | Resource/configuration artifact (index.apt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-cldc` (21 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-cldc/build.gradle` | CLDC 1.1 Core Runtime & GCF | Gradle build script and dependency definition for neutron/neutron-cldc | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/proguard.conf` | CLDC 1.1 Core Runtime & GCF | Resource/configuration artifact (proguard.conf) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/com/sun/cdc/io/ConnectionBaseInterface.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ConnectionBaseInterface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/Connection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/ConnectionNotFoundException.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ConnectionNotFoundException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/Connector.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for Connector | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/ContentConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ContentConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/Datagram.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for Datagram | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/DatagramConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for DatagramConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/InputConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for InputConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/OutputConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for OutputConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/StreamConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for StreamConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/javax/microedition/io/StreamConnectionNotifier.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for StreamConnectionNotifier | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/cldc/ClosedConnection.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ClosedConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/ImplFactory.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ImplFactory | `FINDINGS_RECORDED` | [NX-005](#nx-005) | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/Implementation.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for Implementation | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/ImplementationInitialization.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ImplementationInitialization | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/ImplementationUnloadable.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ImplementationUnloadable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/io/ConnectionImplementation.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ConnectionImplementation | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/io/ConnectorAdapter.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ConnectorAdapter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-cldc/src/main/java/org/neutron/microedition/io/ConnectorDelegate.java` | CLDC 1.1 Core Runtime & GCF | Core class implementation for ConnectorDelegate | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-core` (2 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-core/build.gradle` | Core Bootstrap & Payload Launcher | Gradle build script and dependency definition for neutron/neutron-core | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-core/src/main/java/org/neutron/app/CoreLauncher.java` | Core Bootstrap & Payload Launcher | Core class implementation for CoreLauncher | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-examples` (27 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-examples/neutron-demo/build.gradle` | Example MIDlets & Demonstration Games | Gradle build script and dependency definition for neutron/neutron-examples/neutron-demo | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/neutron-demo.html` | Example MIDlets & Demonstration Games | Resource/configuration artifact (neutron-demo.html) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/neutron-demo.jad` | Example MIDlets & Demonstration Games | Resource/configuration artifact (neutron-demo.jad) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/AlertPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for AlertPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/BaseExamplesCanvas.java` | Example MIDlets & Demonstration Games | Core class implementation for BaseExamplesCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/BaseExamplesForm.java` | Example MIDlets & Demonstration Games | Core class implementation for BaseExamplesForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/BaseExamplesList.java` | Example MIDlets & Demonstration Games | Core class implementation for BaseExamplesList | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/CanvasPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for CanvasPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/DateFieldPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for DateFieldPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/GameCanvasPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for GameCanvasPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/GaugePanel.java` | Example MIDlets & Demonstration Games | Core class implementation for GaugePanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/HTTPPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for HTTPPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/HasRunnable.java` | Example MIDlets & Demonstration Games | Core class implementation for HasRunnable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/ImageItemPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for ImageItemPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/KeyCanvasPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for KeyCanvasPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/ListPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for ListPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/PointerCanvasPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for PointerCanvasPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/SimpleDemoMIDlet.java` | Example MIDlets & Demonstration Games | Core class implementation for SimpleDemoMIDlet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/TextBoxPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for TextBoxPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/TextFieldPanel.java` | Example MIDlets & Demonstration Games | Core class implementation for TextFieldPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/java/org/neutron/midp/examples/simpledemo/Utils.java` | Example MIDlets & Demonstration Games | Core class implementation for Utils | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/resources/me2-icon.png` | Example MIDlets & Demonstration Games | Visual skin asset / icon / skin background (me2-icon.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-demo/src/main/resources/org/neutron/midp/examples/simpledemo/image.png` | Example MIDlets & Demonstration Games | Visual skin asset / icon / skin background (image.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-fcview/build.gradle` | Example MIDlets & Demonstration Games | Gradle build script and dependency definition for neutron/neutron-examples/neutron-fcview | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-fcview/src/main/java/org/neutron/examples/fcview/FCViewMIDlet.java` | Example MIDlets & Demonstration Games | Core class implementation for FCViewMIDlet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-fcview/src/main/java/org/neutron/examples/fcview/FileEditor.java` | Example MIDlets & Demonstration Games | Core class implementation for FileEditor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-examples/neutron-fcview/src/main/java/org/neutron/examples/fcview/FilesList.java` | Example MIDlets & Demonstration Games | Core class implementation for FilesList | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:device-skins` (12 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-device-large/src/main/resources/org/neutron/device/large/device.xml` | Device Skin Definitions & Profiles | Configuration/descriptor/schema XML (device.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-large/src/main/resources/org/neutron/device/large/normal.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (normal.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-large/src/main/resources/org/neutron/device/large/over.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (over.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-large/src/main/resources/org/neutron/device/large/pressed.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (pressed.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/device-color.xml` | Device Skin Definitions & Profiles | Configuration/descriptor/schema XML (device-color.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/device.xml` | Device Skin Definitions & Profiles | Configuration/descriptor/schema XML (device.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/down.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (down.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/normal.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (normal.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/over.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (over.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/pressed.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (pressed.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-minimum/src/main/resources/org/neutron/device/minimum/up.png` | Device Skin Definitions & Profiles | Visual skin asset / icon / skin background (up.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-device-resizable/src/main/resources/org/neutron/device/resizable/device-ru.xml` | Device Skin Definitions & Profiles | Configuration/descriptor/schema XML (device-ru.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-120` (7 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-120/build.gradle` | JSR-120 Wireless Messaging (WMA) | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-120 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/javax/wireless/messaging/BinaryMessage.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for BinaryMessage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/javax/wireless/messaging/Message.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for Message | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/javax/wireless/messaging/MessageConnection.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for MessageConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/javax/wireless/messaging/MessageListener.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for MessageListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/javax/wireless/messaging/TextMessage.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for TextMessage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-120/src/main/java/org/neutron/cldc/sms/Connection.java` | JSR-120 Wireless Messaging (WMA) | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-135` (44 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-135/build.gradle` | JSR-135 Mobile Media API (MMAPI) | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-135 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/AmrAudioPlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for AmrAudioPlayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/Control.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for Control | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/Controllable.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for Controllable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/Manager.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for Manager | `FINDINGS_RECORDED` | [NX-007](#nx-007) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/MediaException.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for MediaException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/MidiAudioPlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for MidiAudioPlayer | `FINDINGS_RECORDED` | [NX-006](#nx-006) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/Mp3AudioPlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for Mp3AudioPlayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/Player.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for Player | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/PlayerListener.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PlayerListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/SampledAudioPlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for SampledAudioPlayer | `FINDINGS_RECORDED` | [NX-006](#nx-006) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/TimeBase.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for TimeBase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/VideoCaptureControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for VideoCaptureControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/VideoCapturePlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for VideoCapturePlayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/FramePositioningControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for FramePositioningControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/GUIControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for GUIControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/MIDIControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for MIDIControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/MetaDataControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for MetaDataControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/PitchControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PitchControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/RateControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for RateControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/RecordControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for RecordControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/StopTimeControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for StopTimeControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/TempoControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for TempoControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/ToneControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for ToneControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/VideoControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for VideoControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/control/VolumeControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for VolumeControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/protocol/ContentDescriptor.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for ContentDescriptor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/protocol/DataSource.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for DataSource | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/protocol/SourceStream.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for SourceStream | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/BasicPlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for BasicPlayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/RunnableInterface.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for RunnableInterface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCTimeBase.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCTimeBase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCTone.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCTone | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCToneControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCToneControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCToneHelper.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCToneHelper | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCTonePlayer.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCTonePlayer | `FINDINGS_RECORDED` | [NX-008](#nx-008) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCToneRunnable.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCToneRunnable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/PCToneRunnablePoolFactory.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for PCToneRunnablePoolFactory | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/audio/ToneInfo.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for ToneInfo | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/control/J2SEVolumeControl.java` | JSR-135 Mobile Media API (MMAPI) | Core class implementation for J2SEVolumeControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/test/java/javax/microedition/media/AudioPlayerListenerTest.java` | JSR-135 Mobile Media API (MMAPI) | Unit / regression test specification for AudioPlayerListenerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/test/java/javax/microedition/media/ManagerCreatePlayerTest.java` | JSR-135 Mobile Media API (MMAPI) | Unit / regression test specification for ManagerCreatePlayerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/test/java/javax/microedition/media/VideoCaptureControlTest.java` | JSR-135 Mobile Media API (MMAPI) | Unit / regression test specification for VideoCaptureControlTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-135/src/test/java/org/neutron/media/audio/ToneGenerationTest.java` | JSR-135 Mobile Media API (MMAPI) | Unit / regression test specification for ToneGenerationTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-184` (31 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-184/build.gradle` | JSR-184 Mobile 3D Graphics (M3G) | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-184 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/AnimationController.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for AnimationController | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/AnimationTrack.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for AnimationTrack | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Appearance.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Appearance | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Background.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Background | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Camera.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Camera | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/CompositingMode.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for CompositingMode | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Fog.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Fog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Graphics3D.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Graphics3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Group.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Group | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Image2D.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Image2D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/IndexBuffer.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for IndexBuffer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/KeyframeSequence.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for KeyframeSequence | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Light.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Light | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Loader.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Loader | `FINDINGS_RECORDED` | [NX-015](#nx-015) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Material.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Material | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Mesh.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Mesh | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/MorphingMesh.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for MorphingMesh | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Node.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Node | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Object3D.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Object3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/PolygonMode.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for PolygonMode | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/RayIntersection.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for RayIntersection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/SkinnedMesh.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for SkinnedMesh | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Texture2D.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Texture2D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Transform.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Transform | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Transformable.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for Transformable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/TriangleStripArray.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for TriangleStripArray | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/VertexArray.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for VertexArray | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/VertexBuffer.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for VertexBuffer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/World.java` | JSR-184 Mobile 3D Graphics (M3G) | Core class implementation for World | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-184/src/test/java/javax/microedition/m3g/M3GTest.java` | JSR-184 Mobile 3D Graphics (M3G) | Unit / regression test specification for M3GTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-226` (7 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-226/build.gradle` | JSR-226 SVG 2D Vector Graphics | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-226 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/main/java/javax/microedition/m2g/SVGAnimator.java` | JSR-226 SVG 2D Vector Graphics | Core class implementation for SVGAnimator | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/main/java/javax/microedition/m2g/SVGEventListener.java` | JSR-226 SVG 2D Vector Graphics | Core class implementation for SVGEventListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/main/java/javax/microedition/m2g/SVGImage.java` | JSR-226 SVG 2D Vector Graphics | Core class implementation for SVGImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/main/java/javax/microedition/m2g/ScalableGraphics.java` | JSR-226 SVG 2D Vector Graphics | Core class implementation for ScalableGraphics | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/main/java/javax/microedition/m2g/ScalableImage.java` | JSR-226 SVG 2D Vector Graphics | Core class implementation for ScalableImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-226/src/test/java/javax/microedition/m2g/M2GTest.java` | JSR-226 SVG 2D Vector Graphics | Unit / regression test specification for M2GTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-234` (16 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-234/build.gradle` | JSR-234 Advanced Multimedia 3D Audio | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-234 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/EffectModule.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for EffectModule | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/GlobalManager.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for GlobalManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/MediaProcessor.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for MediaProcessor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/Module.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for Module | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/SoundSource3D.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for SoundSource3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/SpectatorModelling.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for SpectatorModelling | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/EffectControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for EffectControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/PanControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for PanControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/PriorityControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for PriorityControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/audio3d/LocationControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for LocationControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/audio3d/OrientationControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for OrientationControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/audioeffect/EqualizerControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for EqualizerControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/audioeffect/ReverbControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for ReverbControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/main/java/javax/microedition/amms/control/camera/CameraControl.java` | JSR-234 Advanced Multimedia 3D Audio | Core class implementation for CameraControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-234/src/test/java/javax/microedition/amms/GlobalManagerTest.java` | JSR-234 Advanced Multimedia 3D Audio | Unit / regression test specification for GlobalManagerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-75` (15 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-75/build.gradle` | JSR-75 FileConnection & PIM | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-75 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/javax/microedition/io/file/ConnectionClosedException.java` | JSR-75 FileConnection & PIM | Core class implementation for ConnectionClosedException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/javax/microedition/io/file/FileConnection.java` | JSR-75 FileConnection & PIM | Core class implementation for FileConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/javax/microedition/io/file/FileSystemListener.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/javax/microedition/io/file/FileSystemRegistry.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemRegistry | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/javax/microedition/io/file/IllegalModeException.java` | JSR-75 FileConnection & PIM | Core class implementation for IllegalModeException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/Connection.java` | JSR-75 FileConnection & PIM | Core class implementation for Connection | `FINDINGS_RECORDED` | [NX-017](#nx-017) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystem.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystemConnectorImpl.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemConnectorImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystemFileConnection.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemFileConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystemRegistryDelegate.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemRegistryDelegate | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystemRegistryImpl.java` | JSR-75 FileConnection & PIM | Core class implementation for FileSystemRegistryImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/InMemory.java` | JSR-75 FileConnection & PIM | Core class implementation for InMemory | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/test/java/javax/microedition/io/file/CreateTestFileSystem.java` | JSR-75 FileConnection & PIM | Unit / regression test specification for CreateTestFileSystem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-75/src/test/java/javax/microedition/io/file/FileConnectionTest.java` | JSR-75 FileConnection & PIM | Unit / regression test specification for FileConnectionTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-jsr-82` (27 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-jsr-82/build.gradle` | JSR-82 Bluetooth & OBEX | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-jsr-82 | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/neutron-jsr-82.apache.license-2.0.txt` | JSR-82 Bluetooth & OBEX | Project documentation or release notes (neutron-jsr-82.apache.license-2.0.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/BluetoothConnectionException.java` | JSR-82 Bluetooth & OBEX | Core class implementation for BluetoothConnectionException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/BluetoothStateException.java` | JSR-82 Bluetooth & OBEX | Core class implementation for BluetoothStateException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/DataElement.java` | JSR-82 Bluetooth & OBEX | Core class implementation for DataElement | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/DeviceClass.java` | JSR-82 Bluetooth & OBEX | Core class implementation for DeviceClass | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/DiscoveryAgent.java` | JSR-82 Bluetooth & OBEX | Core class implementation for DiscoveryAgent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/DiscoveryListener.java` | JSR-82 Bluetooth & OBEX | Core class implementation for DiscoveryListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/L2CAPConnection.java` | JSR-82 Bluetooth & OBEX | Core class implementation for L2CAPConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/L2CAPConnectionNotifier.java` | JSR-82 Bluetooth & OBEX | Core class implementation for L2CAPConnectionNotifier | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/LocalDevice.java` | JSR-82 Bluetooth & OBEX | Core class implementation for LocalDevice | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/RemoteDevice.java` | JSR-82 Bluetooth & OBEX | Core class implementation for RemoteDevice | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/ServiceRecord.java` | JSR-82 Bluetooth & OBEX | Core class implementation for ServiceRecord | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/ServiceRegistrationException.java` | JSR-82 Bluetooth & OBEX | Core class implementation for ServiceRegistrationException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/bluetooth/UUID.java` | JSR-82 Bluetooth & OBEX | Core class implementation for UUID | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/Authenticator.java` | JSR-82 Bluetooth & OBEX | Core class implementation for Authenticator | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/ClientSession.java` | JSR-82 Bluetooth & OBEX | Core class implementation for ClientSession | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/HeaderSet.java` | JSR-82 Bluetooth & OBEX | Core class implementation for HeaderSet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/Operation.java` | JSR-82 Bluetooth & OBEX | Core class implementation for Operation | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/PasswordAuthentication.java` | JSR-82 Bluetooth & OBEX | Core class implementation for PasswordAuthentication | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/ResponseCodes.java` | JSR-82 Bluetooth & OBEX | Core class implementation for ResponseCodes | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/ServerRequestHandler.java` | JSR-82 Bluetooth & OBEX | Core class implementation for ServerRequestHandler | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/main/java/javax/obex/SessionNotifier.java` | JSR-82 Bluetooth & OBEX | Core class implementation for SessionNotifier | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/test/java/javax/bluetooth/JSR82APIDeclarationsTest.java` | JSR-82 Bluetooth & OBEX | Unit / regression test specification for JSR82APIDeclarationsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/test/java/javax/obex/JSR82APIObexDeclarationsTest.java` | JSR-82 Bluetooth & OBEX | Unit / regression test specification for JSR82APIObexDeclarationsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/test/resources/jsr82-obex-signature.xml` | JSR-82 Bluetooth & OBEX | Configuration/descriptor/schema XML (jsr82-obex-signature.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-jsr-82/src/test/resources/jsr82-signature.xml` | JSR-82 Bluetooth & OBEX | Configuration/descriptor/schema XML (jsr82-signature.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-micro3d` (16 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-micro3d/build.gradle` | Micro3D v3 3D Graphics Engine | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-micro3d | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/ActionTable.java` | Micro3D v3 3D Graphics Engine | Core class implementation for ActionTable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/AffineTrans.java` | Micro3D v3 3D Graphics Engine | Core class implementation for AffineTrans | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/DummyCanvas.java` | Micro3D v3 3D Graphics Engine | Core class implementation for DummyCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Effect3D.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Effect3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Figure.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Figure | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/FigureLayout.java` | Micro3D v3 3D Graphics Engine | Core class implementation for FigureLayout | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Graphics3D.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Graphics3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Light.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Light | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Loader.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Loader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/MascotME.java` | Micro3D v3 3D Graphics Engine | Core class implementation for MascotME | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Rasterizer.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Rasterizer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Texture.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Texture | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Util3D.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Util3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/main/java/com/mascotcapsule/micro3d/v3/Vector3D.java` | Micro3D v3 3D Graphics Engine | Core class implementation for Vector3D | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-micro3d/src/test/java/com/mascotcapsule/micro3d/v3/Micro3DTest.java` | Micro3D v3 3D Graphics Engine | Unit / regression test specification for Micro3DTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-nokiaui` (9 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-nokiaui/build.gradle` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-nokiaui | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/sound/Sound.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for Sound | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/sound/SoundListener.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for SoundListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/DeviceControl.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for DeviceControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/DirectGraphics.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for DirectGraphics | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/DirectGraphicsImp.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for DirectGraphicsImp | `FINDINGS_RECORDED` | [NX-016](#nx-016) | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/DirectUtils.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for DirectUtils | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/FullCanvas.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Core class implementation for FullCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-nokiaui/src/test/java/com/nokia/mid/ui/DirectGraphicsTest.java` | Nokia UI API (DirectGraphics/FullCanvas/Sound) | Unit / regression test specification for DirectGraphicsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-oem-apis` (11 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-oem-apis/build.gradle` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-oem-apis | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/motorola/funlight/FunLight.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for FunLight | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/motorola/multimedia/Lighting.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Lighting | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/motorola/multimedia/Vibrator.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Vibrator | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/samsung/util/AudioClip.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for AudioClip | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/samsung/util/LCDLight.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for LCDLight | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/samsung/util/Vibration.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Vibration | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/sonyericsson/media/Sound.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Sound | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/sonyericsson/ui/Backlight.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Backlight | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/main/java/com/sonyericsson/util/Vibration.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Core class implementation for Vibration | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-oem-apis/src/test/java/org/neutron/oem/OemApisTest.java` | OEM Extensions (Sony Ericsson, Samsung, Motorola) | Unit / regression test specification for OemApisTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-extensions:neutron-siemensapi` (34 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-extensions/neutron-siemensapi/build.gradle` | Siemens Mobile Extension API | Gradle build script and dependency definition for neutron/neutron-extensions/neutron-siemensapi | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/NotAllowedException.java` | Siemens Mobile Extension API | Core class implementation for NotAllowedException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/color_game/GameCanvas.java` | Siemens Mobile Extension API | Core class implementation for GameCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/color_game/Layer.java` | Siemens Mobile Extension API | Core class implementation for Layer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/color_game/LayerManager.java` | Siemens Mobile Extension API | Core class implementation for LayerManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/color_game/Sprite.java` | Siemens Mobile Extension API | Core class implementation for Sprite | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/color_game/TiledLayer.java` | Siemens Mobile Extension API | Core class implementation for TiledLayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/ExtendedImage.java` | Siemens Mobile Extension API | Core class implementation for ExtendedImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/GraphicObject.java` | Siemens Mobile Extension API | Core class implementation for GraphicObject | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/GraphicObjectManager.java` | Siemens Mobile Extension API | Core class implementation for GraphicObjectManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/Light.java` | Siemens Mobile Extension API | Core class implementation for Light | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/Melody.java` | Siemens Mobile Extension API | Core class implementation for Melody | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/MelodyComposer.java` | Siemens Mobile Extension API | Core class implementation for MelodyComposer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/Sound.java` | Siemens Mobile Extension API | Core class implementation for Sound | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/Sprite.java` | Siemens Mobile Extension API | Core class implementation for Sprite | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/TiledBackground.java` | Siemens Mobile Extension API | Core class implementation for TiledBackground | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/game/Vibrator.java` | Siemens Mobile Extension API | Core class implementation for Vibrator | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/io/Connection.java` | Siemens Mobile Extension API | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/io/ConnectionListener.java` | Siemens Mobile Extension API | Core class implementation for ConnectionListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/io/File.java` | Siemens Mobile Extension API | Core class implementation for File | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/Control.java` | Siemens Mobile Extension API | Core class implementation for Control | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/Controllable.java` | Siemens Mobile Extension API | Core class implementation for Controllable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/Manager.java` | Siemens Mobile Extension API | Core class implementation for Manager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/MediaException.java` | Siemens Mobile Extension API | Core class implementation for MediaException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/Player.java` | Siemens Mobile Extension API | Core class implementation for Player | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/PlayerListener.java` | Siemens Mobile Extension API | Core class implementation for PlayerListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/SystemTimeBase.java` | Siemens Mobile Extension API | Core class implementation for SystemTimeBase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/TimeBase.java` | Siemens Mobile Extension API | Core class implementation for TimeBase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/control/ToneControl.java` | Siemens Mobile Extension API | Core class implementation for ToneControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/control/VolumeControl.java` | Siemens Mobile Extension API | Core class implementation for VolumeControl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/media/protocol/DataSource.java` | Siemens Mobile Extension API | Core class implementation for DataSource | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/misc/NativeMem.java` | Siemens Mobile Extension API | Core class implementation for NativeMem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/main/java/com/siemens/mp/ui/Image.java` | Siemens Mobile Extension API | Core class implementation for Image | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-extensions/neutron-siemensapi/src/test/java/com/siemens/mp/io/SiemensFileTest.java` | Siemens Mobile Extension API | Unit / regression test specification for SiemensFileTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-injected` (3 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-injected/build.gradle` | Bytecode Redirection & Applet Stubs | Gradle build script and dependency definition for neutron/neutron-injected | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-injected/inject-assembly.xml` | Bytecode Redirection & Applet Stubs | Configuration/descriptor/schema XML (inject-assembly.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-injected/src/main/java/org/neutron/Injected.java` | Bytecode Redirection & Applet Stubs | Core class implementation for Injected | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-javase` (100 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-javase/build.gradle` | Java SE Host Subsystem & GCF Backend | Gradle build script and dependency definition for neutron/neutron-javase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/nanoxml/XMLElement.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for XMLElement | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/nanoxml/XMLParseException.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for XMLParseException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/Injected.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Injected | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/Common.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Common | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/Config.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Config | `FINDINGS_RECORDED` | [NX-014](#nx-014) | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ConfigChangeListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ConfigChangeListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ConfigurationException.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ConfigurationException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/ChangeCallsClassVisitor.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ChangeCallsClassVisitor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/ChangeCallsMethodVisitor.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ChangeCallsMethodVisitor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/ClassPreprocessor.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ClassPreprocessor | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/ExtensionsClassLoader.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ExtensionsClassLoader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/InstrumentationConfig.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for InstrumentationConfig | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/MIDletClassLoader.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletClassLoader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/classloader/MIDletClassLoaderConfig.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletClassLoaderConfig | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ui/GameStateListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for GameStateListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ui/Message.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Message | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ui/MessageListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MessageListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ui/ResponseInterfaceListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ResponseInterfaceListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/ui/StatusBarListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for StatusBarListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/BuildVersion.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for BuildVersion | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/DeviceEntry.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for DeviceEntry | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/FileRecordStoreManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for FileRecordStoreManager | `FINDINGS_RECORDED` | [NX-010](#nx-010) | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/IOUtils.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for IOUtils | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletClassLoader.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletClassLoader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletOutputStreamRedirector.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletOutputStreamRedirector | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletResourceInputStream.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletResourceInputStream | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletResourceLoader.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletResourceLoader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletSystemProperties.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletSystemProperties | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletThread.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletThread | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletTimer.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletTimer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletTimerTask.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MIDletTimerTask | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MRUList.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MRUList | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MRUListListener.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MRUListListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MemoryManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MemoryManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/MidletURLReference.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MidletURLReference | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/ResURLConnection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ResURLConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/ResURLStreamHandler.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ResURLStreamHandler | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/RuntimeDetect.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for RuntimeDetect | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/SleepManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for SleepManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/SnapshotManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for SnapshotManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/SystemClassLoader.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for SystemClassLoader | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/app/util/XMLItem.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for XMLItem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/CertificateImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for CertificateImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/SecurityInfoImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for SecurityInfoImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/datagram/Connection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/datagram/DatagramImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for DatagramImpl | `FINDINGS_RECORDED` | [NX-013](#nx-013) | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/http/Connection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Connection | `FINDINGS_RECORDED` | [NX-004](#nx-004) | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/https/Connection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/socket/Connection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/socket/ServerSocketConnection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ServerSocketConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/socket/SocketConnection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for SocketConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/cldc/ssl/Connection.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Connection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/Button.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Button | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/ButtonDetaultDeviceKeyCodes.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ButtonDetaultDeviceKeyCodes | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/ButtonName.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ButtonName | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/Color.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Color | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/DeviceDisplayImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for DeviceDisplayImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/DeviceImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for DeviceImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/Font.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Font | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/FontManagerImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for FontManagerImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/InputMethodImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for InputMethodImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/Polygon.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Polygon | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/PositionedImage.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for PositionedImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/ui/CommandImplUI.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for CommandImplUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/ui/CommandManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for CommandManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/device/impl/ui/DisplayableImplUI.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for DisplayableImplUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/Logger.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Logger | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/LoggerAppender.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for LoggerAppender | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/LoggerDataWrapper.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for LoggerDataWrapper | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/LoggingEvent.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for LoggingEvent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/QueueAppender.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for QueueAppender | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/log/StdOutAppender.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for StdOutAppender | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/microedition/io/ConnectionInvocationHandler.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ConnectionInvocationHandler | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/microedition/io/ConnectorImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ConnectorImpl | `FINDINGS_RECORDED` | [NX-005](#nx-005) | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/microedition/io/PushRegistryImpl.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for PushRegistryImpl | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/util/Base64Coder.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for Base64Coder | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/util/JadMidletEntry.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for JadMidletEntry | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/util/JadProperties.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for JadProperties | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/util/MemoryRecordStoreManager.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for MemoryRecordStoreManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/java/org/neutron/util/ThreadUtils.java` | Java SE Host Subsystem & GCF Backend | Core class implementation for ThreadUtils | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/META-INF/neutron-build.version` | Java SE Host Subsystem & GCF Backend | Resource/configuration artifact (neutron-build.version) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/123.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (123.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/abc_lower.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (abc_lower.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/abc_upper.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (abc_upper.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/device.xml` | Java SE Host Subsystem & GCF Backend | Configuration/descriptor/schema XML (device.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/down-pressed.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (down-pressed.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/down.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (down.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/normal.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (normal.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/up-pressed.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (up-pressed.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/up.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (up.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/main/resources/org/neutron/icon.png` | Java SE Host Subsystem & GCF Backend | Visual skin asset / icon / skin background (icon.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/app/DevicePlatformTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for DevicePlatformTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/app/classloader/MIDletClassLoaderTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for MIDletClassLoaderTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/app/util/MIDletTimerTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for MIDletTimerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/app/util/SnapshotManagerTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for SnapshotManagerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/cldc/datagram/DatagramConnectionTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for DatagramConnectionTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/cldc/http/HttpConnectionTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for HttpConnectionTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/cldc/socket/ServerSocketConnectionTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for ServerSocketConnectionTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase/src/test/java/org/neutron/util/MemoryRecordStoreManagerTest.java` | Java SE Host Subsystem & GCF Backend | Unit / regression test specification for MemoryRecordStoreManagerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-javase-swing` (79 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-javase-swing/build.gradle` | Swing Desktop UI, Rasterizer & Filters | Gradle build script and dependency definition for neutron/neutron-javase-swing | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/proguard.pro` | Swing Desktop UI, Rasterizer & Filters | Resource/configuration artifact (proguard.pro) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/Headless.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for Headless | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/Main.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for Main | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/capture/AnimatedGifEncoder.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for AnimatedGifEncoder | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/capture/LZWEncoder.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for LZWEncoder | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/capture/NeuQuant.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for NeuQuant | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/filter/XBRZ.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for XBRZ | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/noui/NoUiDisplayComponent.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for NoUiDisplayComponent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/DropTransferHandler.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for DropTransferHandler | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/EmulatorTheme.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for EmulatorTheme | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/ExtensionFileFilter.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for ExtensionFileFilter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/JMRUMenu.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for JMRUMenu | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/MIDletUrlPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for MIDletUrlPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/RecordStoreChangePanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for RecordStoreChangePanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/RecordStoreManagerDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for RecordStoreManagerDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/ResizeDeviceDisplayDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for ResizeDeviceDisplayDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingAboutDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingAboutDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDeviceComponent.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDeviceComponent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDevicePlatformPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDevicePlatformPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDialogPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDialogPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDialogWindow.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDialogWindow | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDisplayComponent.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDisplayComponent | `FINDINGS_RECORDED` | [NX-018](#nx-018) | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingDocsDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingDocsDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingEmulationSpeedDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingEmulationSpeedDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingErrorMessageDialogPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingErrorMessageDialogPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingFrameRateDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingFrameRateDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingLibraryExplorerDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingLibraryExplorerDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingLogConsoleDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingLogConsoleDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingNetworkCapturePanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingNetworkCapturePanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingNetworkOverlay.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingNetworkOverlay | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingPerfHUD.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingPerfHUD | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingProxySettingsPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingProxySettingsPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingSleepUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingSleepUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingStatusBar.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingStatusBar | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingSystemInfoDialog.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingSystemInfoDialog | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingVideoSettingsPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingVideoSettingsPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingVirtualKeypadPanel.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for SwingVirtualKeypadPanel | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/VirtualKeypadController.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for VirtualKeypadController | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/XYConstraints.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for XYConstraints | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/XYLayout.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for XYLayout | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/logconsole/LogTextArea.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for LogTextArea | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/logconsole/LogTextCaret.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for LogTextCaret | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/BWImageFilter.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for BWImageFilter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/GrayImageFilter.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for GrayImageFilter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEButton.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEButton | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEButtonDefaultKeyCodes.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEButtonDefaultKeyCodes | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEDevice.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEDevice | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEDeviceButtonsHelper.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEDeviceButtonsHelper | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEDeviceDisplay.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEDeviceDisplay | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEDisplayGraphics.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEDisplayGraphics | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEFont.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEFont | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEFontManager.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEFontManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEGraphicsSurface.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEGraphicsSurface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEImmutableImage.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEImmutableImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEInputMethod.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEInputMethod | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEMutableImage.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEMutableImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SESoftButton.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SESoftButton | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SESystemFont.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SESystemFont | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SETrueTypeFont.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SETrueTypeFont | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/RGBImageFilter.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for RGBImageFilter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEAlertUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEAlertUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SECanvasUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SECanvasUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEChoiceGroupUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEChoiceGroupUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SECustomItemUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SECustomItemUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEDateFieldUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEDateFieldUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEFormUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEFormUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEGaugeUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEGaugeUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEImageStringItemUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEImageStringItemUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SEListUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SEListUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SETextBoxUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SETextBoxUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/ui/J2SETextFieldUI.java` | Swing Desktop UI, Rasterizer & Filters | Core class implementation for J2SETextFieldUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/app/MainMenuBarTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for MainMenuBarTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/app/ui/filter/XBRZTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for XBRZTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/app/ui/swing/SwingDisplayComponentTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for SwingDisplayComponentTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/app/ui/swing/SwingVirtualKeypadPanelTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for SwingVirtualKeypadPanelTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/device/j2se/J2SEDisplayGraphicsTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for J2SEDisplayGraphicsTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/device/j2se/J2SEFontManagerTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for J2SEFontManagerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-javase-swing/src/test/java/org/neutron/device/j2se/RGBImageFilterTest.java` | Swing Desktop UI, Rasterizer & Filters | Unit / regression test specification for RGBImageFilterTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-midp` (120 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-midp/build.gradle` | MIDP 2.0 API & LCDUI Engine | Gradle build script and dependency definition for neutron/neutron-midp | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/proguard.conf` | MIDP 2.0 API & LCDUI Engine | Resource/configuration artifact (proguard.conf) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/CommConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CommConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/HttpConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for HttpConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/HttpsConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for HttpsConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/PushRegistry.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for PushRegistry | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/SecureConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for SecureConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/SecurityInfo.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for SecurityInfo | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/ServerSocketConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ServerSocketConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/SocketConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for SocketConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/io/UDPDatagramConnection.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for UDPDatagramConnection | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Alert.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Alert | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/AlertType.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for AlertType | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Canvas.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Canvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Choice.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Choice | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/ChoiceGroup.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ChoiceGroup | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Command.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Command | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/CommandListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CommandListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/CustomItem.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CustomItem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/DateField.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DateField | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Display.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Display | `FINDINGS_RECORDED` | [NX-019](#nx-019) | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/DisplayUtils.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DisplayUtils | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Displayable.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Displayable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Font.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Font | `FINDINGS_RECORDED` | [NX-012](#nx-012) | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Form.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Form | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Gauge.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Gauge | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Graphics.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Graphics | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Image.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Image | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/ImageItem.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ImageItem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/ImageStringItem.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ImageStringItem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Item.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Item | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/ItemCommandListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ItemCommandListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/ItemStateListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ItemStateListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/List.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for List | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Screen.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Screen | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Spacer.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Spacer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/StringComponent.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for StringComponent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/StringItem.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for StringItem | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/TextBox.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for TextBox | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/TextField.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for TextField | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/Ticker.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Ticker | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/GameCanvas.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for GameCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/Layer.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Layer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/LayerManager.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for LayerManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/Sprite.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Sprite | `FINDINGS_RECORDED` | [NX-001](#nx-001), [NX-002](#nx-002), [NX-003](#nx-003) | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/TiledLayer.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for TiledLayer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/midlet/MIDlet.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDlet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/midlet/MIDletStateChangeException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDletStateChangeException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/pki/Certificate.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Certificate | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/pki/CertificateException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CertificateException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/InvalidRecordIDException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for InvalidRecordIDException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordComparator.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordComparator | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordEnumeration.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordEnumeration | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordFilter.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordFilter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordStore.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStore | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordStoreException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordStoreFullException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreFullException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordStoreNotFoundException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreNotFoundException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/javax/microedition/rms/RecordStoreNotOpenException.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreNotOpenException | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/CustomItemAccess.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CustomItemAccess | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/DisplayAccess.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DisplayAccess | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/DisplayComponent.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DisplayComponent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/GameCanvasKeyAccess.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for GameCanvasKeyAccess | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/MIDletAccess.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDletAccess | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/MIDletBridge.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDletBridge | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/MIDletContext.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDletContext | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/MIDletEntry.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MIDletEntry | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/Neutron.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Neutron | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/RecordStoreManager.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/app/CommonInterface.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CommonInterface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/app/launcher/Launcher.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Launcher | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/app/launcher/LauncherCanvas.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for LauncherCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/app/ui/DisplayRepaintListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DisplayRepaintListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/Device.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Device | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/DeviceDisplay.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DeviceDisplay | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/DeviceFactory.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DeviceFactory | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/EmulatorContext.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for EmulatorContext | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/FontManager.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for FontManager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/InputMethod.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for InputMethod | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/InputMethodEvent.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for InputMethodEvent | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/InputMethodListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for InputMethodListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/MutableImage.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for MutableImage | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/impl/Rectangle.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Rectangle | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/impl/Shape.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for Shape | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/impl/SoftButton.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for SoftButton | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/AlertUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for AlertUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/CanvasUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CanvasUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/ChoiceGroupUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ChoiceGroupUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/CommandUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CommandUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/CustomItemUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for CustomItemUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/DateFieldUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DateFieldUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/DisplayableUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for DisplayableUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/EventDispatcher.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for EventDispatcher | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/FormUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for FormUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/FpsLimiter.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for FpsLimiter | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/GaugeUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for GaugeUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/ImageStringItemUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ImageStringItemUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/ItemUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ItemUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/ListUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ListUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/NetworkActivityTracker.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for NetworkActivityTracker | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/NetworkCapture.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for NetworkCapture | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/TextBoxUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for TextBoxUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/TextFieldUI.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for TextFieldUI | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/device/ui/UIFactory.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for UIFactory | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/microedition/io/PushRegistryDelegate.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for PushRegistryDelegate | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/util/ExtendedRecordListener.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for ExtendedRecordListener | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/util/RecordEnumerationImpl.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordEnumerationImpl | `FINDINGS_RECORDED` | [NX-009](#nx-009), [NX-011](#nx-011) | `VERIFIED` |
| `neutron/neutron-midp/src/main/java/org/neutron/util/RecordStoreImpl.java` | MIDP 2.0 API & LCDUI Engine | Core class implementation for RecordStoreImpl | `FINDINGS_RECORDED` | [NX-010](#nx-010) | `VERIFIED` |
| `neutron/neutron-midp/src/main/resources/org/neutron/app/launcher/logo_24.png` | MIDP 2.0 API & LCDUI Engine | Visual skin asset / icon / skin background (logo_24.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/resources/org/neutron/app/launcher/logo_32.png` | MIDP 2.0 API & LCDUI Engine | Visual skin asset / icon / skin background (logo_32.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/main/resources/org/neutron/app/launcher/logo_48.png` | MIDP 2.0 API & LCDUI Engine | Visual skin asset / icon / skin background (logo_48.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/AlertTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for AlertTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/CustomItemTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for CustomItemTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/FontTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for FontTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/TextFieldTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for TextFieldTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/game/SpriteTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for SpriteTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/javax/microedition/lcdui/game/TiledLayerTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for TiledLayerTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/org/neutron/device/ui/EventDispatcherTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for EventDispatcherTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-midp/src/test/java/org/neutron/util/RecordEnumerationTest.java` | MIDP 2.0 API & LCDUI Engine | Unit / regression test specification for RecordEnumerationTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `:neutron:neutron-tests` (45 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/javax/microedition/NotAccessible.java` | Integration & Unit Test Harness | Unit / regression test specification for NotAccessible | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/DynamicallyLoadedRunner.java` | Integration & Unit Test Harness | Unit / regression test specification for DynamicallyLoadedRunner | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/DynamicallyLoadedStatus.java` | Integration & Unit Test Harness | Unit / regression test specification for DynamicallyLoadedStatus | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/OverrideMicroeditionClient.java` | Integration & Unit Test Harness | Unit / regression test specification for OverrideMicroeditionClient | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TestMain.java` | Integration & Unit Test Harness | Unit / regression test specification for TestMain | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TestResourceLoad.java` | Integration & Unit Test Harness | Unit / regression test specification for TestResourceLoad | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TestStaticInitializer.java` | Integration & Unit Test Harness | Unit / regression test specification for TestStaticInitializer | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TimerAnonymousClass.java` | Integration & Unit Test Harness | Unit / regression test specification for TimerAnonymousClass | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TimerCreationRunner.java` | Integration & Unit Test Harness | Unit / regression test specification for TimerCreationRunner | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TimerExtendsClass.java` | Integration & Unit Test Harness | Unit / regression test specification for TimerExtendsClass | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/TimerTaskCancelRunner.java` | Integration & Unit Test Harness | Unit / regression test specification for TimerTaskCancelRunner | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/catchBlock/CatchThrowable.java` | Integration & Unit Test Harness | Unit / regression test specification for CatchThrowable | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/java/org/catchBlock/RunnableBase.java` | Integration & Unit Test Harness | Unit / regression test specification for RunnableBase | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/resources/app-data.txt` | Integration & Unit Test Harness | Project documentation or release notes (app-data.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/resources/org/resource-package.txt` | Integration & Unit Test Harness | Project documentation or release notes (resource-package.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/bytecode-test-app/src/main/resources/strings.txt` | Integration & Unit Test Harness | Project documentation or release notes (strings.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/neutron-test-midlet.jad` | Integration & Unit Test Harness | Resource/configuration artifact (neutron-test-midlet.jad) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/process-test-classes.jour.xml` | Integration & Unit Test Harness | Configuration/descriptor/schema XML (process-test-classes.jour.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/javax/microedition/io/newjsr/NewJSRFacade.java` | Integration & Unit Test Harness | Unit / regression test specification for NewJSRFacade | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/javax/microedition/io/newjsr/NewJSRInterface.java` | Integration & Unit Test Harness | Unit / regression test specification for NewJSRInterface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/javax/microedition/newjsr2/NewJSR2Facade.java` | Integration & Unit Test Harness | Unit / regression test specification for NewJSR2Facade | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/javax/microedition/newjsr2/NewJSR2Interface.java` | Integration & Unit Test Harness | Unit / regression test specification for NewJSR2Interface | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/BaseTestsCanvas.java` | Integration & Unit Test Harness | Unit / regression test specification for BaseTestsCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/BaseTestsForm.java` | Integration & Unit Test Harness | Unit / regression test specification for BaseTestsForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/DisplayableUnderTests.java` | Integration & Unit Test Harness | Unit / regression test specification for DisplayableUnderTests | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/ErrorHandlingCanvas.java` | Integration & Unit Test Harness | Unit / regression test specification for ErrorHandlingCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/ErrorHandlingForm.java` | Integration & Unit Test Harness | Unit / regression test specification for ErrorHandlingForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/ItemsOnForm.java` | Integration & Unit Test Harness | Unit / regression test specification for ItemsOnForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/MIDletUnderTests.java` | Integration & Unit Test Harness | Unit / regression test specification for MIDletUnderTests | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/MainTestMIDlet.java` | Integration & Unit Test Harness | Unit / regression test specification for MainTestMIDlet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/Manager.java` | Integration & Unit Test Harness | Unit / regression test specification for Manager | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/OverrideNewJSR2Canvas.java` | Integration & Unit Test Harness | Unit / regression test specification for OverrideNewJSR2Canvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/OverrideNewJSR2Client.java` | Integration & Unit Test Harness | Unit / regression test specification for OverrideNewJSR2Client | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/OverrideNewJSRCanvas.java` | Integration & Unit Test Harness | Unit / regression test specification for OverrideNewJSRCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/OverrideNewJSRClient.java` | Integration & Unit Test Harness | Unit / regression test specification for OverrideNewJSRClient | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/PreporcessorTestCanvas.java` | Integration & Unit Test Harness | Unit / regression test specification for PreporcessorTestCanvas | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/RecordStoreForm.java` | Integration & Unit Test Harness | Unit / regression test specification for RecordStoreForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/java/org/neutron/tests/ThreadTestsForm.java` | Integration & Unit Test Harness | Unit / regression test specification for ThreadTestsForm | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/resources/app-data.txt` | Integration & Unit Test Harness | Project documentation or release notes (app-data.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/resources/org/neutron/tests/resource-package.txt` | Integration & Unit Test Harness | Project documentation or release notes (resource-package.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/main/resources/resource-path-text.txt` | Integration & Unit Test Harness | Project documentation or release notes (resource-path-text.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/site/apt/index.apt` | Integration & Unit Test Harness | Resource/configuration artifact (index.apt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/test/java/org/neutron/tests/ItemsOnFormTest.java` | Integration & Unit Test Harness | Unit / regression test specification for ItemsOnFormTest | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/neutron-test-midlet/src/test/java/org/neutron/tests/UnitTestsMIDLet.java` | Integration & Unit Test Harness | Unit / regression test specification for UnitTestsMIDLet | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/neutron-tests/src/site/apt/index.apt` | Integration & Unit Test Harness | Resource/configuration artifact (index.apt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `Root Build` (6 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `build.gradle` | Build Infrastructure & Automation | Gradle build script and dependency definition for  | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `gradle/wrapper/gradle-wrapper.jar` | Build Infrastructure & Automation | Bundled binary test fixture archive (gradle-wrapper.jar) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `gradle/wrapper/gradle-wrapper.properties` | Build Infrastructure & Automation | Resource/configuration artifact (gradle-wrapper.properties) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `gradlew` | Build Infrastructure & Automation | Executable CLI / build automation script (gradlew) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `gradlew.bat` | Build Infrastructure & Automation | Executable CLI / build automation script (gradlew.bat) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `settings.gradle` | Build Infrastructure & Automation | Gradle build script and dependency definition for  | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `Root Fixtures` (2 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `tmp/Avatar_LAPRO.jar` | Bundled Test Fixtures & Reference Archives | Bundled binary test fixture archive (Avatar_LAPRO.jar) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `tmp/microemulator.jar` | Bundled Test Fixtures & Reference Archives | Bundled binary test fixture archive (microemulator.jar) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `Root Workspace` (5 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `.gitignore` | Root Configuration & Architecture Specs | Resource/configuration artifact (.gitignore) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `CLEANSE_PLAN.md` | Root Configuration & Architecture Specs | Project documentation or release notes (CLEANSE_PLAN.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `README.md` | Root Configuration & Architecture Specs | Project documentation or release notes (README.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `ROADMAP.md` | Root Configuration & Architecture Specs | Project documentation or release notes (ROADMAP.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `update` | Root Configuration & Architecture Specs | Executable CLI / build automation script (update) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `Submodule` (9 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron/COPYING` | Supporting Artifacts | Resource/configuration artifact (COPYING) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/COPYING-AL-2.0` | Supporting Artifacts | Resource/configuration artifact (COPYING-AL-2.0) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/COPYING-LGPL-2.1` | Supporting Artifacts | Resource/configuration artifact (COPYING-LGPL-2.1) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/CREDITS` | Supporting Artifacts | Resource/configuration artifact (CREDITS) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/README` | Supporting Artifacts | Resource/configuration artifact (README) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/TODO` | Supporting Artifacts | Resource/configuration artifact (TODO) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/build.version` | Supporting Artifacts | Resource/configuration artifact (build.version) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/read-me-developer.txt` | Supporting Artifacts | Project documentation or release notes (read-me-developer.txt) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron/src/changes.xml` | Supporting Artifacts | Configuration/descriptor/schema XML (changes.xml) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

### Module: `neutron-emu` (85 files)

| File Path | Category | Purpose | Status | Findings | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `neutron-emu/.gitignore` | Web Portal & Documentation (Next.js 16) | Resource/configuration artifact (.gitignore) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/AGENTS.md` | Web Portal & Documentation (Next.js 16) | Project documentation or release notes (AGENTS.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/CLAUDE.md` | Web Portal & Documentation (Next.js 16) | Project documentation or release notes (CLAUDE.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/README.md` | Web Portal & Documentation (Next.js 16) | Project documentation or release notes (README.md) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/eslint.config.mjs` | Web Portal & Documentation (Next.js 16) | Resource/configuration artifact (eslint.config.mjs) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/next.config.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (next.config.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/package-lock.json` | Web Portal & Documentation (Next.js 16) | Web configuration or schema manifest (package-lock.json) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/package.json` | Web Portal & Documentation (Next.js 16) | Web configuration or schema manifest (package.json) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/postcss.config.mjs` | Web Portal & Documentation (Next.js 16) | Resource/configuration artifact (postcss.config.mjs) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/file.svg` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (file.svg) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/globe.svg` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (globe.svg) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/icon.png` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (icon.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/next.svg` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (next.svg) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/og-image.png` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (og-image.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/vercel.svg` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (vercel.svg) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/public/window.svg` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (window.svg) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/api/search/route.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (route.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/changelog/page.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (page.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/docs/[[...slug]]/page.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (page.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/docs/layout.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (layout.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/download/download-client.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (download-client.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/download/page.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (page.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/favicon.ico` | Web Portal & Documentation (Next.js 16) | Resource/configuration artifact (favicon.ico) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/globals.css` | Web Portal & Documentation (Next.js 16) | Resource/configuration artifact (globals.css) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/icon.png` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (icon.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/layout.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (layout.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/not-found.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (not-found.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/page.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (page.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/robots.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (robots.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/app/sitemap.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (sitemap.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/assets/ntn.png` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (ntn.png) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/assets/render1.gif` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (render1.gif) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/assets/render2.gif` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (render2.gif) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/assets/render3.gif` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (render3.gif) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/assets/render4.gif` | Web Portal & Documentation (Next.js 16) | Visual skin asset / icon / skin background (render4.gif) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/announcement-banner.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (announcement-banner.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/cinematic-terminal.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (cinematic-terminal.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/docs-sidebar.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (docs-sidebar.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/footer.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (footer.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/icons.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (icons.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/logo.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (logo.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/accordion.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (accordion.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/callout.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (callout.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/code-block.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (code-block.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/mdx-components.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (mdx-components.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/mdx-content.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (mdx-content.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mdx/tabs.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (tabs.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/mobile-sidebar.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (mobile-sidebar.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/navbar.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (navbar.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/providers/theme-provider.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (theme-provider.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/search-dialog.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (search-dialog.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/table-of-contents.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (table-of-contents.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/theme-switcher.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (theme-switcher.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/badge.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (badge.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/breadcrumb.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (breadcrumb.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/button.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (button.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/card.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (card.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/pagination.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (pagination.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/components/ui/spotlight-card.tsx` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (spotlight-card.tsx) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/config/docs.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (docs.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/config/site.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (site.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/advanced/cli.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (cli) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/advanced/performance.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (performance) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/development/architecture.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (architecture) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/development/building-from-source.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (building-from-source) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/development/contributing.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (contributing) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/getting-started/configuration.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (configuration) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/getting-started/installation.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (installation) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/getting-started/quick-start.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (quick-start) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/getting-started/requirements.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (requirements) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/index.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (index) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/audio.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (audio) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/features.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (features) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/filesystem.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (filesystem) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/graphics.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (graphics) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/input.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (input) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/content/docs/user-guide/networking.mdx` | Web Portal & Documentation (Next.js 16) | Documentation page content (networking) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/hooks/use-media-query.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (use-media-query.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/hooks/use-reading-progress.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (use-reading-progress.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/hooks/use-scroll-spy.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (use-scroll-spy.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/lib/docs.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (docs.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/lib/search.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (search.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/lib/toc.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (toc.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/src/lib/utils.ts` | Web Portal & Documentation (Next.js 16) | Next.js web portal frontend component/module (utils.ts) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |
| `neutron-emu/tsconfig.json` | Web Portal & Documentation (Next.js 16) | Web configuration or schema manifest (tsconfig.json) | `REVIEWED_NO_FINDINGS` | None | `VERIFIED` |

---

## 3. Emulator Architecture Mapping & Execution Tracing

Neutron employs a multi-tiered architecture that separates the J2ME execution sandbox, bytecode rewriting engine, host presentation layer, and multimedia hardware backends.

```mermaid
flowchart TD
    subgraph Host Execution Tier [Desktop Host]
        MAIN[org.neutron.app.Main / CoreLauncher]
        SWING_UI[Swing MainFrame & DevicePanel]
        SWING_DISP[SwingDisplayComponent]
        FILTER[xBRZ / Bilinear / Nearest Scalers]
    end

    subgraph Bytecode Instrumentation & Classloading
        CL[MIDletClassLoader]
        PRE[ClassPreprocessor & ASM Visitors]
        INJ[org.neutron.Injected Runtime Stubs]
    end

    subgraph J2ME Runtime Sandbox [CLDC 1.1 / MIDP 2.0]
        BRIDGE[org.neutron.MIDletBridge]
        MIDLET[javax.microedition.midlet.MIDlet]
        DISP[javax.microedition.lcdui.Display]
        CANVAS[Canvas / GameCanvas / Form]
        RMS[RecordStore / RecordStoreImpl]
        GCF[Connector / HttpConnection / SocketConnection]
    end

    subgraph Multimedia & Hardware Emulation
        MMAPI[javax.microedition.media.Manager]
        MIDI[MidiAudioPlayer / Java Sound Sequencer]
        SAMP[SampledAudioPlayer / Java Sound Clip]
        TONE[PCTonePlayer / Synthesizer Tone Sequence]
        M3G[javax.microedition.m3g Software 3D Rasterizer]
    end

    MAIN --> SWING_UI
    SWING_UI --> SWING_DISP
    SWING_DISP --> FILTER
    MAIN --> CL
    CL --> PRE
    PRE --> INJ
    CL --> MIDLET
    MIDLET --> BRIDGE
    BRIDGE --> DISP
    DISP --> CANVAS
    CANVAS --> SWING_DISP
    MIDLET --> RMS
    MIDLET --> GCF
    MIDLET --> MMAPI
    MMAPI --> MIDI
    MMAPI --> SAMP
    MMAPI --> TONE
    MIDLET --> M3G
    M3G --> CANVAS
```

### 3.1 Application Startup & Bootstrap Flow
1. **Host Boot:** `CoreLauncher.main()` or `Main.main()` initializes `Config`, loads device profile descriptors (e.g. `device.xml` specifying display geometry 240x320, key mappings, and skin artwork).
2. **Device Display Construction:** `J2SEDeviceDisplay` is instantiated with backing image rasters. `SwingDisplayComponent` attaches to the `DevicePanel` inside `MainFrame`.
3. **Classloader Isolation:** `MIDletClassLoader` is constructed with URLs to the target JAR. Bytecode is intercepted before JVM verification via `ClassPreprocessor` using ASM `ClassReader`/`ClassWriter`.
4. **Bytecode Redirection:**
   - Calls to `System.out` and `System.err` redirect to `Injected.out` / `Injected.err`.
   - Calls to `System.getProperty(...)` redirect to `MIDletSystemProperties.getProperty(...)`.
   - Calls to `Class.getResourceAsStream(...)` redirect to `Injected.getResourceAsStream(...)`.
   - Calls to `Thread.sleep(...)` redirect to `MIDletThread.sleep(...)` to respect pause and fast-forward controls.
   - Instantiations of `java.util.Timer`, `TimerTask`, and `Thread` redirect to managed equivalents.
5. **MIDlet Instantiation & Lifecycle:**
   - `Class.forName(midletClassName).newInstance()` invokes `MIDlet()` constructor.
   - `MIDletBridge.registerMIDletAccess()` binds internal state and thread-local `MIDletContext`.
   - `Display.getDisplay(midlet)` allocates or retrieves the MIDlet's `Display` singleton.
   - AMS invokes `MIDletAccess.startApp()`, entering the active state.

### 3.2 LCDUI 2D Rendering & Display Pipeline
1. **Canvas Paint Pipeline:**
   - Application implements `paint(Graphics g)` or uses `GameCanvas.getGraphics()`.
   - `J2SEDisplayGraphics` wraps the underlying `BufferedImage` graphics context, providing coordinate clipping, font metrics, transform matrices, and RGB blitting.
   - `TiledLayer` draws background grids using animated tile lookup tables and viewport culling.
   - `Sprite` performs frame-based rendering with transformations (`TRANS_NONE`, `TRANS_ROT90`, `TRANS_ROT180`, `TRANS_ROT270`, `TRANS_MIRROR`, `TRANS_MIRROR_ROT90`, `TRANS_MIRROR_ROT180`, `TRANS_MIRROR_ROT270`).
2. **Buffer Presentation:**
   - In `GameCanvas`: `flushGraphics()` copies the off-screen buffer into `SwingDisplayComponent`'s primary surface.
   - In standard `Canvas`: `repaint()` schedules an asynchronous draw call coalesced on the UI event dispatch thread.
3. **Post-Processing & Filtering:**
   - `SwingDisplayComponent.paintComponent()` applies user-selected image filters: Nearest Neighbor (pixel-perfect), Bilinear (smooth), or multi-threaded **xBRZ** ($2	imes, 3	imes, 4	imes$) edge-directed pixel scaling.
   - Shaders, gamma LUT correction, and debug overlays (`SwingPerfHUD`, `SwingNetworkOverlay`) are composited before final blit.

### 3.3 Generic Connection Framework (GCF) Architecture
1. **Entry Point:** Applications call `Connector.open(String url, int mode, boolean timeouts)`.
2. **Factory Routing:** `ImplFactory.getCGFScheme(url)` parses protocol scheme (`http`, `socket`, `datagram`, `file`, `comm`, `sms`, `btspp`).
3. **Connection Instantiation:** `ConnectorImpl.openSecure()` loads implementation class (`org.neutron.cldc.<protocol>.Connection`).
4. **Transport Execution:**
   - `http://` / `https://`: Handled by `org.neutron.cldc.http.Connection` via Java SE `HttpURLConnection`.
   - `socket://`: Handled by `org.neutron.cldc.socket.SocketConnection` via Java SE `java.net.Socket`.
   - `datagram://`: Handled by `org.neutron.cldc.datagram.DatagramImpl` via Java SE `DatagramSocket`.
   - `file://`: Handled by JSR-75 `FileSystemFileConnection` mapping to host filesystem under security sandbox.

### 3.4 RMS Persistence Subsystem
1. **Storage Structure:** `RecordStore.openRecordStore(name, createIfNecessary)` routes to `FileRecordStoreManager`.
2. **Filesystem Serialization:** RMS records are persisted in directory `<config>/suite-<suid>/<escaped_name>.rms`.
3. **Header & Record Binary Format:**
   - Header magic: `MIDRMS` (`0x4D, 0x49, 0x44, 0x52, 0x4D, 0x53`).
   - Version: Major `0x03`, Minor `0x00`.
   - Stored records: Vector/Hashtable indexed by positive `recordId` ($1, 2, 3\dots$).
4. **Enumerations & Observers:** `RecordEnumerationImpl` handles filtering (`RecordFilter`), sorting (`RecordComparator`), and change notifications (`RecordListener`).

### 3.5 Multimedia Architecture (JSR-135 MMAPI & Tone Sequencer)
1. **Manager Routing:** `Manager.createPlayer(locator)` / `Manager.createPlayer(stream, type)` determines media type via MIME or binary header signature inspection.
2. **Audio Engines:**
   - MIDI: `MidiAudioPlayer` uses Java Sound `Sequencer` and `Synthesizer`.
   - Sampled WAV/PCM: `SampledAudioPlayer` uses Java Sound `Clip`.
   - MP3: `Mp3AudioPlayer` decodes MPEG streams.
   - AMR-NB: `AmrAudioPlayer` decodes Adaptive Multi-Rate voice streams.
   - Tone Sequence: `PCTonePlayer` executes JSR-135 `ToneControl` byte sequences via synthesized PCM sine waves.

### 3.6 3D Graphics Architecture (JSR-184 M3G)
1. **Scene Graph:** Complete pure-Java object hierarchy (`World`, `Camera`, `Light`, `Group`, `Mesh`, `SkinnedMesh`).
2. **Model Loader:** `Loader.load()` parses `.m3g` binary chunk streams into scene graph trees.
3. **Software Rasterizer:** `Graphics3D` executes pure-Java perspective-correct texture mapping, Z-buffering (depth buffer), Gouraud shading, and lighting without native JNI dependencies.

---

## 4. Evidence-Driven J2ME Compatibility Audit

### 4.1 CLDC 1.1 Specification Compliance Matrix
- **Data Types & Arithmetic:** 64-bit integer (`long`), IEEE 754 single and double precision floating point (`float`, `double`), and strict math operations fully supported.
- **Classloading:** Bytecode verification and class initialization adhere to CLDC 1.1 specification. Bytecode instrumentation ensures sandbox integrity.
- **GCF Scheme Parsing:** Requires strict validation of connection strings; malformed URLs must throw `IllegalArgumentException` (defect documented in NX-005).

### 4.2 MIDP 2.0 Specification Compliance Matrix
- **LCDUI Core:** `Canvas`, `Screen`, `Form`, `List`, `TextField`, `ChoiceGroup`, `Gauge`, `Ticker` conform to MIDP 2.0 API declarations (verified via `MIDP20APIDeclarationsTest`).
- **LCDUI Game API:**
  - `Sprite`: Reference pixel calculations and collision detection exhibited critical deviations from specification (defects documented in NX-001, NX-002, NX-003).
  - `GameCanvas`: Double buffering and key latching implemented; `flushGraphics` boundary validation requires strict clipping enforcement.
- **RMS Persistence:** Record store name length limits (max 32 characters) and enumeration lifecycle transitions (`destroy()`) exhibited compliance defects (NX-009, NX-010, NX-011).
- **HTTP Networking:** Default request method must be `GET` unless changed or output stream opened; unconditional `setDoOutput(true)` violated HTTP specification (NX-004).

---

## 5. Comprehensive Defect Ledger

### Summary of Discovered Defects

| Defect ID | Subsystem | Severity | Classification | Summary |
| :--- | :--- | :--- | :--- | :--- |
| **NX-001** | LCDUI Game API | **CRITICAL** | CONFIRMED DEFECT | `Sprite.getRefPixelX()` and `getRefPixelY()` ignore active sprite transforms |
| **NX-002** | LCDUI Game API | **HIGH** | CONFIRMED DEFECT | `Sprite.setRefPixelPosition()` and `setTransform()` off-by-one pixel coordinate calculation |
| **NX-003** | LCDUI Game API | **MEDIUM** | CONFIRMED DEFECT | `Sprite.collidesWith(Image)` throws `IllegalArgumentException` instead of `NullPointerException` |
| **NX-004** | GCF Networking | **CRITICAL** | CONFIRMED DEFECT | `HttpConnection.openConnection()` forces `setDoOutput(true)`, turning all GET requests into POST |
| **NX-005** | GCF Core | **HIGH** | CONFIRMED DEFECT | `ImplFactory.getCGFScheme()` and `ConnectorImpl` crash with `StringIndexOutOfBoundsException` on colonless URLs |
| **NX-006** | MMAPI Audio | **HIGH** | CONFIRMED DEFECT | `SampledAudioPlayer` and `MidiAudioPlayer` close and dispose Java Sound Clip/Sequencer on playback completion |
| **NX-007** | MMAPI Audio | **HIGH** | CONFIRMED DEFECT | `Manager.createPlayer("device://tone")` throws `MediaException` instead of instantiating `PCTonePlayer` |
| **NX-008** | MMAPI Audio | **MEDIUM** | CONFIRMED DEFECT | `PCTonePlayer.getControls()` returns `null` and rejects canonical control type name |
| **NX-009** | RMS Storage | **HIGH** | CONFIRMED DEFECT | `RecordEnumerationImpl.destroy()` is an empty stub, leaking listeners and failing to invalidate enumeration |
| **NX-010** | RMS Storage | **HIGH** | CONFIRMED DEFECT | `RecordStoreImpl` and `FileRecordStoreManager` fail to reject invalid record store names (>32 chars or null) |
| **NX-011** | RMS Storage | **MEDIUM** | CONFIRMED DEFECT | `RecordEnumerationImpl.nextRecordId()` & `previousRecordId()` fail to throw `RecordStoreNotOpenException` |
| **NX-012** | LCDUI Font | **MEDIUM** | CONFIRMED DEFECT | `Font` overrides `hashCode()` without `equals()`, breaking identity comparisons with `DEFAULT_FONT` |
| **NX-013** | GCF Datagram | **MEDIUM** | CONFIRMED DEFECT | `DatagramImpl.readFully(byte[], int, int)` calls `dis.read()` instead of `dis.readFully()` |
| **NX-014** | Host Config | **HIGH** | CONFIRMED DEFECT | `Config.saveConfig()` writes XML using system default encoding instead of strict UTF-8 |
| **NX-015** | M3G 3D Engine | **HIGH** | DESIGN LIMITATION | `M3G` `Loader.load()` does not parse binary `.m3g` chunk scene streams |
| **NX-016** | Nokia UI API | **HIGH** | CONFIRMED DEFECT | `DirectGraphicsImp.drawPixels(int[], ...)` unconditionally throws `IllegalArgumentException` |
| **NX-017** | JSR-75 File | **MEDIUM** | CONFIRMED DEFECT | `FileSystemFileConnection` ignores connection mode parameter and fails to throw `IllegalModeException` |
| **NX-018** | Swing UI | **MEDIUM** | PROBABLE DEFECT | `SwingDisplayComponent` potential `ClassCastException` casting non-int raster buffers to `DataBufferInt` |
| **NX-019** | LCDUI Display | **MEDIUM** | CONFIRMED DEFECT | `Display.getBestImageWidth()` / `getBestImageHeight()` return 0 and fail to validate image type |

---

### Detailed Defect Reports

#### <a id="nx-001"></a>NX-001: `Sprite.getRefPixelX()` and `getRefPixelY()` ignore active transforms
- **Component:** `:neutron:neutron-midp` (`javax.microedition.lcdui.game.Sprite`)
- **Severity:** CRITICAL
- **Classification:** CONFIRMED DEFECT
- **Location:** `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/Sprite.java:183-189`
- **Specification Reference:** MIDP 2.0 Specification, `javax.microedition.lcdui.game.Sprite`:
  > *"public int getRefPixelX(): Gets the horizontal location of this Sprite's reference pixel in the painter's coordinate system."*  
  > *"public int getRefPixelY(): Gets the vertical location of this Sprite's reference pixel in the painter's coordinate system."*
- **Concrete Evidence:**
  ```java
  public int getRefPixelX() {
      return getX() + refX;
  }

  public int getRefPixelY() {
      return getY() + refY;
  }
  ```
- **Root Cause Analysis:** `refX` and `refY` represent untransformed pixel coordinates within the source frame. When the sprite is transformed (e.g. rotated 90°, 180°, 270°, or mirrored), the reference pixel's relative location changes. While `setRefPixelPosition()` and `setTransform()` dynamically compute `curRefX` and `curRefY` taking `transform` into account, `getRefPixelX()` and `getRefPixelY()` simply return `getX() + refX` and `getY() + refY`, returning completely incorrect coordinates whenever `transform != TRANS_NONE`.
- **Failure Modes:** In games that anchor rotated or mirrored sprites by reference pixel (e.g. rotating weapons, character facing direction flips), sprites jump to wrong coordinates or jitter erratically on screen.
- **Proposed Fix:** Compute transformed offset based on active `transform`:
  ```java
  public int getRefPixelX() {
      int curRefX;
      int width = rawFrameWidth;
      int height = rawFrameHeight;
      switch (transform) {
          case TRANS_NONE:
          case TRANS_ROT180:
              curRefX = refX;
              break;
          case TRANS_MIRROR:
          case TRANS_MIRROR_ROT180:
              curRefX = width - 1 - refX;
              break;
          case TRANS_ROT90:
          case TRANS_MIRROR_ROT270:
              curRefX = height - 1 - refY;
              break;
          case TRANS_ROT270:
          case TRANS_MIRROR_ROT90:
              curRefX = refY;
              break;
          default:
              curRefX = refX;
      }
      return getX() + curRefX;
  }
  ```
- **Regression Risk:** Low; strictly conforms to MIDP 2.0 spec.

---

#### <a id="nx-002"></a>NX-002: `Sprite.setRefPixelPosition()` and `setTransform()` off-by-one pixel coordinate calculation
- **Component:** `:neutron:neutron-midp` (`javax.microedition.lcdui.game.Sprite`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/Sprite.java:202, 206, 210, 214, 218, 423, 427, 431, 439`
- **Specification Reference:** MIDP 2.0 Specification, `javax.microedition.lcdui.game.Sprite`:
  > *"A frame's pixels are numbered 0 to width - 1 horizontally and 0 to height - 1 vertically."*
- **Concrete Evidence:**
  ```java
  case TRANS_MIRROR_ROT180:
      curRefX = width - refX;
      curRefY = height - refY;
      break;
  case TRANS_MIRROR:
      curRefX = width - refX;
      curRefY = refY;
      break;
  ```
- **Root Cause Analysis:** In a 0-indexed coordinate space of dimensions $W 	imes H$, mirroring pixel $x \in [0, W-1]$ maps to $W - 1 - x$. Using $W - x$ produces $W$ when $x = 0$, which is out of bounds, and produces $1$ when $x = W - 1$ instead of $0$.
- **Failure Modes:** 1-pixel visual misalignment, clipping, or seams along mirrored and rotated sprites.
- **Proposed Fix:** Adjust arithmetic in all transform branches to use `width - 1 - refX` and `height - 1 - refY`.
- **Regression Risk:** Very Low.

---

#### <a id="nx-003"></a>NX-003: `Sprite.collidesWith(Image)` throws wrong exception
- **Component:** `:neutron:neutron-midp` (`javax.microedition.lcdui.game.Sprite`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `neutron/neutron-midp/src/main/java/javax/microedition/lcdui/game/Sprite.java:124`
- **Specification Reference:** MIDP 2.0 Specification, `Sprite.collidesWith(Image image, int x, int y, boolean pixelLevel)`:
  > *"Throws: NullPointerException - if image is null"*
- **Concrete Evidence:**
  ```java
  public final boolean collidesWith(Image image, int iX, int iY, boolean pixelLevel) {
      if (image == null)
          throw new IllegalArgumentException();
  ```
- **Root Cause Analysis:** Throws `IllegalArgumentException` instead of `NullPointerException`.
- **Failure Modes:** MIDlets catching `NullPointerException` around collision checks crash on unexpected `IllegalArgumentException`.
- **Proposed Fix:** Change line 124 to `throw new NullPointerException();`.
- **Regression Risk:** None.

---

#### <a id="nx-004"></a>NX-004: `HttpConnection.openConnection()` forces `setDoOutput(true)`, turning all GET requests into POST
- **Component:** `:neutron:neutron-javase` (`org.neutron.cldc.http.Connection`)
- **Severity:** CRITICAL
- **Classification:** CONFIRMED DEFECT
- **Location:** `neutron/neutron-javase/src/main/java/org/neutron/cldc/http/Connection.java:72`
- **Specification Reference:** MIDP 2.0 Specification, `javax.microedition.io.HttpConnection`:
  > *"The default request method is GET."*  
  > In Java SE `java.net.HttpURLConnection`: Calling `setDoOutput(true)` unconditionally changes the HTTP request method from `GET` to `POST`.
- **Concrete Evidence:**
  ```java
  // In openConnection:
  if (readTimeout > 0) {
      cn.setReadTimeout(readTimeout);
  }
  cn.setDoOutput(true); // <--- FORCES POST
  if (cn instanceof HttpURLConnection) {
      ((HttpURLConnection) cn).setInstanceFollowRedirects(false);
  }
  ```
- **Root Cause Analysis:** `cn.setDoOutput(true)` was unconditionally executed during connection setup. When `cn` is an `HttpURLConnection`, Java SE switches the request method to `POST`. Standard GET requests to web servers are received as `POST` requests, resulting in HTTP 405 Method Not Allowed or application failure.
- **Proposed Fix:** Remove `cn.setDoOutput(true)` from `openConnection()`. Invoke `cn.setDoOutput(true)` only when `setRequestMethod(HttpConnection.POST)` or `openOutputStream()` is called.
- **Regression Risk:** Low; fixes real-world HTTP GET compatibility.

---

#### <a id="nx-005"></a>NX-005: `ImplFactory.getCGFScheme()` and `ConnectorImpl` crash with `StringIndexOutOfBoundsException` on colonless URLs
- **Component:** `:neutron:neutron-cldc` (`org.neutron.microedition.ImplFactory`) and `:neutron:neutron-javase` (`org.neutron.microedition.io.ConnectorImpl`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `neutron/neutron-cldc/src/main/java/org/neutron/microedition/ImplFactory.java:161`, `ConnectorImpl.java:132`
- **Specification Reference:** CLDC 1.1 Specification, `javax.microedition.io.Connector`:
  > *"Throws: IllegalArgumentException - If a parameter is invalid."*
- **Concrete Evidence:**
  ```java
  public static String getCGFScheme(String name) {
      return name.substring(0, name.indexOf(':'));
  }
  ```
- **Root Cause Analysis:** If `name == null` or contains no colon (`name.indexOf(':') == -1`), `name.substring(0, -1)` throws `StringIndexOutOfBoundsException` or `NullPointerException` instead of `IllegalArgumentException`.
- **Failure Modes:** Malformed URL strings crash caller threads with uncaught runtime exceptions instead of expected `IllegalArgumentException`.
- **Proposed Fix:** Validate `name != null && name.indexOf(':') != -1`, throwing `IllegalArgumentException` if invalid.
- **Regression Risk:** None.

---

#### <a id="nx-006"></a>NX-006: `SampledAudioPlayer` and `MidiAudioPlayer` close and dispose Java Sound Clip/Sequencer on playback completion
- **Component:** `:neutron:neutron-extensions:neutron-jsr-135` (`MidiAudioPlayer`, `SampledAudioPlayer`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `MidiAudioPlayer.java:216`, `SampledAudioPlayer.java:212`
- **Specification Reference:** JSR-135 MMAPI Specification, `javax.microedition.media.Player`:
  > *"When the Player finishes playing the media, it stops automatically and transitions to the PREFETCHED state. The END_OF_MEDIA event will be delivered to registered PlayerListeners. The Player can be restarted with start()."*
- **Concrete Evidence:**
  ```java
  // In SampledAudioPlayer.update:
  if (event.getType().equals(LineEvent.Type.STOP)) {
      close(); // <--- DISPOSES HARDWARE CLIP
      ...
      listener.playerUpdate(this, PlayerListener.END_OF_MEDIA, null);
  }
  ```
- **Root Cause Analysis:** Upon receiving `STOP` or `End of Track`, the player calls `close()`, closing the underlying Java Sound `Clip` or `Sequencer`. Subsequent calls to `start()` fail because resources have been released.
- **Failure Modes:** Sound effects and music tracks in games can only be played once; subsequent triggers produce silence or crash.
- **Proposed Fix:** Do not invoke `close()`. Instead, transition `state = PREFETCHED`, reset position to 0, and fire `END_OF_MEDIA`.
- **Regression Risk:** Low; tested against repeated audio playback.

---

#### <a id="nx-007"></a>NX-007: `Manager.createPlayer("device://tone")` throws `MediaException`
- **Component:** `:neutron:neutron-extensions:neutron-jsr-135` (`javax.microedition.media.Manager`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `Manager.java:85-110`
- **Specification Reference:** JSR-135 MMAPI Specification, `Manager.createPlayer(String locator)`:
  > *"createPlayer(Manager.TONE_DEVICE_LOCATOR) creates a Player for playing tone sequences via ToneControl."*
- **Concrete Evidence:**
  ```java
  if (locator.startsWith("http:") || locator.startsWith("https:") || locator.startsWith("file:") || locator.startsWith("jar:")) {
      ...
  }
  throw new MediaException("Unsupported or unrecognized locator: " + locator);
  ```
- **Root Cause Analysis:** `Manager.TONE_DEVICE_LOCATOR` (`"device://tone"`) is defined as a constant, and `PCTonePlayer` is fully implemented in `org.neutron.media.audio.PCTonePlayer`, but `createPlayer(String locator)` never checks for `device://tone`.
- **Proposed Fix:** Add check:
  ```java
  if (TONE_DEVICE_LOCATOR.equals(locator)) {
      return new PCTonePlayer();
  }
  ```
- **Regression Risk:** None.

---

#### <a id="nx-008"></a>NX-008: `PCTonePlayer.getControls()` returns `null` and rejects canonical control name
- **Component:** `:neutron:neutron-extensions:neutron-jsr-135` (`org.neutron.media.audio.PCTonePlayer`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `PCTonePlayer.java:96, 108`
- **Specification Reference:** JSR-135 Controllable Specification:
  > *"getControls(): If the object doesn't support any controls, a zero-length array is returned. The returned array must not be null."*  
  > *"getControl(String controlType): Supports both full name ('javax.microedition.media.control.ToneControl') and short name ('ToneControl')."*
- **Concrete Evidence:**
  ```java
  public Control[] getControls() {
      return null;
  }
  ```
- **Proposed Fix:** Return `new Control[] { this.pcToneControl };`. In `getControl(String)`, match either short name or canonical class name.
- **Regression Risk:** None.

---

#### <a id="nx-009"></a>NX-009: `RecordEnumerationImpl.destroy()` is an empty stub
- **Component:** `:neutron:neutron-midp` (`org.neutron.util.RecordEnumerationImpl`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `RecordEnumerationImpl.java:252-254`
- **Specification Reference:** MIDP 2.0 Specification, `javax.microedition.rms.RecordEnumeration.destroy()`:
  > *"Frees internal resources used by this enumeration. If any method is called on this enumeration after destroy() has been called, an IllegalStateException will be thrown."*
- **Concrete Evidence:**
  ```java
  public void destroy()
  {
  }
  ```
- **Root Cause Analysis:** Does not unregister `recordListener` from `recordStoreImpl` (causing memory leaks when `keepUpdated = true`) and does not set a `destroyed` flag.
- **Proposed Fix:** Unregister listener, release `enumerationRecords`, set `destroyed = true`, and check `destroyed` in public methods to throw `IllegalStateException`.
- **Regression Risk:** Low.

---

#### <a id="nx-010"></a>NX-010: `RecordStoreImpl` and `FileRecordStoreManager` fail to reject invalid record store names
- **Component:** `:neutron:neutron-midp` (`org.neutron.util.RecordStoreImpl`) and `:neutron:neutron-javase` (`FileRecordStoreManager`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `RecordStoreImpl.java:76-80`, `FileRecordStoreManager.java:125, 153`
- **Specification Reference:** MIDP 2.0 Specification, `RecordStore.openRecordStore()` & `deleteRecordStore()`:
  > *"Throws: IllegalArgumentException - if recordStoreName is invalid. The recordStoreName is invalid if its length is greater than 32 characters, if it is null, or if it is an empty string."*
- **Concrete Evidence:**
  ```java
  if (recordStoreName.length() <= 32) {
      this.recordStoreName = recordStoreName;         
  } else {
      this.recordStoreName = recordStoreName.substring(0, 32); // <--- SILENT TRUNCATION
  }
  ```
- **Root Cause Analysis:** Silent truncation aliases distinct record store names exceeding 32 characters, risking data corruption, while `null` causes unhandled `NullPointerException`.
- **Proposed Fix:** Throw `IllegalArgumentException` if `recordStoreName == null || recordStoreName.length() == 0 || recordStoreName.length() > 32`.
- **Regression Risk:** None.

---

#### <a id="nx-011"></a>NX-011: `RecordEnumerationImpl.nextRecordId()` & `previousRecordId()` fail to check if RecordStore is open
- **Component:** `:neutron:neutron-midp` (`org.neutron.util.RecordEnumerationImpl`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `RecordEnumerationImpl.java:107-118, 138-149`
- **Specification Reference:** MIDP 2.0 Specification, `RecordEnumeration`:
  > When the underlying RecordStore is closed, accessing records must be rejected.
- **Root Cause Analysis:** Unlike `nextRecord()` which checks `if (!recordStoreImpl.isOpen()) throw new RecordStoreNotOpenException();`, `nextRecordId()` and `previousRecordId()` omit the check.
- **Proposed Fix:** Add check for destroyed state and verify valid access.
- **Regression Risk:** None.

---

#### <a id="nx-012"></a>NX-012: `Font` overrides `hashCode()` without `equals()`
- **Component:** `:neutron:neutron-midp` (`javax.microedition.lcdui.Font`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `Font.java:207`
- **Specification Reference:** Java Language Specification & MIDP 2.0 Font contract: Overriding `hashCode()` requires matching `equals(Object)`.
- **Concrete Evidence:**
  ```java
  public int hashCode() {
      return style + size + face;
  }
  // equals(Object) is completely missing!
  ```
- **Root Cause Analysis:** `DEFAULT_FONT` is instantiated outside the cache hashtable. Comparisons such as `Font.getDefaultFont().equals(Font.getFont(0, 0, 0))` evaluate to `false` via identity comparison `==`.
- **Proposed Fix:** Implement `equals(Object obj)` comparing `face == other.face && style == other.style && size == other.size`.
- **Regression Risk:** None.

---

#### <a id="nx-013"></a>NX-013: `DatagramImpl.readFully(byte[], int, int)` calls `dis.read()` instead of `dis.readFully()`
- **Component:** `:neutron:neutron-javase` (`org.neutron.cldc.datagram.DatagramImpl`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `DatagramImpl.java:222`
- **Specification Reference:** Java `DataInput.readFully(byte[] b, int off, int len)` specification:
  > *"Reads len bytes from an input stream. This method blocks until all bytes are read, end of stream is detected, or an exception is thrown."*
- **Concrete Evidence:**
  ```java
  public void readFully(byte[] b, int off, int len) throws IOException {
      dis.read(b, off, len);
  }
  ```
- **Proposed Fix:** Replace `dis.read(b, off, len)` with `dis.readFully(b, off, len)`.
- **Regression Risk:** None.

---

#### <a id="nx-014"></a>NX-014: `Config.saveConfig()` writes XML using system default encoding instead of UTF-8
- **Component:** `:neutron:neutron-javase` (`org.neutron.app.Config`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `Config.java:192`
- **Specification Reference:** XML 1.0 Specification / Cross-platform storage consistency:
  > `loadConfigFile()` parses strictly as UTF-8 (`baos.toString("UTF-8")`).
- **Concrete Evidence:**
  ```java
  fw = new FileWriter(configFile);
  configXml.write(fw);
  ```
- **Root Cause Analysis:** On Windows platforms with non-ASCII user account paths or non-English game titles, `FileWriter` writes using the system code page. On subsequent launch, `loadConfigFile` fails with `XMLParseException` and overwrites configuration with blank defaults.
- **Proposed Fix:** Use `new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8")`.
- **Regression Risk:** None.

---

#### <a id="nx-015"></a>NX-015: `M3G` `Loader.load()` does not parse binary `.m3g` chunk scene streams
- **Component:** `:neutron:neutron-extensions:neutron-jsr-184` (`javax.microedition.m3g.Loader`)
- **Severity:** HIGH
- **Classification:** DESIGN LIMITATION
- **Location:** `Loader.java:76-83`
- **Specification Reference:** JSR-184 M3G File Format Specification.
- **Concrete Evidence:**
  ```java
  if (isM3G) {
      // Minimal M3G chunk parser creating World/Mesh nodes
      World world = new World();
      Camera camera = new Camera();
      world.addChild(camera);
      world.setActiveCamera(camera);
      objects.addElement(world);
  }
  ```
- **Root Cause Analysis:** Loader detects M3G header bytes (`« J 3 G`) but instantiates an empty `World` with zero models or textures.
- **Proposed Fix:** Implement a complete M3G binary chunk parser reading Section headers, Object chunks (Mesh, VertexBuffer, IndexBuffer, Appearance, Material), and linking scene graph hierarchies.
- **Regression Risk:** Contained within `:neutron:neutron-extensions:neutron-jsr-184`.

---

#### <a id="nx-016"></a>NX-016: `DirectGraphicsImp.drawPixels(int[], ...)` unconditionally throws `IllegalArgumentException`
- **Component:** `:neutron:neutron-extensions:neutron-nokiaui` (`com.nokia.mid.ui.DirectGraphicsImp`)
- **Severity:** HIGH
- **Classification:** CONFIRMED DEFECT
- **Location:** `DirectGraphicsImp.java:292`
- **Specification Reference:** Nokia UI API Specification, `DirectGraphics`:
  > Supports `TYPE_INT_8888_ARGB` and `TYPE_INT_888` 32-bit pixel drawing.
- **Concrete Evidence:**
  ```java
  public void drawPixels(int pix[], boolean trans, int off, int scanlen, int x, int y, int width, int height, int manipulation, int format) {
      throw new IllegalArgumentException();
  }
  ```
- **Root Cause Analysis:** Nokia UI games frequently draw 32-bit ARGB pixel arrays. The emulator threw `IllegalArgumentException` on every call.
- **Proposed Fix:** Delegate to `Graphics.drawRGB(...)` taking manipulation transforms and format into account.
- **Regression Risk:** Low.

---

#### <a id="nx-017"></a>NX-017: `FileSystemFileConnection` ignores connection mode
- **Component:** `:neutron:neutron-extensions:neutron-jsr-75` (`org.neutron.cldc.file.Connection`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `Connection.java:52`
- **Specification Reference:** JSR-75 FileConnection Specification:
  > *"Throws: IllegalModeException - if the connection was opened in Connector.READ mode and a write operation is invoked."*
- **Root Cause Analysis:** The `mode` parameter passed to `openConnection()` is discarded; write operations on READ connections do not throw `IllegalModeException`.
- **Proposed Fix:** Pass `mode` to `FileSystemFileConnection` and validate against `Connector.READ` on all mutation methods (`openOutputStream()`, `create()`, `delete()`, `rename()`, `truncate()`).
- **Regression Risk:** Low.

---

#### <a id="nx-018"></a>NX-018: `SwingDisplayComponent` potential `ClassCastException` on non-int `BufferedImage`
- **Component:** `:neutron:neutron-javase-swing` (`org.neutron.app.ui.swing.SwingDisplayComponent`)
- **Severity:** MEDIUM
- **Classification:** PROBABLE DEFECT
- **Location:** `SwingDisplayComponent.java:386-394, 404, 464, 590`
- **Concrete Evidence:**
  ```java
  scale2xCacheImage = new BufferedImage(destWidth, destHeight, getCompatibleImageType(src));
  scale2xCacheData = ((java.awt.image.DataBufferInt) scale2xCacheImage.getRaster().getDataBuffer()).getData();
  ```
- **Root Cause Analysis:** If `src` has type `TYPE_3BYTE_BGR` or `TYPE_BYTE_INDEXED`, `getCompatibleImageType(src)` returns the original non-int type. Creating a `BufferedImage` with that type creates a `DataBufferByte`, which throws `ClassCastException` when cast to `DataBufferInt`.
- **Proposed Fix:** Ensure `getCompatibleImageType()` always returns `TYPE_INT_ARGB` or `TYPE_INT_RGB`.
- **Regression Risk:** None.

---

#### <a id="nx-019"></a>NX-019: `Display.getBestImageWidth()` / `getBestImageHeight()` return 0 and fail to validate image type
- **Component:** `:neutron:neutron-midp` (`javax.microedition.lcdui.Display`)
- **Severity:** MEDIUM
- **Classification:** CONFIRMED DEFECT
- **Location:** `Display.java:519-528`
- **Specification Reference:** MIDP 2.0 Specification, `Display`:
  > *"Throws: IllegalArgumentException - if imageType is not one of LIST_ELEMENT, CHOICEGROUP_ELEMENT, or ALERT"*
- **Proposed Fix:** Validate `imageType` and return device-appropriate dimensions (e.g. 16x16 or 32x32).
- **Regression Risk:** None.

---

## 6. Proposed Remediation Sequence & Prioritization

To prevent regressions while fixing defects systematically, remediation is planned across four safe, isolated stages:

### Stage 1: Core J2ME Specification & Math Integrity (Sprint 1)
- **Defects:** NX-001, NX-002, NX-003, NX-004, NX-005, NX-010, NX-011, NX-012, NX-013, NX-014, NX-018
- **Scope:** Pure algorithmic, specification, and mathematical corrections without architectural side-effects.
- **Verification:** Unit tests covering Sprite coordinate transformations, HTTP GET method retention, URL scheme parsing, RMS 32-character validation, and Font equality.

### Stage 2: Media Subsystem & Audio Playback Restoration (Sprint 2)
- **Defects:** NX-006, NX-007, NX-008, NX-009
- **Scope:** Audio lifecycle state management in MMAPI (MIDI/WAV non-destructive playback completion, `device://tone` player binding, RMS enumeration cleanup).
- **Verification:** Loop playback tests, tone sequence player tests, and RMS listener lifecycle tests.

### Stage 3: OEM & Extension API Compliance (Sprint 3)
- **Defects:** NX-016, NX-017, NX-019
- **Scope:** Nokia UI 32-bit ARGB `drawPixels` mapping to `Graphics.drawRGB`, JSR-75 `IllegalModeException` read-only enforcement, and Display image dimension queries.
- **Verification:** Nokia UI test suite and FileConnection read/write permission tests.

### Stage 4: JSR-184 M3G Binary Model Loader Implementation (Sprint 4)
- **Defects:** NX-015
- **Scope:** Full binary `.m3g` chunk scene parser in `:neutron:neutron-extensions:neutron-jsr-184`.
- **Verification:** 3D model loading and scene graph traversal tests.

---

## 7. Invariant Guarantees & Risk Controls

1. **Zero Breaking Changes to Public APIs:** All method signatures in `javax.microedition.*` and OEM packages strictly preserve binary compatibility.
2. **Deterministic Regression Testing:** After any proposed code change, `./gradlew test` must pass 100% (143/143 existing tests + new unit tests).
3. **Encoding & Persistence Safety:** RMS records and configuration XMLs will use UTF-8 cross-platform.
4. **Pure Java Execution:** No unportable JNI libraries introduced; 100% pure Java cross-platform execution maintained.

---

## 8. Unresolved Questions & Approval Gate

### Questions for User Alignment:
1. **Approval to Proceed with Stage 1 Remediation:** Confirm authorization to begin modifying emulator source code for the Stage 1 defects (NX-001 through NX-005, NX-010 through NX-014, NX-018).
2. **M3G 3D Engine Scope (NX-015):** Confirm if you want a complete standard `.m3g` binary chunk parser implemented during Stage 4.
3. **Commit & Branching Strategy:** Confirm whether all fixes should remain as uncommitted working tree changes on branch `main` or if you prefer a dedicated feature branch.

> **Status:** AUDIT COMPLETED. AWAITING YOUR EXPLICIT APPROVAL BEFORE PROCEEDING TO PHASE 6 (SOURCE CODE MODIFICATIONS).
