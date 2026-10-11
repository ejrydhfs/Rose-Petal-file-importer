@echo off
set classpath=./build/rosepetal.jar;%classpath%
set classpath=./lib/nsuml/nsuml.jar;%classpath%
set classpath=./lib/nsuml/xerces.jar;%classpath%
set classpath=./lib/nsuml/jdom.jar;%classpath%
@echo on
java -classpath %CLASSPATH% net.sf.rosepetal.nsuml.PetalToUml %1 %2
