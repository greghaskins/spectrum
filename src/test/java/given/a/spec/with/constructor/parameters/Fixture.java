package given.a.spec.with.constructor.parameters;

class Fixture {

  public static Class<?> getSpecThatRequiresAConstructorParameter() {
    class Spec {
      @SuppressWarnings("unused")
      public Spec(final String something) {}
    }

    return Spec.class;
  }

}
