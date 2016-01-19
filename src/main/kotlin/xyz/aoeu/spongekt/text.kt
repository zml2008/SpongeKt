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

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.format.TextStyle

/**
 * Operator overloads and extensions for [String], [TextBuilder], and [Text] to allow simpler conversions.
 * Keep in mind that all extension methods for Text and String objects involve a roundtrip from a Text builder, so for more complicated operations
 * builder usage is preferred
 */

// -- Simple conversion overloads
operator fun String.unaryPlus(): Text {
    return Text.of(this)
}

operator fun String.unaryMinus(): Text.Builder {
    return Text.builder(this)
}

// -- Provide Text operations on Strings
fun String.format(format: TextFormat): Text = Text.builder(this).format(format).build()
fun String.color(color: TextColor): Text = Text.builder(this).color(color).build()
fun String.style(vararg styles: TextStyle): Text = Text.builder(this).style(*styles).build();
fun String.onClick(clickAction: ClickAction<*>): Text = Text.builder(this).onClick(clickAction).build()
fun String.onHover(hoverAction: HoverAction<*>): Text = Text.builder(this).onHover(hoverAction).build()
fun String.onShiftClick(shiftClickAction: ShiftClickAction<*>): Text = Text.builder(this).onShiftClick(shiftClickAction).build()

// Allow combining Texts
operator fun String.plus(text: Text): Text {
    return Text.builder().append(+this, text).build()
}

operator fun Text.plus(text: Text): Text {
    return Text.builder().append(this, text).build()
}

operator fun Text.plus(text: String): Text {
    return Text.builder().append(this, +text).build();
}

// TODO: How to go on from here?
// There are some things that could be added, but I'm not sure if it makes sense to go the same way phroa has in providing named methods for
// TextColors and formatting. Instead, other thoughts:
// - Replicate all Text.Builder methods as extensions on String
// - Specific formatting options should be left to plugins to implement themselves?
// - Link formatting? Text.Builder.link(URL target)
// - Type-safe-style builders? How would this look when adding child elements? (having the unaryPlus extension method?
