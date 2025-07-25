package org.myjtools.imconfig;

/**
 * A convenient interface for objects that can apply a configuration to another object.
 * The main difference with {@link Configurable} is that, while a `Configurer` is a third
 * object that applies a configuration to others, a `Configurable` applies the configuration
 * to itself.
 * @param <T> The type of the object that can be configured
 */
public interface Configurer<T> {

    /**
     * Apply the given configuration to the specified object
     * @param configurable The object to configure
     * @param configuration The configuration that would be applied
     */
    void configure (T configurable, Config configuration);




}
