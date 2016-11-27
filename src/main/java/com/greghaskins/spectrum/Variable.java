package com.greghaskins.spectrum;

/**
 * This box allows data passing between specs or steps.
 */
public class Variable<T> {
  // the boxed object
  private T object;

  /**
   * Default constructor for null object.
   */
  public Variable() {}

  /**
   * Construct with the object to box.
   * @param object to box
   */
  public Variable(T object) {
    this.object = object;
  }

  /**
   * Retrieve the object.
   * @return the object in the box
   */
  public T get() {
    return object;
  }

  /**
   * Change the object in the box.
   * @param object new value
   */
  public void set(T object) {
    this.object = object;
  }
}
