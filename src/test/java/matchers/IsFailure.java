package matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.runner.notification.Failure;

public class IsFailure extends TypeSafeDiagnosingMatcher<Failure> {

  public static Matcher<Failure> failure(final String methodName, final Class<? extends Throwable> exceptionType,
      final String failureMessage) {
    return new IsFailure(methodName, exceptionType, failureMessage);
  }

  private final Class<? extends Throwable> exceptionType;
  private final String failureMessage;
  private final String methodName;

  IsFailure(final String methodName, final Class<? extends Throwable> exceptionType, final String failureMessage) {
    this.exceptionType = exceptionType;
    this.failureMessage = failureMessage;
    this.methodName = methodName;
  }

  @Override
  protected boolean matchesSafely(final Failure item, final Description mismatchDescription) {
    final String actualMethodName = getMethodName(item);
    final Throwable exception = item.getException();
    final Class<? extends Throwable> actualExceptionType = exception == null ? null : exception.getClass();
    final String actualMessage = exception == null ? null : item.getMessage();

    describeTo(mismatchDescription, actualMethodName, actualExceptionType, actualMessage);

    return methodName.equals(actualMethodName) && exceptionType.equals(actualExceptionType)
        && failureMessage.equals(actualMessage);
  }

  private String getMethodName(final Failure failure) {
    final String actualMethodName;
    if (failure.getDescription() == null) {
      actualMethodName = null;
    } else {
      actualMethodName = failure.getDescription().getMethodName();
    }
    return actualMethodName;
  }

  @Override
  public void describeTo(final Description description) {
    describeTo(description, methodName, exceptionType, failureMessage);
  }

  private void describeTo(final Description description, final String methodName,
      final Class<? extends Throwable> exceptionType, final String failureMessage) {
    description.appendText("Failure with test name ").appendValue(methodName).appendText(" with exception type ")
        .appendValue(exceptionType).appendText(" and message ").appendValue(failureMessage);
  }
}
