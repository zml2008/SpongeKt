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

/**
 * Operator overloads and extensions for [String], [TextBuilder], and [Text] to allow simpler conversions.
 * Keep in mind that all extension methods for Text and String objects involve a roundtrip from a Text builder, so for more complicated operations
 * builder usage is preferred
 */

operator fun String.unaryPlus(): Text {
    return Text.of(this)
}

operator fun String.unaryMinus(): Text.Builder {
    return Text.builder(this)
}
