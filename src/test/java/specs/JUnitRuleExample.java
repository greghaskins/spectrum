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
  public interface JUnitTempFolderInterface {
    TemporaryFolder getTempFolder();
  }

  // mixins for the Spectrum native style of mixin
  public static class TempFolderRuleMixin implements JUnitTempFolderInterface {
    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();

    @Override
    public TemporaryFolder getTempFolder() {
      return tempFolderRule;
    }
  }

  // alternative morphology of providing a rule - see http://junit.org/junit4/javadoc/4.12/org/junit/Rule.html
  public static class TempFolderRuleProvidedViaMethodMixin {
    private TemporaryFolder tempFolderRule = new TemporaryFolder();

    @Rule
    public TemporaryFolder getFolder() {
      return tempFolderRule;
    }
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

    describe("A spec with a rule mix-in where the rule is provided by method", () -> {
      Supplier<TempFolderRuleProvidedViaMethodMixin> tempFolderRuleMixin =
          junitMixin(TempFolderRuleProvidedViaMethodMixin.class);

      it("has access to an initialised object", () -> {
        assertNotNull(tempFolderRuleMixin.get().getFolder().getRoot());
      });

      describe("A spec with a rule mix-in via interface", () -> {
        JUnitTempFolderInterface tempFolderGetter =
            junitMixin(TempFolderRuleMixin.class, JUnitTempFolderInterface.class);

        it("has access to the rule-provided object at the top level", () -> {
          checkCanUseTempFolderAndRecordWhatItWas(ruleProvidedFoldersSeen,
              tempFolderGetter.getTempFolder());
        });

      });
    });
  }

  private void checkCanUseTempFolderAndRecordWhatItWas(Set<File> filesSeen,
      Supplier<TempFolderRuleMixin> tempFolderRuleMixin) {
    checkCanUseTempFolderAndRecordWhatItWas(filesSeen, tempFolderRuleMixin.get().tempFolderRule);
  }

  private void checkCanUseTempFolderAndRecordWhatItWas(Set<File> filesSeen,
      TemporaryFolder folder) {
    assertNotNull(folder.getRoot());
    filesSeen.add(folder.getRoot());
  }
}
