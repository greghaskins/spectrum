package com.greghaskins.spectrum.internal.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import com.greghaskins.spectrum.internal.junit.RunNotifierReporting;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class RunNotifierReportingTest {
  @Test
  public void equalsVariants() {
    // really to satisfy code coverage here
    RunNotifierReporting.FailureWrapper wrapper = new RunNotifierReporting.FailureWrapper(null);

    // all the edge cases for equals, right here
    assertEquals(wrapper, wrapper);
    assertNotEquals(wrapper, null);
    // here to force a branch in FailureWrapper.equals
    assertFalse(wrapper.equals(null));
    assertFalse(wrapper.equals("Hello"));
  }

  @Test
  public void variationsOfFailureComparison() {
    Description desc1 = Description.createSuiteDescription("A");
    Description desc2 = Description.createSuiteDescription("B");
    Throwable exc1 = new RuntimeException("A");
    Throwable exc2 = new RuntimeException("B");

    assertEquals(new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc1)),
        new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc1)));

    assertNotEquals(new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc1)),
        new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc2)));

    assertNotEquals(new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc1)),
        new RunNotifierReporting.FailureWrapper(new Failure(desc2, exc1)));

    assertNotEquals(new RunNotifierReporting.FailureWrapper(new Failure(desc1, exc1)),
        new RunNotifierReporting.FailureWrapper(new Failure(desc2, exc2)));
  }

}
