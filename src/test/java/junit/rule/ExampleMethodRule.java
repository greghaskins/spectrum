package junit.rule;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Method rule we can test with.
 */
public class ExampleMethodRule implements MethodRule {
  private int count = 0;

  public int getCount() {
    return count;
  }

  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    count++;

    return base;
  }
}
