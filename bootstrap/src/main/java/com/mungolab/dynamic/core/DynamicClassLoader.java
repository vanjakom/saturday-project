package com.mungolab.dynamic.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class DynamicClassLoader extends java.lang.ClassLoader {
    private final List<String> repositories;

    public DynamicClassLoader(List<String> repositories) {
        this.repositories = repositories;

        System.out.println("Parent classloader: " + this.getParent().getClass());
    }

    /*
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("Loading class: " + name);
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("Loading class: " + name + ", resolve: " + resolve);
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        System.out.println("Loading resource: " + name);

        return super.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        System.out.println("Loading resource as stream: " + name);

        return super.getResourceAsStream(name);
    }
    */

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("Finding class: " + name);

        try {
            String path = findClassPath(name);
            if (path != null) {
                InputStream inputStream = new FileInputStream(path);
                byte[] bytes = IOUtils.toByteArray(inputStream);

                return defineClass(name, bytes, 0, bytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Unable to find class: " + name);
        return null;
    }

    @Override
    protected URL findResource(String subpath) {
        System.out.println("Finding resource: " + subpath);

        String path = findResourcePath(subpath);
        if (path != null) {
            try {
                return new URL("file://" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Unable to find resource: " + subpath);
        return null;
    }

    private String findClassPath(String name) {
        for (String repository: repositories) {
            String possiblePath = createClassPath(repository, name);
            if (new File(possiblePath).exists()) {
                return possiblePath;
            }
        }

        return null;
    }

    private String createClassPath(String repository, String name) {
        return repository + "/" + name.replace(".", "/") + ".class";
    }

    private String findResourcePath(String subpath) {
        for (String repository: repositories) {
            String possiblePath = repository + "/" + subpath;
            if (new File(possiblePath).exists()) {
                return possiblePath;
            }
        }

        return null;
    }
}
