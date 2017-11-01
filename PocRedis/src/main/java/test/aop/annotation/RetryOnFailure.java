package test.aop.annotation;

/**
 * Created by HUANGYE2 on 10/31/2017.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Retry the method in case of exception.
 * <p>
 * <p>For example, this {@code load()} method will retry to load the URL
 * content if it fails at the first attempts:
 * <p>
 * <pre> &#64;RetryOnFailure(attempts = 2)
 * String load(URL url) throws IOException {
 *   return url.getContent().toString();
 * }</pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @see <a href="http://aspects.jcabi.com">http://aspects.jcabi.com/</a>
 * @since 0.1.10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RetryOnFailure {

    /**
     * How many times to retry.
     *
     * @checkstyle MagicNumber (2 lines)
     */
    int attempts() default 3;

    /**
     * Delay between attempts, in time units.
     *
     * @checkstyle MagicNumber (2 lines)
     */
    long delay() default 50;

    /**
     * Time units.
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * When to retry (in case of what exception types).
     */
    Class<? extends Throwable>[] types() default {Throwable.class};

    /**
     * Exception types to ignore.
     */
    Class<? extends Throwable>[] ignore() default {};

    /**
     * Shall it be fully verbose (show full exception trace) or just
     * exception message?
     */
    boolean verbose() default true;

    /**
     * Shall the time between retries by randomized.
     */
    boolean randomize() default true;

}
