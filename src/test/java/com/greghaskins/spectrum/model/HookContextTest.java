package com.greghaskins.spectrum.model;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

/**
 * Tests for the {@link HookContext} class. Its comparison
 * algorithm is important enough to need testing.
 */
@RunWith(Spectrum.class)
public class HookContextTest {
  {
    describe("Hook context", () -> {
      it("can never consider two hook contexts equal", () -> {
        HookContext one = new HookContext(a -> {
        },
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(a -> {
        },
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertThat(one.compareTo(two), not(is(0)));
      });

      it("considers the later created one to be less important", () -> {
        HookContext one = new HookContext(a -> {
        },
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(a -> {
        },
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertFirstIsMoreImportantThanSecond(one, two);
      });

      it("considers a higher level precedence to be more important"
          + " than a sequence number", () -> {
            HookContext one = new HookContext(a -> {
            },
                0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.OUTER);
            HookContext two = new HookContext(a -> {
            },
                0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
            assertFirstIsMoreImportantThanSecond(two, one);
          });

      it("considers a lower depth to be more important", () -> {
        HookContext one = new HookContext(a -> {
        },
            1, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(a -> {
        },
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertFirstIsMoreImportantThanSecond(two, one);
      });
    });

  }

  private static void assertFirstIsMoreImportantThanSecond(HookContext first, HookContext second) {
    assertThat(first.compareTo(second), greaterThan(0));
  }

}
