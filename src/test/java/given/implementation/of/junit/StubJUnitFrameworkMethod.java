package given.implementation.of.junit;

import static com.greghaskins.spectrum.internal.junit.StubJUnitFrameworkMethod.stubFrameworkMethod;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

public class StubJUnitFrameworkMethod {
  @Test
  public void stubCanBeRetrievedAndUsed() throws Throwable {
    FrameworkMethod method = stubFrameworkMethod();
    method.invokeExplosively(
        new com.greghaskins.spectrum.internal.junit.StubJUnitFrameworkMethod.Stub());
  }

  @Test(expected = RuntimeException.class)
  public void cannotMakeFrameworkMethodOfJustAnything() throws Throwable {
    stubFrameworkMethod(
        com.greghaskins.spectrum.internal.junit.StubJUnitFrameworkMethod.Stub.class,
        "notrealmethod");
  }
}
