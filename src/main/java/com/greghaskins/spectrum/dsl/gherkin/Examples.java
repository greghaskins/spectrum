package com.greghaskins.spectrum.dsl.gherkin;

import java.util.Collection;
import java.util.stream.Stream;

public class Examples<T> {

  private Collection<TableRow<T>> examples;

  public Examples(Collection<TableRow<T>> examples) {
    this.examples = examples;
  }

  public Stream<TableRow<T>> rows() {
    return this.examples.stream();
  }

}
