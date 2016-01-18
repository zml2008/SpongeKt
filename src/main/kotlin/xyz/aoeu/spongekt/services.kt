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
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.service.ChangeServiceProviderEvent
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * A delegate for service-backed properties. Setting a value to this property will register that value as its service
 */
class ServiceProperty<T: Any>(val plugin: Any) {
    private var serviceRef: T? = null
    private var serviceType: Class<T>? = null;

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        updateType(property.returnType)

        if (serviceRef == null) {
            serviceRef = Sponge.getGame().serviceManager.provide(serviceType!!).get();
        }

        return serviceRef!!
    }

    @Listener
    fun onServiceChange(event: ChangeServiceProviderEvent) {
        val type = serviceType;
        val service = event.newProvider
        if (type != null && type.isInstance(service)) {
            serviceRef = type.cast(service);
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Sponge.getGame().serviceManager.setProvider(plugin, updateType(property.returnType), value)
    }

    private fun updateType(type: KType): Class<T> {
        if (serviceType == null) {
            val returnType = type.javaType;
            if (returnType is Class<*>) {
                serviceType = returnType as Class<T>;
                Sponge.getGame().eventManager.registerListeners(plugin, this)
            } else {
                throw IllegalArgumentException("Services with generic types are not supported")
            }
        }
        return serviceType!!
    }
}
