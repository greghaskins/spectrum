package junit.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of SomeService Bean.
 */
@Service
public class SomeServiceImpl implements SomeService {
  @Autowired
  private SomeComponent someComponent;

  @Override
  public String getGreeting() {
    return "Hello world!";
  }

  @Override
  public SomeComponent getComponent() {
    return someComponent;
  }
}
