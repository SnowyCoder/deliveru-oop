# DeliverU
Delivery manager, project required for the exam of the "Programmazione a Oggetti"
course at UniMoRe.

## Description
The program has two access modes: admin and user, the users can create deliveries
and view what they have previously created. The admin account can view all the
submitted deliveries, and it can change its state only if the automatic delivery
is disabled. If it's enabled then he can only remove packets that can no longer
change (ex. received packets).

For more information check USER_MANUAL.md

## Implementation notes
The project is written in Java 11, mostly because it's the version required by
the only supported JavaFX version, not many of the newer features are used but
still a JVM with version 11+ is required for both compilation and runtime.

Most of the UI is controlled trough JavaFX' Beans and Properties, that use a
declarative paradigm to signal data changes. This choice is also reflected in
the data storage system, it has the obvious benefits of being simple to use, but
it makes the code not really versatile. In a real-world program the declarative
paradigm should be interfaced to a database (with some glue code for the change
signaling).
Due to this the async part of the program will only compute the changes to make
in another thread, running the code to commit these changes in the UI thread.

For more details and an overview of the code check the javadoc.

## External libraries
This project uses maven as the dependency manager, so you can find all the
used dependency in the pom.xml file
### Jackson
Serializes and deserializes the program data from/to JSON

### AppDirs
Finds save directories in an OS-independent way

### Log4j
Manages the logs

### Lombok
Shortens a lot of java constructs (getters, setters, constructors).
Unfortunately due to some technical details involving maven javadoc generation
all the source code needs to be located in src/main/lombok instead of
src/main/java for the annotation preprocessor to work correctly.

### JavaFx
An UI library, a more modern alternative to Java Swing.

### EasyBind
A lightweight library that uses some of the Java 8 functional features to
give a more pleasant and performant interface to the JavaFX Bindings

## Compilation
This project uses maven as its dependency manager, to run the code you have
to install maven and run `mvn package` in the main project directory, this will
download all the necessary dependencies and compile the project.
The generated executable fat-jar will be available in `target/deliveru.jar`.

You can run it with `java -jar target/deliveru.jar`

## Javadoc Generation
Install maven and run `mvn javadoc:javadoc`, the generated HTML page can be
found in `target/site/apidocs`
