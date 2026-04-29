package dev.sulfurmc.Sulfur.Utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class PluginClassLoader extends URLClassLoader {

    private final List<ClassLoader> dependencies;

    public PluginClassLoader(URL[] urls, ClassLoader parent, List<ClassLoader> dependencies) {
        super(urls, parent);
        this.dependencies = dependencies != null ? dependencies : List.of();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> c = findLoadedClass(name);

        if (c == null) {

            try {
                c = findClass(name);
            } catch (ClassNotFoundException ignored) {}

            if (c == null) {
                for (ClassLoader dep : dependencies) {
                    try {
                        c = dep.loadClass(name);
                        break;
                    } catch (ClassNotFoundException ignored) {}
                }
            }

            if (c == null) {
                c = super.loadClass(name, false);
            }
        }

        if (resolve) resolveClass(c);
        return c;
    }
}