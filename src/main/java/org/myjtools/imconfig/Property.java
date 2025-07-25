/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package org.myjtools.imconfig;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Pair of [key,value] used in {@link AnnotatedConfig#value()}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Property {

    /** The property key */
    String key();

    /** The value (either single or multiple) of the property */
    String[] value();

}
