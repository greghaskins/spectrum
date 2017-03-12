package com.greghaskins.spectrum.dsl.gherkin;

import java.util.Collection;
import java.util.stream.Stream;

public class Examples<T> {

  private final Collection<TableRow<T>> examples;

  Examples(Collection<TableRow<T>> examples) {
    this.examples = examples;
  }

  Stream<TableRow<T>> rows() {
    return this.examples.stream();
  }

}
