package com.greghaskins.spectrum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Options that can be applied to a test class annotated with {@link org.junit.runner.RunWith}
 * with the {@link Spectrum} runner. E.g.<br>
 * <pre><code class="java">
 *     &#064;RunWith(Spectrum.class)
 *     &#064;SpectrumOptions(includeTags={"wip","dev"})
 *     public class MyTest { ... }
 * </code></pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpectrumOptions {
  String REQUIRE_TAGS_PROPERTY = "spectrum.require.tags";
  String EXCLUDE_TAGS_PROPERTY = "spectrum.exclude.tags";
  String TAGS_SEPARATOR = ",";

  /**
   * Allows tags to be selected for controlling the test at coding time.
   * This means you can supply tags which relate to, say, Work In Progress
   * specs temporarily while developing them. See {@link PreConditions#tags(String...)}
   * @return the tags that must be present.
   */
  String[] requireTags() default {};

  /**
   * Allows tags to be selected for controlling the test at coding time.
   * This means you can supply tags while developing. See {@link PreConditions#tags(String...)}
   * @return the tags hard coded for exclusion.
   */
  String[] excludeTags() default {};

}
