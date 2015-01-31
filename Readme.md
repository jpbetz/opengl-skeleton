A gradle skeleton project for full access to OpenGL on the JVM.  The main thing this skeleton does is set up a build
dependency to [LWJGL](http://www.lwjgl.org/),  the same OpenGL bindings used by Minecraft.  This dependency is not
entirely trivial because LWJGL uses JNI.

SBT users,  see [sbt-lwjgl-plugin](https://github.com/philcali/sbt-lwjgl-plugin).

[gradle-natives plugin](https://github.com/cjstehno/gradle-natives) is used to load
LWJGL native binaries for the correct platform.  This was documented fairly well on the web
but I wanted an working skeleton project that has everything in one place.

This is a great starting point for anyone wanting to work directly with OpenGL who is most familiar with a JVM language
(Java, Scala, Clojure, Groovy, ...) because unlike most other Java 3d style APIs,  LWJGL gives direct, low level, access
to all OpenGL APIs,  including 4.5.  It would, for example, go great with Jason L. McKesson's
[Learning Modern 3D Graphics Programming](http://www.arcsynthesis.org/gltut/).

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