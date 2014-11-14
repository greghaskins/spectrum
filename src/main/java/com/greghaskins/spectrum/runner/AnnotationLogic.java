package com.greghaskins.spectrum.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

class AnnotationLogic {

    public static IfElement ifElement(final AnnotatedElement element) {
        return new IfElement(element);
    }

    public static interface ThenClause<T> {
        void then(final T foundAnnotation);
    }

    public static class IfElement {

        private final AnnotatedElement element;

        private IfElement(final AnnotatedElement element) {
            this.element = element;
        }

        public <T extends Annotation> HasAnnotation<T> hasAnnotation(final Class<T> annotationType) {
            return new HasAnnotation<T>(annotationType);
        }

        public class HasAnnotation<T extends Annotation> {

            private final Class<T> type;

            private HasAnnotation(final Class<T> annotationType) {
                type = annotationType;
            }

            public void then(final ThenClause<T> thenClause) {
                final T annotation = element.getAnnotation(type);
                if (annotation != null) {
                    thenClause.then(annotation);
                }
            }

        }

    }

}
