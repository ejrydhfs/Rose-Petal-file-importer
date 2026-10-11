The project consists from following files.

src - sources of runtime classes
genast/src - sources of classes that are needed only for building the library 
      and which are not needed at runtime.
lib - jar files needed for compiling and running project
  * lib/build - jar files that are needed only during building sources
  * lib/nsuml - jar files that are needed only for nsuml 0.4.* input filter
model - model describing petal file. it is in mof1.3 format. It were saved
   using unisys bridge from rational rose.

This project aims to import UML diagrams made in Rational Rose's petals file format.
original at https://sourceforge.net/projects/rosepetal/
