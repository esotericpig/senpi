# senpi
pi in base 12 with custom-made Mutable/Immutable Big Integer classes for use with (almost) any base.

The idea was taken from Kryon channelings by channeler Lee Carroll.

The "senpi" name comes from seeing a funny "Notice me, Senpai" meme with the pi symbol.

## setup
This project uses Gradle for the build process.  If you already have Gradle installed, ignore this section.

Graddle Wrapper is included so that you don't have to install Gradle.  Simply run one of the following commands:
- **./gradlew** (Unix-like systems: Linux and Mac OS X)
- **./gradlew.bat** (Windows)

(For Unix-like systems, you may have to "**chmod +x ./gradlew**" or simply do "**bash gradlew**".)

Make sure that you have Java installed (with potentially **JAVA_HOME** set correctly).

Instead, you can also move **gradle-wrapper.jar** and **gradle-wrapper.properties** from **./gradle/wrapper** to the top directory and simply run them with the following command if you wish:
- **java -jar ./gradle-wrapper.jar**

For safety, **distributionSha256Sum** has been added to **gradle-wrapper.properties**.

## uninstall
If you used Gradle Wrapper, any downloaded files will be stored in **$USER_HOME/.gradle/wrapper/dists**, so delete this directory afterwards.

## use
- **gradle tasks**
- **gradle clean**
- **gradle test**
- **gradle -q run**
- **gradle javadoc**
- **gradle jar**
