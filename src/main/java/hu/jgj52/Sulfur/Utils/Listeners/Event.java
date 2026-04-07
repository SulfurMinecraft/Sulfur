package hu.jgj52.Sulfur.Utils.Listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
    boolean ignoreCancelled() default false;
    int priority() default 0;
}
