package com.greghaskins.spectrum;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * If the number of <code>get</code> calls when using a {@link java.util.function.Supplier}
 * makes the test code harder to read, you can use {@link Unboxer#unbox} to create
 * a proxy object that masquerades as an interface to the item in the Supplier. This
 * can be used with <code>let</code> and {@link Variable}
 */
public interface Unboxer {
  /**
   * Provide a proxy object to the contents of a supplier to reduce the number of
   * {@link Supplier#get} calls in your code. Note, if using {@link Variable} then you
   * will want to keep a reference to the original object so you can use {@link Variable#set}.
   * @param supplier supplier of object
   * @param asClass target type of the unboxer - must be interface
   * @param <T> type within the supplier
   * @param <R> (inferred) type of the unboxer object (allows generic types to be preserved)
   * @param <S> class of the interface to return
   * @return a proxy to the contents of the supplier
   */
  @SuppressWarnings("unchecked")
  static <T extends S, R extends S, S> R unbox(Supplier<T> supplier, Class<S> asClass) {
    return (R) Proxy.newProxyInstance(asClass.getClassLoader(), new Class<?>[] {asClass},
        (Object proxy, Method method, Object[] args) -> method.invoke(supplier.get(), args));
  }
}
