#!/bin/sh

# starts server
start()
{
	java -jar "$HOME/.m2/repository/org/seleniumhq/selenium/server/selenium-server/$VERSION/selenium-server-$VERSION-standalone.jar" 
}

# ----- Execute the commands -----------------------------------------

VERSION=$1
if [ "$VERSION" != "" ]; then

  start $VERSION
      	
else

  echo "Usage: selenium-server.sh <version>"

fi

exit 1

