package com.greghaskins.spectrum.internal.junit;

import org.junit.runners.model.FrameworkMethod;

/**
 * Provide an framework method for JUnit to use as a stub for its method rules.
 */
public interface StubJUnitFrameworkMethod {
  class Stub {
    public void method() {}
  }

  /**
   * Provide empty stub.
   * @return stub of nothing.
   */
  static FrameworkMethod stubFrameworkMethod() {
    return stubFrameworkMethod(Stub.class, "method");
  }

  /**
   * Arbitrary stub.
   * @param klazz class on which to find the method
   * @param methodName method to find
   * @return framework method wrapping the given method
   */
  static FrameworkMethod stubFrameworkMethod(Class<?> klazz, String methodName) {
    try {
      return new FrameworkMethod(klazz.getMethod(methodName));
    } catch (NoSuchMethodException noSuchMethod) {
      throw new RuntimeException("Could not reach method", noSuchMethod);
    }
  }

}
