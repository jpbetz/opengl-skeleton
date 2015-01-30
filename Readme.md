A skeleton gradle project for full access to OpenGL on the JVM.  It simply sets up a dependency to
[LWJGL](http://www.lwjgl.org/),  the same OpenGL bindings used by Minecraft.

The main thing this skeleton does is use [gradle-natives plugin](https://github.com/cjstehno/gradle-natives) to load
[LWJGL](http://www.lwjgl.org/) native binaries for the correct platform.

Unlike most other Java 3d style APIs,  LWJGL gives direct, low level, access to all OpenGL APIs,  including 4.5.  It
also includes bindings to related specifications such as OpenCL.

An HelloWorld class using [LWJGL](http://www.lwjgl.org/) is included to display a minimal OpenGL window.

Dependencies
------------

* Java 8 (Java 6+ may work, have not tested)
* Gradle 2.2.1+ (older may work, have not tested)
* Platforms supported are: 'osx', 'linux', 'windows'

Usage
-----

Setup the native OpenGL bindings by running:

    gradle unpackNatives

To run the app from the command line, modify the run task in build.gradle to use your platform (osx is the default),  then execute:

    gradle run

To Run from intelliJ,  set the "VM Options" for any Run/Debug Configuration to:

    -Djava.library.path=build/natives/osx

Replace 'osx' with the appropriate platform if needed.

TODO
====
[ ] Upgrade to [LWJGL 3](http://www.lwjgl.org/download) once it is available in maven central (see release section on the Download page).