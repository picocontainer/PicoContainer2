== Build instructions ==

1) Put your openapi.jar here.
2) In your ~/build.properties, put something like this:
idea.plugins.dir=C:/IntelliJ-IDEA-4.0/plugins

== For developers ==

Copy the openapi.jar in DC's checkout manually:

pscp openapi.jar beaver.codehaus.org:/home/services/dcontrol/build/checkout/picoextras/picoidea/lib

Then login to beaver and do:
chmod o-r openapi.jar