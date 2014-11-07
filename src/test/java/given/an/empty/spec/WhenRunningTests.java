package given.an.empty.spec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import examples.EmptySpec;

public class WhenRunningTests {

	private Result result;

	@Before
	public void runEmptySpec(){
		JUnitCore jUnitCore = new JUnitCore();
		result = jUnitCore.run(EmptySpec.class);
	}

	@Test
	public void theRunCountShouldBeOne() throws Exception {
		assertThat(result.getRunCount(), is(1));
	}

	@Test
	public void theRunIsSuccessful() throws Exception {
		assertThat(result.wasSuccessful(), is(true));
	}

}
