JFLAGS = -cp ./../../lwjgl-2.9.3/jar/lwjgl.jar:./../../lwjgl-2.9.3/jar/lwjgl_util.jar:.
JLIBS = -Djava.library.path=./../../lwjgl-2.9.3/native/linux/x64
#JFLAGS = -cp ./../lwjgl-2.9.3/jar/lwjgl.jar:./../lwjgl-2.9.3/jar/lwjgl_util.jar:.
#JLIBS = -Djava.library.path=./../../lwjgl-2.9.3/native/windows/x64
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java



CLASSES = \
	RigidBody.java \
	PSphere.java \
	Planet.java \
	Star.java \
	SolarSystem.java \
	FinalProject.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	rm -f *.class
