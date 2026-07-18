<img width="295" height="37" alt="Neutron-logo" src="https://github.com/user-attachments/assets/114843cc-baaf-417c-900d-17a7792430e8" />


Neutron is a free, open-source emulator for J2ME (Java 2 Platform, Micro Edition) applications. It allows you to run MIDP (Mobile Information Device Profile) applications and games on your desktop computer.

## Project Structure

This repository contains the source code for various Neutron modules, including:
*   `neutron-javase-swing`: The main desktop application (Swing-based).
*   `neutron-javase`: Core Java SE integration.
*   `neutron-midp`: MIDP API implementation.
*   `neutron-cldc`: CLDC API implementation.
*   `api`: Core API definitions.
*   And many more modules for different platforms and extensions.

## Building the Project

This version of Neutron uses Gradle as its build system.

### Prerequisites

*   **Java Development Kit (JDK):** Version 8 or higher is recommended.
*   **Gradle:** The project includes a Gradle Wrapper, so you don't need to install Gradle globally.

### Build Steps

1.  **Clone the repository (if you haven't already):**
    ```bash
    git clone https://github.com/nsomatrix/neutron.git
    ```

2.  **Build the entire project:**
    To compile all modules and generate their respective JAR files, run the following command from the project root directory:
    ```bash
    ./gradlew build
    ```
    This command will download necessary dependencies, compile the source code, and package the modules.

3.  **Run only the desktop application JAR:**
    If you only need the runnable desktop application :
    ```bash
    ./gradlew run
    ```

## Running the Desktop Application

After a successful build, you can find the runnable JAR for the desktop application.

### Location of the Runnable JAR

The main runnable JAR for the Swing-based desktop application is located at:
`neutron/neutron-javase-swing/build/libs/neutron-javase-swing-1.0.jar`

### Execution

To run the Neutron desktop application, use the `java -jar` command:
```bash
java -jar neutron/neutron-javase-swing/build/libs/neutron-javase-swing-1.0.jar
```
This will launch the Neutron GUI, allowing you to load and run J2ME applications.

## Resources

*   **Source Code:** [https://github.com/nsomatrix/neutron](https://github.com/nsomatrix/neutron)
*   **Original Code:** [https://github.com/barteo/neutron](https://github.com/barteo/neutron)


## Contributing

Contributions are welcome! Please refer to the project's guidelines (if any) for contributing.
