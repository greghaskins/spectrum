package specs;

import static com.greghaskins.spectrum.Spectrum.applyRules;
import static com.greghaskins.spectrum.Spectrum.applyRulesHere;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class JUnitRuleExample {
  public static class TempFolderRule {
    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();
  }

  {
    final Set<File> filesSeen = new HashSet<>();
    describe("A parent spec", () -> {
      Supplier<TempFolderRule> tempFolderRuleSupplier = applyRules(TempFolderRule.class);

      it("has access to the folder at suite level", () -> {
        assertNotNull(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        filesSeen.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
      });

      describe("with a nested set of specs", () -> {
        it("has access to the folder at nested level", () -> {
          assertNotNull(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
          filesSeen.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
        it("has access to the folder with different value ", () -> {
          assertNotNull(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
          filesSeen.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
      });

      it("has seen several different folders", () -> {
        assertThat(filesSeen.size(), is(3));
      });
    });

    describe("Each nested spec should not have its own instance of test object", () -> {
      Supplier<TempFolderRule> tempFolderRuleSupplier = applyRulesHere(TempFolderRule.class);
      final Set<File> filesSeen2 = new HashSet<>();

      // should have the folder once for here
      describe("with a nested set of specs", () -> {
        it("has access to the folder", () -> {
          filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
        it("has access to the folder", () -> {
          filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
      });

      // and once for here
      describe("with a nested set of specs", () -> {
        describe("more nesting", () -> {
          it("has access to the folder", () -> {
            filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
          });
        });
        it("has access to the folder", () -> {
          filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
        it("has access to the folder", () -> {
          filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        });
      });

      it("has only had new folders at this level of the test", () -> {
        filesSeen2.add(tempFolderRuleSupplier.get().tempFolderRule.getRoot());
        assertThat(filesSeen2.size(), is(3));
      });
    });
  }
}
