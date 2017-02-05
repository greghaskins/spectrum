package com.greghaskins.spectrum.internal.junit;

import static com.greghaskins.spectrum.internal.junit.StubJUnitFrameworkMethod.stubFrameworkMethod;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.ConstructorBlock;
import com.greghaskins.spectrum.internal.Hook;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.MethodRule;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tracks a junit.rule that must be applied to all descendants of a suite.
 */
public class RuleContext<T> implements Supplier<T> {
  private final Class<T> ruleClass;
  private final TestClass testClass;
  private T currentTestObject;
  private final boolean constructEveryTime;

  RuleContext(final Class<T> ruleClass) {
    this.ruleClass = ruleClass;
    this.testClass = new TestClass(ruleClass);
    this.constructEveryTime = true;
  }

  @SuppressWarnings("unchecked")
  RuleContext(final T object) {
    this.ruleClass = (Class<T>) object.getClass();
    this.testClass = new TestClass(this.ruleClass);
    this.currentTestObject = object;
    this.constructEveryTime = false;
  }

  @Override
  public T get() {
    return currentTestObject;
  }

  /**
   * Construct the class level hook.
   * @return the hook
   */
  Hook classHook() {
    return (description, notifier,
        block) -> withClassBlock(statementOf(block), fakeForJunit(description))
            .evaluate();
  }

  /**
   * Construct a hook for around methods.
   * @return the hook
   */
  Hook methodHook() {
    return (description, notifier, block) -> decorate(statementOf(block), fakeForJunit(description))
        .evaluate();
  }

  /**
   * Add the method and test rules execution around a test method statement.
   * @param base the base statement
   * @param description of the child
   * @return the statement to use to execute the child within the rules
   * @throws Throwable on error
   */
  private Statement decorate(final Statement base, final Description description) throws Throwable {
    if (constructEveryTime) {
      constructTestObject();
    }

    return withTestRules(getTestRules(currentTestObject),
        withMethodRules(base, getMethodRules(currentTestObject)), description);
  }

  private void constructTestObject() throws Throwable {
    ConstructorBlock<T> constructor = new ConstructorBlock<>(ruleClass);
    constructor.run();
    currentTestObject = constructor.get();
  }

  private Statement withMethodRules(final Statement base, final List<MethodRule> methodRules) {
    FrameworkMethod method = stubFrameworkMethod();

    return decorateWithMethodRules(base, methodRules, method);
  }

  private Statement decorateWithMethodRules(final Statement base,
      final List<MethodRule> methodRules,
      final FrameworkMethod method) {
    Statement result = base;
    for (MethodRule each : methodRules) {
      result = each.apply(result, method, currentTestObject);
    }

    return result;
  }

  private Statement withTestRules(final List<TestRule> testRules, final Statement statement,
      final Description childDescription) {
    return testRules.isEmpty() ? statement : new RunRules(statement, testRules, childDescription);
  }

  /**
   * Find the method rules within the test class mixin.
   * @param target the test case instance
   * @return a list of TestRules that should be applied when executing this
   *         test
   */
  private List<MethodRule> getMethodRules(final Object target) {
    return Stream.concat(
        testClass.getAnnotatedMethodValues(target, Rule.class, MethodRule.class).stream(),
        testClass.getAnnotatedFieldValues(target, Rule.class, MethodRule.class).stream())
        .collect(Collectors.toList());
  }

  /**
   * Find the test rules within the test mixin.
   * @param target the test case instance
   * @return a list of TestRules that should be applied when executing this
   *         test
   */
  private List<TestRule> getTestRules(final Object target) {
    return Stream.concat(
        testClass.getAnnotatedMethodValues(target, Rule.class, TestRule.class).stream(),
        testClass.getAnnotatedFieldValues(target, Rule.class, TestRule.class).stream())
        .collect(Collectors.toList());
  }

  private boolean hasAnyTestOrMethodRules() {
    return !testClass.getAnnotatedFields(Rule.class).isEmpty()
        || !testClass.getAnnotatedMethods(Rule.class).isEmpty();
  }

  private Statement withClassBlock(final Statement base, final Description description) {
    return withClassRules(withAfterClasses(withBeforeClasses(base)), description);
  }

  // In the case of multi-threaded execution, this will prevent two threads from
  // executing the same class junit.rule.
  private synchronized Statement withClassRules(final Statement base,
      final Description description) {
    List<TestRule> classRules = getClassRules();

    return classRules.isEmpty() ? base : new RunRules(base, classRules, description);
  }

  private Statement withAfterClasses(final Statement base) {
    List<FrameworkMethod> afters = testClass.getAnnotatedMethods(AfterClass.class);

    return afters.isEmpty() ? base : new RunAfters(base, afters, null);
  }

  private Statement withBeforeClasses(final Statement base) {
    List<FrameworkMethod> befores = testClass.getAnnotatedMethods(BeforeClass.class);

    return befores.isEmpty() ? base : new RunBefores(base, befores, null);
  }

  private List<TestRule> getClassRules() {
    return Stream.concat(
        testClass.getAnnotatedMethodValues(null, ClassRule.class, TestRule.class).stream(),
        testClass.getAnnotatedFieldValues(null, ClassRule.class, TestRule.class).stream())
        .collect(Collectors.toList());
  }

  /**
   * Wrap a {@link Block} as a {@link Statement} for JUnit purposes.
   * @param toExecute block that will be running inside the statement
   * @return statement encapsulating the work
   */
  private Statement statementOf(final Block toExecute) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        toExecute.run();
      }
    };
  }

  /**
   * Does the object provided actually have any rules.
   * @return true if there are rules
   */
  boolean hasAnyJUnitAnnotations() {
    return getClassRules().size() > 0 || hasAnyTestOrMethodRules();
  }

  /**
   * The Description objects from {@link com.greghaskins.spectrum.Spectrum} are not from the
   * class that the JUnit rules expect.
   * @param description to convert to something JUnit can cope with
   * @return a new description
   */
  private Description fakeForJunit(final Description description) {
    return Description.createTestDescription(testClass.getJavaClass(), description.getMethodName());
  }
}
