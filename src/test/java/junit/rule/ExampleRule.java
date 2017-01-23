package junit.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


/**
 * Testable junit.rule.
 */
public class ExampleRule implements TestRule {
  private int count = 0;

  @Override
  public Statement apply(Statement base, Description description) {
    count++;

    return base;
  }

  public int getCount() {
    return count;
  }
}
