package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.List;

public class SpectrumHelper {

  public static class RecordingListener extends RunListener {
    private List<Description> testsStarted = new ArrayList<>();

    @Override
    public void testStarted(Description description) throws Exception {
      super.testStarted(description);
      testsStarted.add(description);
    }

    public List<Description> getTestsStarted() {
      return testsStarted;
    }
  }

  public static Result run(final Class<?> specClass) throws Exception {
    return runWithJUnit(new Spectrum(specClass));
  }

  public static Result run(final Block block) {
    return runWithJUnit(
        new Spectrum(Description.createSuiteDescription(block.getClass()), block));
  }

  /**
   * Allows a listener to listen to a run.
   * @param specClass the class to execute via Spectrum
   * @param listener the listener to use
   * @param <T> type of listener
   * @return the listener for fluent usage
   * @throws Exception on error
   */
  public static <T extends RunListener> T runWithListener(final Class<?> specClass,
      final T listener) throws Exception {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(listener);
    new Spectrum(specClass).run(notifier);

    return listener;
  }

  private static Result runWithJUnit(final Runner runner) {
    return new JUnitCore().run(Request.runner(runner));
  }

}
