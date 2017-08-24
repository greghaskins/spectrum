package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class VariableSpecs {
  {

    describe("The Variable convenience wrapper", () -> {

      final Variable<Integer> counter = new Variable<>();

      beforeAll(() -> {
        counter.set(0);
      });

      beforeEach(() -> {
        final int previousValue = counter.get();
        counter.set(previousValue + 1);
      });

      it("lets you work around Java's requirement that closures only use `final` variables", () -> {
        assertThat(counter.get(), is(1));
      });

      it("can share values across scopes, so use it carefully", () -> {
        assertThat(counter.get(), is(2));
      });

      it("can optionally have an initial value set", () -> {
        final Variable<String> name = new Variable<>("Alice");
        assertThat(name.get(), is("Alice"));
      });

      it("has a null value if not specified", () -> {
        final Variable<String> name = new Variable<>();
        assertNull(name.get());
      });

      it("has the same value across threads", () -> {
        final Variable<String> outerVariable = new Variable<>("outer");
        final Variable<String> whatWorkerThreadSees = new Variable<>();

        Thread worker = new Thread(() -> whatWorkerThreadSees.set(outerVariable.get()));
        worker.start();
        worker.join();

        assertThat(whatWorkerThreadSees.get(), is(outerVariable.get()));
      });

    });

  }
}
