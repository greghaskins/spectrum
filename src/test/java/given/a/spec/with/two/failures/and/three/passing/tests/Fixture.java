package given.a.spec.with.two.failures.and.three.passing.tests;

import org.junit.Assert;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

public class Fixture {

    public static Class<?> getSpecWithTwoFailuresAndThreePassingTests(){

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

            @It("fails test 4") void test4() throws Exception {
                throw new Exception("failure message four");
            }

            @It("passes test 5") void test5() {
                Assert.assertTrue(true);
            }
        }
        return SpecWithTwoFailuresAndThreePassingTests.class;
    }

}
