package junit.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * A Spring Configuration Bean which just finds beans in this package.
 */
@Configuration
@ComponentScan(basePackages = "junit.spring")
public class SpringConfig {
}
