A skeleton gradle project for OpenGL on the JVM.  Uses LWJGL,  the same OpenGL bindings used by Minecraft.

Uses the gradle-natives plugin to

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