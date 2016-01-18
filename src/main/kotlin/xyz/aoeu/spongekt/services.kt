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

import com.google.common.collect.Sets
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.service.ChangeServiceProviderEvent
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * A delegate for service-backed properties. Setting a value to this property will register that value as its service
 */
class ServiceProperty<T: Any>(internal val plugin: Any) {
    internal var serviceRef: T? = null
    internal var serviceType: Class<T>? = null;

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        updateType(property.returnType)

        if (serviceRef == null) {
            serviceRef = Sponge.getGame().serviceManager.provide(serviceType!!).get();
        }

        return serviceRef!!
    }

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
                registerListener(this)
            } else {
                throw IllegalArgumentException("Services with generic types are not supported")
            }
        }
        return serviceType!!
    }
}

// -- Internal junk to prevent holding references to service instances

private val listenerTracker: ConcurrentMap<Any, PluginListenerHolder> = ConcurrentHashMap()

private fun registerListener(prop: ServiceProperty<*>) {
    var holder = listenerTracker.get(prop.plugin);
    if (holder == null) {
        holder = PluginListenerHolder()
        val existing = listenerTracker.putIfAbsent(prop.plugin, holder)
        if (existing != null) {
            holder = existing;
        } else {
            Sponge.getGame().eventManager.registerListeners(prop.plugin, holder)
        }
    }
    holder.listeners.add(WeakReference(prop))
}

private class PluginListenerHolder {
    val listeners: MutableSet<WeakReference<ServiceProperty<*>>> = Sets.newConcurrentHashSet()

    @Listener
    fun onServiceChange(event: ChangeServiceProviderEvent) {
        val it = listeners.iterator();
        while (it.hasNext()) {
            val prop = it.next().get();
            if (prop == null) {
                it.remove()
                continue
            }
            prop.onServiceChange(event)
        }
    }
}
