#!/bin/bash

# NanoContainer Booter script v @VER@
# www.nanocontainer.org

EXEC="$JAVA_HOME/bin/java -Djava.security.manager -Djava.security.policy=file:booter.policy -jar lib/nanocontainer-booter-@VER@.jar $@"
echo $EXEC
$EXEC

