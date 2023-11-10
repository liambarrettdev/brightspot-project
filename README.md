# Brightspot Project Template

## Local Dev Environment Setup
This is a standard Brightspot 3.x deployment. Versions for the various components are listed below. An 'x' signifies that the latest point release version should be used but an older version is fine too. A '^' signifies that at least that point release version **MUST** be used but a later version is fine too.
* Java 1.8.x
* Apache 2.2.x
* Tomcat 8.x
* Mysql 5.6.x
* Solr 4.8.1^

## Local Styleguide Development
NPM install only need to do once - ```npm install -g grunt-cli```

Run the styleguide - ```node/node node_modules/bsp-styleguide/bin/styleguide```

Grunt watch, any changes are picked up on the fly - ```target/bin/grunt watch```

Navigate to - http://localhost:3000

### Java
Java 8 is required to build and deploy your app as there are Java 8 specific features being used in the source code.

http://en.wikipedia.org/wiki/Java_version_history#Java_8_updates

### Apache
Not necessary for local environment deployments.

### Tomcat
Tomcat 8 is required to successfully run the project as we are using new features of the Servlet/JSP/EL specs deployed with this version.

http://tomcat.apache.org/whichversion.html


## Code Styles - Java
We use the built-in Dari code styles checker, which is based on the Sun/Oracle conventions (http://www.oracle.com/technetwork/java/codeconvtoc-136057.html) and is in the process of being refactored to use the Google conventions (http://google.github.io/styleguide/javaguide.html)

To that end, when building (full compile) locally, make sure to include the appropriate flags to invoke the styles checker, e.g.:

```mvn clean -P library verify```
