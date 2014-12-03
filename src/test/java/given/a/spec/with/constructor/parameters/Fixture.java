package given.a.spec.with.constructor.parameters;

class Fixture {

    public static Class<?> getSpecThatRequiresAConstructorParameter() {
        class Spec {
            @SuppressWarnings("unused")
            public Spec(final String something) {
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
