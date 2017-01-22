package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.junitMixin;
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
  public static class TempFolderRuleMixin {
    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();
  }

  {
    final Set<File> filesSeen = new HashSet<>();
    describe("A parent spec", () -> {
      Supplier<TempFolderRuleMixin> tempFolderRuleMixin = junitMixin(TempFolderRuleMixin.class);

      it("has access to the folder at suite level", () -> {
        assertNotNull(tempFolderRuleMixin.get().tempFolderRule.getRoot());
        filesSeen.add(tempFolderRuleMixin.get().tempFolderRule.getRoot());
      });

      describe("with a nested set of specs", () -> {
        it("has access to the folder at nested level", () -> {
          assertNotNull(tempFolderRuleMixin.get().tempFolderRule.getRoot());
          filesSeen.add(tempFolderRuleMixin.get().tempFolderRule.getRoot());
        });
        it("has access to the folder with different value ", () -> {
          assertNotNull(tempFolderRuleMixin.get().tempFolderRule.getRoot());
          filesSeen.add(tempFolderRuleMixin.get().tempFolderRule.getRoot());
        });
      });

      it("has seen several different folders", () -> {
        assertThat(filesSeen.size(), is(3));
      });
    });
  }
}
