package com.greghaskins.spectrum.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class AnnotationLogic {

    public static interface Then<T> {
        void then(final T foundAnnotation);
    }

    public static <T extends Annotation> void ifMethodHasAnnotation(final Method method, final Class<T> annotationType,
            final Then<T> then) {
        final T annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
            then.then(annotation);
        }
    }

}
