# senpi
pi in base 12 with custom-made Big Integer/Decimal classes for use with (almost) any base.

The idea was taken from Kryon channelings by channeler Lee Carroll.

The "senpi" name comes from seeing a funny "Notice me, Senpai" meme with the pi symbol.

## TOC
- [Setup](#setup)
- [Setup Notes](#setup-notes)
- [Uninstall](#uninstall)
- [Use](#use)
- [License](#license)

## Setup
This project uses Gradle for the build process.  If you already have Gradle installed, ignore this section.

Graddle Wrapper is included so that you don't have to install Gradle.  Simply run one of the following commands:
- `./gradlew` (Unix-like systems: Linux and Mac OS X)
- `./gradlew.bat` (Windows)

(For Unix-like systems, you may have to `chmod +x ./gradlew` or simply do `bash gradlew`.)

Make sure that you have Java installed (with potentially `JAVA_HOME` set correctly).

Instead, you can also move `gradle-wrapper.jar` and `gradle-wrapper.properties` from `./gradle/wrapper` to the top directory and simply run them with the following command if you wish:
- `java -jar ./gradle-wrapper.jar`

For safety, `distributionSha256Sum` has been added to `gradle-wrapper.properties`.

## Setup Notes
By default, Gradle now runs a Daemon.  You can stop it by using this command:
- `./gradlew(.bat) --stop`

To disable the Daemon, add the following to `$USER_HOME/.gradle/gradle.properties` (or create the file):
- `org.gradle.daemon=false`

## Uninstall
If you used Gradle Wrapper, do the following:
1. Stop the Gradle Daemon if it is running:
   - `./gradlew(.bat) --stop`
2. Delete any files stored in here:
   - `$USER_HOME/.gradle/wrapper/dists`

## Use
Default tasks (gradle -q) are:
- `run`

Useful tasks:
- `gradle tasks`
- `gradle clean`
- `gradle build`
- `gradle test`
- `gradle -q run`
- `gradle javadoc`
- `gradle jar`
- `gradle distTar distZip`

You can exclude a task using `-x`.  This is useful for long and/or annoying tasks, like `test`, or for debugging:
- `gradle -x test clean build run`

## License
> senpi (https://github.com/esotericpig/senpi)  
> Copyright (c) 2016-2017 Jonathan Bradley Whited (@esotericpig)  
> 
> senpi is free software: you can redistribute it and/or modify  
> it under the terms of the GNU Lesser General Public License as published by  
> the Free Software Foundation, either version 3 of the License, or  
> (at your option) any later version.  
> 
> senpi is distributed in the hope that it will be useful,  
> but WITHOUT ANY WARRANTY; without even the implied warranty of  
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
> GNU Lesser General Public License for more details.  
> 
> You should have received a copy of the GNU Lesser General Public License  
> along with senpi.  If not, see <http://www.gnu.org/licenses/>.  
