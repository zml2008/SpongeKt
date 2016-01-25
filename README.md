SpongeKt is a set of extension methods and utilities to make using [Sponge] easier for [Kotlin] plugins.

The [sponge-kotlin-plugin-archetype] exists in association with this project, as a template to help create Kotlin-language plugins.

# Components

## Services

SpongeKt provides a property delegate for services, `ServiceProperty`, that will track the currently registered service of the specified type.

```kotlin
@Plugin([...])
class MyPlugin {
    val aService: PermissionService by ServiceProperty(this)
}
```

Notice that this is an immutable `val` property. If this were to be a `var` property, setting a value would update the service stored in the game's `ServiceManager` owned by the plugin.

## Commands

Kotlin's type-safe builders can be used to build command descriptions. There are several ways to go about this.

The `command` method simply creates a `CommandSpec`. Additionally, to directly register commands, the builder methods `register(plugin: Any, vararg names: String)` and `registerAll(plugin: Any)` are available.

```kotlin
@Plugin([...])
class MyPlugin @Inject constructor(val game: Game) {
    @Listener
    fun registerCommands(event: GameInitializationEvent) {
        game.commandManager.registerAll(this) {
            command("test") {
                description(+"merp")
                // [...]
            }
            command("meow") {
                description(+"fuzz")
                executor { src, ctx -> 
                    src.sendMessage("meow".color(TextColors.GOLD))
                    CommandResult.success()
                }
            }
        }
    }
}
```

## Text

Extension methods and operator overloading allow for convenience methods that ease working with texts.

The `+` and `-` prefix unary operators are overloaded on Strings to convert to a `Text` and `Text.Builder` respectively. The infix addition operator is overloaded for `String + Text` and `Text + Text` by returning a parent text, meaning each component maintains its own style. For `Text + String`, the operator is overloaded as well, but unlike the other operators the `String` is appended to the `Text`'s list of children, so formatting is maintained. This is open to change based on feedback. Additionally, all the formatting methods (`style`, `color`, etc) are available on `String`s as extension methods, returning `Text`s with the specific formatting operations.

These overloads are designed to make simple operations shorter without being too opinionated or requiring a lot of maintenance. For any more complicated operations, it is probably more efficient to work with `Text.Builder`s directly. Plugins may also want to define their own overloaded methods for formatting based on purpose of messages, for example `fun String.error(): Text` which would create red-colored `Text`, or `fun String.link(url: URL): Text` which would create an appropriately formatted link. These formatting operations are currently outside the scope of SpongeKt, but as more experience is gained from people developing Kotlin-language plugins about what operations are most useful, some may be added to the library.

# Building

We use Maven to build, so the command to build is `mvn clean install`. The built jar is located in `target/`

To use SpongeKt in a plugin, releases are deployed to the PEX Maven repository at https://pex-repo.aoeu.xyz. The Maven dependency specification is:

```xml
<dependency>
    <groupId>xyz.aoeu</groupId>
    <artifactId>spongekt</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

And for Gradle:

```groovy

compile 'xyz.aoeu:spongekt:1.0-SNAPSHOT'
```



# Expansion and Contributions

SpongeKt is in its early stages -- not a lot of plugins have been written in Kotlin yet, Kotlin is a new language, and Sponge is a new API. This library is very open to suggestions on what to add to make life easier for plugin developers. Pull requests and feature requests are encouraged by anyone writing Kotlin plugins.

[Sponge]: https://spongepowered.org
[Kotlin]: https://kotlinlang.org
[sponge-kotlin-plugin-archetype]: https://github.com/zml2008/sponge-kotlin-plugin-archetype
