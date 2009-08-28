#!/bin/sh

# starts server
start()
{
	java -jar "$M2_REPOSITORY/org/seleniumhq/selenium/server/selenium-server/$VERSION/selenium-server-$VERSION-standalone.jar" 
}

# ----- Execute the commands -----------------------------------------

VERSION=$1
if [ "$VERSION" == "" ]; then
VERSION="1.0.1"
fi

start $VERSION

exit 1

