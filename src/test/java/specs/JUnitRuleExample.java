package specs;

import static com.greghaskins.spectrum.Configure.junitMixin;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class JUnitRuleExample {
  // mixins for the Spectrum native style of mixin
  public static class TempFolderRuleMixin {
    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();
  }

  public static class JUnitBeforeClassExample {
    public static String value;

    @BeforeClass
    public static void beforeClass() {
      value = "Hello world";
    }
  }

  // can also use native junit annotations
  private static String classValue;

  @BeforeClass
  public static void beforeClass() {
    classValue = "initialised";
  }

  {
    final Set<File> ruleProvidedFoldersSeen = new HashSet<>();
    describe("A spec with a rule mix-in", () -> {
      Supplier<TempFolderRuleMixin> tempFolderRuleMixin = junitMixin(TempFolderRuleMixin.class);

      it("has access to the rule-provided object at the top level", () -> {
        checkCanUseTempFolderAndRecordWhatItWas(ruleProvidedFoldersSeen, tempFolderRuleMixin);
      });

      describe("with a nested set of specs", () -> {
        it("can access the rule-provided object within the nested spec", () -> {
          checkCanUseTempFolderAndRecordWhatItWas(ruleProvidedFoldersSeen, tempFolderRuleMixin);
        });
        it("can access the rule-provided object over and over", () -> {
          checkCanUseTempFolderAndRecordWhatItWas(ruleProvidedFoldersSeen, tempFolderRuleMixin);
        });
      });

      it("has received a different instance of the rule-provided object each time", () -> {
        assertThat(ruleProvidedFoldersSeen.size(), is(3));
      });

      describe("with just a beforeClass mixin", () -> {
        Supplier<JUnitBeforeClassExample> mixin = junitMixin(JUnitBeforeClassExample.class);
        it("the mixin's before class has been called", () -> {
          assertThat(JUnitBeforeClassExample.value, is("Hello world"));
        });
      });

      it("has also initialised a class member owing to a local JUnit annotation", () -> {
        assertThat(classValue, is("initialised"));
      });
    });
  }

  private void checkCanUseTempFolderAndRecordWhatItWas(Set<File> filesSeen,
      Supplier<TempFolderRuleMixin> tempFolderRuleMixin) {
    assertNotNull(tempFolderRuleMixin.get().tempFolderRule.getRoot());
    filesSeen.add(tempFolderRuleMixin.get().tempFolderRule.getRoot());
  }
}
