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
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType

/**
 * A delegate for service-backed properties. Setting a value to this property will register that value as its service
 */
class ServiceProperty<T: Any>(val plugin: Any) {
    private var serviceRef: T? = null
    private var serviceType: KClass<T>? = null;

    init {
        Sponge.getGame().eventManager.registerListeners(plugin, this)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (serviceType == null) {
            val returnType = property.returnType;
            if (returnType is KClass<*>) {
                serviceType = returnType as KClass<T>;
            } else {
                throw IllegalArgumentException("Services with generic types are not supported")
            }
        }

        if (serviceRef == null) {
            serviceRef = Sponge.getGame().serviceManager.provide(serviceType!!.java).get();
        }

        return serviceRef!!
    }

    @Listener
    fun onServiceChange(event: ChangeServiceProviderEvent) {
        val type = serviceType;
        val service = event.newProvider
        if (type != null && type.java.isInstance(service)) {
            serviceRef = type.java.cast(service);
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Sponge.getGame().serviceManager.setProvider(plugin, property.returnType.javaType as Class<T>, value)
    }
}
