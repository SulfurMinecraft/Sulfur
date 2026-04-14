package dev.sulfurmc.Sulfur.Utils.Listeners;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.trait.CancellableEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class Listener {
    private static final GlobalEventHandler geh = MinecraftServer.getGlobalEventHandler();
    private static final Map<Class<? extends net.minestom.server.event.Event>, List<E>> registered = new ConcurrentHashMap<>();
    private static final List<EventListener<? extends net.minestom.server.event.Event>> listeners = new ArrayList<>();

    public Listener() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Event.class) && method.getParameterCount() == 1) {
                Class<? extends net.minestom.server.event.Event> type = (Class<? extends net.minestom.server.event.Event>) method.getParameterTypes()[0];
                Event annotation = method.getAnnotation(Event.class);
                E listener = new E(annotation.priority(), annotation.ignoreCancelled(), method, this);

                List<E> listeners = registered.computeIfAbsent(type, t -> {
                    EventListener<? extends net.minestom.server.event.Event> listen = EventListener.builder(t)
                            .handler(event -> {
                                for (E e : registered.getOrDefault(t, new ArrayList<>())) {
                                    try {
                                        if (event instanceof CancellableEvent ce && ce.isCancelled() && !e.ignoreCancelled) continue;
                                        e.method().invoke(e.l(), event);
                                    } catch (IllegalAccessException | InvocationTargetException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            })
                            .build();
                    geh.addListener(listen);
                    Listener.listeners.add(listen);
                    return new ArrayList<>();
                });
                listeners.add(listener);
                listeners.sort(Comparator.comparingInt(E::priority));
            }
        }
    }

    public static void unregisterAll() {
        listeners.forEach(geh::removeListener);
        listeners.clear();
        registered.clear();
    }

    private record E(int priority, boolean ignoreCancelled, Method method, Listener l) {}
}