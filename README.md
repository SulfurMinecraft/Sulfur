# Sulfur
A [Minestom](https://minestom.net) based Minecraft server software

We'll write plugins for other features than the ones already in Sulfur.
\
This way, if you don't need crafting, you don't put in the crafting plugin.
\
This makes it easier to keep the server lightweight.

### Plugins
Sulfur supports plugins.
\
Here's an example:

**pom.xml**
```xml
<repositories>
    <repository>
        <id>jgj52-repo</id>
        <url>https://maven.jgj52.hu/repository/maven-releases/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>hu.jgj52</groupId>
        <artifactId>Sulfur</artifactId>
        <version>1.5</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
**src/main/resources/plugin.yml**
```yaml
main: your.group.pluginName.Main
name: YourPlugin
authors:
  - You
version: 1.0
```
**src/main/java/you/group/pluginName/Main.java**
```java
package you.group.pluginName;

import hu.jgj52.Sulfur.Utils.Plugin;

public class Main implements Plugin {
    @Override
    public void onEnable() {
        // do stuff
    }

    @Override
    public void onDisable() {
        // not necessary
    }
}
```

also render distance in server.yml does nothing yet