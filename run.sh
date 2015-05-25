#!/bin/bash

OS=linux
LIBPATH=-Djava.library.path=../../lwjgl-2.9.3/native/$OS/


cd src/SolarSystem
make clean
make
#java -cp ./../../lwjgl-2.9.3/jar/lwjgl.jar:./../../lwjgl-2.9.3/jar/lwjgl_util.jar:.  -Djava.library.path=../../lwjgl-2.9.3/native/$OS/ FinalProject
java -cp ./../../lwjgl-2.9.3/jar/lwjgl.jar:./../../lwjgl-2.9.3/jar/lwjgl_util.jar:. $LIBPATH FinalProject
cd ../..
