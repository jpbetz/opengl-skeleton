I'm testing out using OpenGL with Scala.

The main thing this skeleton does is:
* Set up a dependency to [LWJGL](http://www.lwjgl.org/),  the same OpenGL bindings used by Minecraft.  I currently am using LWJGL 2,  but may switch to LWJGL 3.  This dependency is not entirely trivial because LWJGL uses JNI.  I may switch to SBT at some point if I find an advantage to using it.  If so,  I'll likely start by explorint [sbt-lwjgl-plugin](https://github.com/philcali/sbt-lwjgl-plugin).
* Monkey patch in some vector, matrix and quaternion classes based on https://github.com/ra4king/LWJGL-OpenGL-Utils.  I will likely rewrite these classes as scala AnyVal classes,  but will need to upgrade to scala 2.12 first.
* Add some basic utility classes for managing openGL state.

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

* Java JDK 1.7.*
* Scala 2.11.*
* Gradle 2.2.1+ (older may work, have not tested)
* Platforms supported are: 'osx', 'linux', 'windows'

Usage
-----

Setup the native OpenGL bindings by running:

    gradle unpackNatives

To run the app from the command line, modify the run task in build.gradle to use your platform (osx is the default),  then execute:

    gradle run

To Run from intelliJ:

1. Generate an idea project:

    gradle idea

2. Change the the SDK to Java 1.8 in the IntelliJ "project settings".

3. Set the "VM Options" for any Run/Debug Configuration to:

    -Djava.library.path=build/natives/osx

Replace 'osx' with the appropriate platform if needed.

TODO
====
[ ] Upgrade to scala 2.12
[ ] Convert vector, matrix and quaternion class to scala.  Make them case classes that extend AnyVal so everything is stack allocated.
[ ] Test out using textures and UV texture coords.
[ ] Test out importing some astronomical data (e.g. sample of the milky way stars) and rendering a particle system with it.
[ ] Upgrade to [LWJGL 3](http://www.lwjgl.org/download) once it is available in maven central (see release section on the Download page).
[ ] Add support for [Collada](https://www.khronos.org/files/collada_spec_1_5.pdf) ?  Ugh,  3d file formats look awful.
