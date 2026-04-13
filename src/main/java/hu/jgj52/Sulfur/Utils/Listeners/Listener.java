package hu.jgj52.Sulfur.Utils.Listeners;

import net.minestom.server.MinecraftServer;
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

    public Listener() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Event.class) && method.getParameterCount() == 1) {
                Class<? extends net.minestom.server.event.Event> type = (Class<? extends net.minestom.server.event.Event>) method.getParameterTypes()[0];
                Event annotation = method.getAnnotation(Event.class);
                E listener = new E(annotation.priority(), annotation.ignoreCancelled(), method, this);

                List<E> listeners = registered.computeIfAbsent(type, t -> {
                    geh.addListener(t, event -> {
                        for (E e : registered.getOrDefault(t, new ArrayList<>())) {
                            try {
                                if (event instanceof CancellableEvent ce && ce.isCancelled() && !e.ignoreCancelled) continue;
                                e.method().invoke(e.l(), event);
                            } catch (IllegalAccessException | InvocationTargetException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    return new ArrayList<>();
                });
                listeners.add(listener);
                listeners.sort(Comparator.comparingInt(E::priority));
            }
        }
    }
    private record E(int priority, boolean ignoreCancelled, Method method, Listener l) {}
}