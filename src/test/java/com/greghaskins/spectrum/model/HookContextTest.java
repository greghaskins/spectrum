package com.greghaskins.spectrum.model;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.internal.hooks.Hook;
import com.greghaskins.spectrum.internal.hooks.HookContext;

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
        HookContext one = new HookContext(emptyHook(),
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(emptyHook(),
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertThat(one.compareTo(two), not(is(0)));
      });

      it("considers the later created one to be less important", () -> {
        HookContext one = new HookContext(emptyHook(),
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(emptyHook(),
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertFirstIsMoreImportantThanSecond(one, two);
      });

      it("considers a higher level precedence to be more important"
          + " than a sequence number", () -> {
            HookContext one = new HookContext(emptyHook(),
                0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.OUTER);
            HookContext two = new HookContext(emptyHook(),
                0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
            assertFirstIsMoreImportantThanSecond(two, one);
          });

      it("considers a lower depth to be more important", () -> {
        HookContext one = new HookContext(emptyHook(),
            1, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        HookContext two = new HookContext(emptyHook(),
            0, HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.ROOT);
        assertFirstIsMoreImportantThanSecond(two, one);
      });
    });

  }

  private Hook emptyHook() {
    return (description, notifier, block) -> {
    };
  }

  private static void assertFirstIsMoreImportantThanSecond(HookContext first, HookContext second) {
    assertThat(first.compareTo(second), greaterThan(0));
  }

}
