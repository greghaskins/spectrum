package given.a.spec.with.exception.in.constructor;

class Fixture {

    public static Class<?> getSpecThatThrowsAnExceptionInConstructor() {
        class Spec {
            {
                if (true) {
                    throw new SomeException("kaboom");
                }
            }
        }
        return Spec.class;
    }

    public static class SomeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public SomeException(final String message) {
            super(message);
        }
    }

}
