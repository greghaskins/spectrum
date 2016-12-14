package com.greghaskins.spectrum.internal.parameterized;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Example<T> {

  final String description;
  private Consumer<T> blockRunner;

  public Example(Consumer<T> blockRunner, Object... arguments) {
    this.blockRunner = blockRunner;
    this.description = describe(arguments);
  }

  public void runDeclaration(T block) {
    this.blockRunner.accept(block);
  }

  @Override
  public String toString() {
    return description;
  }

  private static String describe(Object[] objects) {
    return Arrays.stream(objects)
        .map(o -> Optional.ofNullable(o)
            .map(Object::toString)
            .orElse("null"))
        .collect(Collectors.joining("|", "|", "|"));
  }

}
