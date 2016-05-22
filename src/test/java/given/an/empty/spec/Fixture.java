package given.an.empty.spec;

class Fixture {

  public static Class<?> getEmptySpec() {
    class Spec {
      {

      }
    }

    return Spec.class;
  }

}
