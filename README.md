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

## license
senpi <https://github.com/esotericpig/senpi>
Copyright (c) 2016-2017 Jonathan Bradley Whited (@esotericpig)

senpi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

senpi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with senpi.  If not, see <http://www.gnu.org/licenses/>.
