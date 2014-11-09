package given.a.spec.with.two.failures.and.three.passing.tests;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@RunWith(Enclosed.class)
public class TestsCanPassOrFail_Spec {

    private static Class<?> getSpecWithTwoFailuresAndThreePassingTests(){

        @Describe("A spec with two failures and three passing tests")
        @RunWith(Spectrum.class)
        class SpecWithTwoFailuresAndThreePassingTests {

            @It("fails test 1") void test1() {
                Assert.fail("failure message one");
            }

            @It("passes test 2") void test2() {
                Assert.assertTrue(true);
            }

            @It("passes test 3") void test3() {
                Assert.assertTrue(true);
            }

            @It("fails test 4") void test4() {
                Assert.fail("failure message four");
            }

            @It("passes test 5") void test5() {
                Assert.assertTrue(true);
            }
        }
        return SpecWithTwoFailuresAndThreePassingTests.class;
    }

    public static class WhenDescribingTheSpec {

        private Description description;

        @Before
        public void getTheDescription() throws Exception {
            description = new Spectrum(getSpecWithTwoFailuresAndThreePassingTests()).getDescription();
        }

        @Test
        public void thereShouldBeFiveChildTests() throws Exception {
            assertThat(description.getChildren(), hasSize(5));
        }

        @Test
        public void theTestsShouldBeInSourceDefinitionOrder() throws Exception {
            final List<String> testNames = getMethodNames(description.getChildren());
            assertThat(testNames, contains("fails test 1", "passes test 2", "passes test 3", "fails test 4", "passes test 5"));
        }

        @Test
        public void theTestsShouldAllHaveTheParentContextName() throws Exception {
            for (final Description testDescription : description.getChildren()) {
                assertThat(testDescription.getClassName(), is("A spec with two failures and three passing tests"));
            }
        }

    }

    public static class WhenThatSpecIsRun {

        private Result result;

        @Before
        public void runTheSpec() {
            result = JUnitCore.runClasses(getSpecWithTwoFailuresAndThreePassingTests());
        }

        @Test
        public void thereShouldBeFiveTotalRuns() throws Exception {
            assertThat(result.getRunCount(), is(5));
        }

        @Test
        public void thereShouldBeTwoFailures() throws Exception {
            assertThat(result.getFailureCount(), is(2));
        }

        @Test
        public void theCorrectTestsShouldHaveFailed() throws Exception {
            assertThat(result.getFailures().get(0).getDescription().getMethodName(), is("fails test 1"));
            assertThat(result.getFailures().get(1).getDescription().getMethodName(), is("fails test 4"));
        }

    }

    private static List<String> getMethodNames(final List<Description> testDescriptions) {
        final ArrayList<String> methodNames = new ArrayList<String>();
        for (final Description description : testDescriptions) {
            methodNames.add(description.getMethodName());
        }
        return methodNames;
    }

}
