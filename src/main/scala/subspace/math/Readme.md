Subspace
========

Lightweight Scala vector and matrix library for graphics programming.

This is special purpose linear algebra library,  written specifically to have a minimal footprint.  It
contains the subset of types needed to by graphics programmers when working with a modern graphics pipeline:

* Vector2
* Vector3
* Vector4
* Matrix3x3
* Matrix4x4
* Quaternion
* ...

It does not include graphics API bindings, model loaders, or a graphics/game engine.

Using Subspace for OpenGL Programming in Scala
----------------------------------------------

At the time this was written, LWJGL was the only library providing full access the full OpenGL API from the JVM.

This library can be used with version 2 and 3 of LWJGL.  While LWJGL 2 provides a utility library with vector and matrix classes, it is rather
incomplete.  And in LWJGL 3, they are removing the utility library entirely.

To use this library with LWJGL,  simply build whatever types are needed and then use the toBuffer methods to produce the ByteBuffers
needed by LWJGL.  E.g.:


    glUniformMatrix4("xyzUniform", false, worldToViewMatrix.toBuffer)

Goals
-----
* Good at one thing.  Minimal footprint of code to provide the vector and matrix types needed to program modern against a modern graphics pipeline, and nothing else.
* Consistent and complete.  Similar libraries for other languages have been studied to make sure all the convenience operations developers expect have been included.
* Scala idomatic. Immutable case classes for all vector and matrix types. Carefully defined operator overloading for natural looking mathematical expressions.

Non-Goals
---------
* Provide Scala bindings for OpenGL
* Write a graphics/game engine
* Write a general purpose linear algebra library

References
----------
* https://github.com/ra4king/LWJGL-OpenGL-Utils/tree/master/src/com/ra4king/opengl/util/math
* http://developer.android.com/reference/android/opengl/Matrix.html
* http://docs.unity3d.com/ScriptReference/index.html
* https://github.com/mrdoob/three.js/tree/master/src/math


