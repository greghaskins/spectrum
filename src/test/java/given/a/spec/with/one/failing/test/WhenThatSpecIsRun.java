package given.a.spec.with.one.failing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import examples.SpecWithOneFailingTest;

public class WhenThatSpecIsRun {

    private Result result;

    @Before
    public void runTheSpec(){
        result = JUnitCore.runClasses(SpecWithOneFailingTest.class);
    }

    @Test
    public void thereIsOneFailure() throws Exception {
        assertThat(result.getFailureCount(), is(1));
    }

    @Test
    public void theFailureDescriptionHasTheAnnotatedTestName() throws Exception {
        assertThat(getFailureDescription().getMethodName(), is("should fail"));
    }

    @Test
    public void theFailureDescriptionHasTheParentContext() throws Exception {
        assertThat(getFailureDescription().getClassName(), is("A spec with one failing test"));
    }

    private Description getFailureDescription() {
        return result.getFailures().get(0).getDescription();
    }

}
