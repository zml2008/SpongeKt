SpongeKt is a set of extension methods and utilities to make using [Sponge] easier for [Kotlin] plugins.

The [sponge-kotlin-plugin-archetype] exists in association with this project, as a template to help create Kotlin-language plugins.

Components
----------

### Services

SpongeKt provides a property delegate for services, `ServiceProperty`.

```kotlin
@Plugin([...])
class MyPlugin {
    val aService: PermissionService by ServiceProperty(this)
}
```

Notice that this is an immutable `val` property. If this were to be a `var` property, setting a value would update the service stored in the game's `ServiceManager` owned by the plugin.

### Commands

Kotlin's type-safe builders can be used to build command descriptions. There are several ways to go about this.

The `command` method simply creates a `CommandSpec`. Additionally, to directly register commands, the builder methods `register(plugin: Any, vararg names: String)` and `registerAll(plugin: Any)` are available.

```kotlin
@Plugin([...])
class MyPlugin @Inject constructor(val game: Game) {
    fun registerCommands() {
        game.commandManager.registerAll(this) {
            command("test") {
                description(+"merp")
                // [...]
            }
            command("meow") {
                description(+"fuzz")
                executor { src, ctx -> 
                    src.sendMessage((-"meow").color(TextColors.GOLD).build())
                    CommandResult.success()
                }
            }
        }
    }
}
```

### Text

Extension methods and operator overloading allow for convenience methods that ease working with texts.

The `+` and `-` prefix unary operators are overridden on Strings to convert to a `Text` and `Text.Builder` respectively.
More overrides may come as they are found useful.

## Building

We use Maven to build, so the command to build is `mvn clean install`. The built jar is located in `target/`

[Sponge]: https://spongepowered.org
[Kotlin]: https://kotlinlang.org
[sponge-kotlin-plugin-archetype]: https://github.com/zml2008/sponge-kotlin-plugin-archetype
