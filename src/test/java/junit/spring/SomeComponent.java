package junit.spring;

import org.springframework.stereotype.Component;

/**
 * A Spring Component.
 */
@Component
public class SomeComponent {
  private String state = "";

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
