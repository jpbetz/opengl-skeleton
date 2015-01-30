Install Java 8

Setup:

  gradle unpackNatives

Run:

  gradle run

To Run from intelliJ,  set the "VM Options" for any Run/Debug Configuration to:

  -Djava.library.path=build/natives/osx

