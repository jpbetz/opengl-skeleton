Subspace
========

Lightweight vector and matrix library for graphics programming in Scala.

This is a small collection of vector and matrix classes written specifically for graphics programmers working with a
modern graphics pipeline in Scala.  It contains classes such as Vector3, Matrix4x4 and Quaternion.

To minimize it's footprint, this library has no dependencies.

While this library is intended for graphics programming,  it must be paired with a graphics API such as OpenGL to be of
much practical use.  It does not include graphics API bindings, model loaders, or a graphics/game engine.

OpenGL Programming in Scala
---------------------------

At the time this was written, LWJGL was the only library providing access the full OpenGL API from the JVM.

This library can be used with version 2 and 3 of LWJGL.  While LWJGL 2 provides a utility library with vector
and matrix classes, it is rather incomplete.  And in LWJGL 3, they are removing the utility library entirely.

To use this library with LWJGL,  simply build whatever types are needed and then use the toBuffer methods to produce the
ByteBuffers needed by LWJGL.  E.g.:

    val modelViewMatrix = Matrix4x4.forTranslationRotationScale(...)
    glUniformMatrix4("modelViewMatrix", false, modelViewMatrix.toBuffer)

Goals
-----
* Good at one thing.  Provide the vector and matrix types needed to program modern against a modern graphics pipeline, and nothing else.
* Consistent and complete.  Similar libraries for other languages have been studied to make sure all the convenience operations developers expect have been included.
* Scala idomatic. Immutable case classes for all vector and matrix types. Carefully defined operator overloading for natural looking mathematical expressions.
* Minimal footprint.  No dependencies.

Non-Goals
---------
* Provide Scala bindings for OpenGL
* Write a graphics/game engine
* Write a general purpose linear algebra library

Current Limitations
-------------------
* All types are currently reference types.  This has negative performance implications.  While scala does allow
  stack allocated value types to be defined by extending AnyVal,  AnyVal can only be used for single field types, not
  multi field types like Vector2.  It is not clear if Scala will ever support stack allocated value types that could
  be used for my purposes.

References
----------
* https://github.com/ra4king/LWJGL-OpenGL-Utils/tree/master/src/com/ra4king/opengl/util/math
* http://developer.android.com/reference/android/opengl/Matrix.html
* http://docs.unity3d.com/ScriptReference/index.html
* https://github.com/mrdoob/three.js/tree/master/src/math


