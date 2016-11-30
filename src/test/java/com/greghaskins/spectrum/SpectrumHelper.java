package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;

public class SpectrumHelper {

  public static Result run(final Class<?> specClass) throws Exception {
    return runWithJUnit(new Spectrum(specClass));
  }

  public static Result run(final Block block) {
    return runWithJUnit(
        new Spectrum(Description.createSuiteDescription(block.getClass()), block));
  }

  private static Result runWithJUnit(final Runner runner) {
    return new JUnitCore().run(Request.runner(runner));
  }

}
