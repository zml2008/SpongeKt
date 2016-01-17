/**
 * SpongeKt
 * Copyright (C) zml and SpongeKt contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.aoeu.spongekt

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.text.format.TextColors

/**
 * Minor glue to allow Kotlin-style type-save builders to be used with [CommandSpec] instances.
 */

/**
 * Create a [CommandSpec] within a builder function.
 *
 * @sample exampleCommand
 */
fun command(init: CommandSpec.Builder.() -> Unit): CommandSpec {
    return CommandSpec.builder().apply(init).build();
}

/**
 * Add a child [CommandSpec] with the specified name
 *
 * @param names The aliases to make the child available under
 * @param init The function that will populate a child with data
 */
fun CommandSpec.Builder.child(vararg names: String, init: CommandSpec.Builder.() -> Unit): CommandSpec.Builder {
    child(command(init), *names);
    return this
}

/**
 * Helper class for [registerAll], do not use on its own
 */
class RegisterMultiple internal constructor(private val mgr: CommandManager, private val plugin: Any) {
    fun command(vararg names: String, init: CommandSpec.Builder.() -> Unit) {
        mgr.register(plugin, command(init), *names)
    }
}

/**
 * Register multiple commands to the same plugin without having to specify the plugin each time
 *
 * @sample exampleRegisterAll
 * @param plugin The plugin to register to
 */
fun CommandManager.registerAll(plugin: Any, init: RegisterMultiple.() -> Unit) {
    val rm = RegisterMultiple(this, plugin)
    rm.init()
}

/**
 * Create and register a single command
 */
fun CommandManager.register(plugin: Any, vararg names: String, init: CommandSpec.Builder.() -> Unit) {
    register(plugin, command(init), *names)
}

// -- Examples for documentation

private val myPlugin = RegisterMultiple::class.java // Dummy value

private fun exampleRegisterAll() {
    val mgr = Sponge.getCommandManager()
    mgr.registerAll(myPlugin) {
        command("test") {
            // [...]
        }
        command("another") {
            // [...]
        }
    }
}

/**
 * Create a simple command with common data.
 * This example also uses the operator overload shorthand to create a Text builder.
 *
 * @example command
 */
private fun exampleCommand() {
    val spec = command {
        description(+"A description")
        permission("run.some.command")
        arguments(string(+"arg"), integer(+"another"))
        executor { src, args ->
            val quantity = args.getOne<Int>("another").orElse(1)
            val name = args.getOne<String>("arg").get()

            src.sendMessage((-"You have received ")
                    .append(+quantity.toString(), +" ", +if (quantity == 1) { name } else { name + "s" })
                    .color(TextColors.AQUA).build())
            CommandResult.success()
        }
    }
}

