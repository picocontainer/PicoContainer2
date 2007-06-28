This is an experimental attempt at a little tool that can identify where new objects are instantiated.
The goal is to run this over a project's source code to identify classes that are candidates
for Picofication.

We realise that object instantiation is not necessarily contradictory to DI desgign rules. Even hard core DI-designed systems will contain classes that instantiate a lot of objects, such as collections and even domain-specific objects.

In order to provide useful metrics, it is therefore essential that the user of the tool can specify what types of object instantiation we are looking for.

The goal is to be able to declare this during the invocation of PicoMeter. This can be done via ant:

<picometer destination="${picometer.report.dir}">
  <fileset dir="${srd.dir}"/>
  <classpath refid="compiled.classes"/>
  <include class-pattern="com.acme.*"/>
</picometer>

This tells PicoMeter only to look for instantiations in the com.acme package. It will generate a report highlighting all places in the source code where classes in this package are instantiated. And then the developers can start to think about strategies for how to refactor the classes that instantiate classes that could be injected by a service injector such as PicoContainer, Spring, HiveMind or Avalon.

Future functionality might be:
-Look for usage of classes that could have been interfaces
-Look for class instantiation usages (IntelliJ usage of ctor like functionality)