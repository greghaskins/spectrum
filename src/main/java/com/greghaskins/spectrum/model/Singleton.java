package com.greghaskins.spectrum.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The singleton pattern. An abstraction of a single object which can be created and
 * destroyed. This is a twist on the usual singleton pattern, in that normally the
 * singleton stays around indefinitely. This version is also thread safe.
 */
public class Singleton<T> implements Supplier<T> {
  private ConcurrentHashMap<Thread, T> objects = new ConcurrentHashMap<>();

  @Override
  public T get() {
    return objects.get(Thread.currentThread());
  }

  public void clear() {
    objects.remove(Thread.currentThread());
  }

  public void set(T object) {
    objects.put(Thread.currentThread(), object);
  }
}
