/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package org.myjtools.imconfig;


import org.myjtools.imconfig.internal.ConfigFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * The main interface used to get configuration values and create derived configurations.
 */
public interface Config {

    static ConfigFactory factory = new ConfigFactory();

    /**
     * Create a new empty configuration
     */
    static Config empty() {
        return ConfigFactory.EMPTY;
    }

    /**
     * Create a new configuration from the system properties
     */
    static Config system() {
        return ConfigFactory.SYSTEM;
    }


    /**
     * Create a new configuration from the environment properties
     */
    static Config env() {
        return ConfigFactory.ENVIRONMENT;
    }


    /**
     * Create a new configuration from an annotated class
     */
    static Config ofClass(Class<?> configuredClass) {
        return factory.ofClass(configuredClass);
    }


    /**
     * Create a new configuration from the file of the classpath resource
     */
    static Config ofResource(String resource, ClassLoader classLoader) {
        return factory.ofResource(resource, classLoader);
    }

    /**
     * Create a new configuration from the file of the given path
     */
    static Config ofPath(Path path) {
        return factory.ofURI(path.toUri());
    }


    /**
     * Create a new configuration from the file of the given URI
     */
    static Config ofURI(URI uri) {
        return factory.ofURI(uri);
    }

    /**
     * Create a new configuration from a properties object
     */
    static Config ofProperties(Properties properties) {
        return factory.ofProperties(properties);
    }


    /**
     * Create a new configuration from a map
     */
    static Config ofMap(Map<String, ?> map) {
        return factory.ofMap(map);
    }


    /**
     * Create a new defined configuration from a set of definitions
     */
    static Config withDefinitions(Collection<PropertyDefinition> definitions) {
        return factory.withDefinitions(definitions);
    }


    /**
     * Create a new defined configuration
     */
    static Collection<PropertyDefinition> loadDefinitions(URI uri) {
        return factory.loadDefinitions(uri);
    }


    /**
     * Create a new defined configuration
     */
    static Collection<PropertyDefinition> loadDefinitionsFromResource(String resource, ClassLoader classLoader) {
        return factory.loadDefinitionsFromResource(resource,classLoader);
    }


    /**
     * Creates a new configuration resulting of adding the given prefix to every
     * key
     */
    Config withPrefix(String keyPrefix);


    /**
     * Creates a new configuration resulting of filtering the properties starting
     * with the given prefix
     */
    Config filtered(String keyPrefix);


    /**
     * Creates a new configuration resulting of filtering the properties starting
     * with the given prefix, and the removing it
     */
    Config inner(String keyPrefix);


    /**
     * @return <code>true</code> if there is no properties in this configuration
     */
    boolean isEmpty();


    /**
     * @return <code>true</code> if there is any property in this configuration
     */
    boolean isNotEmpty();


    /** @return <code>true</code> if there is a valued property with the given key */
    boolean hasProperty(String key);

    /** @return <code>true</code> if there is not any valued property with the given key */
    boolean notHasProperty(String key);


    /** @return A stream from all the keys of the configuration,
     *  even for those which have no value */
    Stream<String> keys();


    /**
     * @return An optional value of the specified type, empty if the key does not
     *         exist
     */
    <T> Optional<T> get(String key, Class<T> type);


    /**
     * @return An optional value converted from a string, empty if the key does not exist
     */
    <T> Optional<T> get(String key, Function<String,T> converter);


    /**
     * @return An optional integer value, empty if the key does not exist
     */
    Optional<Integer> getInteger(String key);

    /**
     * @return An optional long value, empty if the key does not exist
     */
    Optional<Long> getLong(String key);


    /**
     * @return An optional float value, empty if the key does not exist
     */
    Optional<Float> getFloat(String key);

    /**
     * @return An optional double value, empty if the key does not exist
     */
    Optional<Double> getDouble(String key);

    /**
     * @return An optional integer value, empty if the key does not exist
     */
    Optional<String> getString(String key);


    /**
     * Instantiate a new object with its fields filled according the configuration.
     */
    <T> T getObject(Class<T> configClass);

    /**
     * @return A list with values of the specified type, empty if the key does
     *         not exist
     */
    <T> List<T> getList(String key, Class<T> type);


    /**
     * @return A set with values of the specified type, empty if the key does not
     *         exist
     */
    <T> Set<T> getSet(String key, Class<T> type);


    /**
     * @return A stream with values of the specified type, empty if the key does
     *         not exist
     */
    <T> Stream<T> getStream(String key, Class<T> type);


    /** @return The configuration represented as a {@link Properties} object */
    Properties asProperties();


    /** @return The configuration represented as a {@link Map} object */
    Map<String, String> asMap();



    /** Perform an action for each pair <code>[key,value]</code> */
    void forEach(BiConsumer<String, String> consumer);


    /**
     * Create a new configuration resulting in the merge the current configuration
     * with another one
     */
    Config append(Config otherConfiguration);


    /**
     * @return whether there is a definition for the given property
     */
    boolean hasDefinition(String key);


    /**
     * Check whether the current value for the given property is valid according its definition.
     * If the property is multi-valued, it may return a different validation for each value
     * @param key The property key
     * @return The validation messages, or empty if the value is valid
     */
    List<String> validations(String key);



    /**
     * Retrieve the property definition for a given property
     */
    Optional<PropertyDefinition> getDefinition(String key);


    /**
     * Retrieve every property definition defined for this configuration
     * @return An unmodifiable map in the form of <property,definition>
     */
    Map<String,PropertyDefinition> getDefinitions();


    /**
     * Return a map in form of <tt>property=[validation_message1,...]</tt>
     * with the validation error messages for all invalid properties values
     * according the current definition.
     * <p>
 *     Configurations without definition will always return an empty map.
     * </p>
     */
    Map<String,List<String>> validations();


    /**
     * Ensures that all property values are valid according the current definition.
     * Otherwise, it will raise a {@link ConfigException} with a list of every
     * invalid value.
     * <p>
 *     Configurations without definition will never raise an exception using this method
     * </p>
     * @throws ConfigException if one or more properties have invalid values
     * @return The same instance, for convenience
     */
    Config validate() throws ConfigException;


    /**
     * Create a new configuration according the given property definitions.
     * <p>
     * Defined properties will be set to their default value if it exists and no current value is
     * set.
     * @see PropertyDefinition
     */
    Config accordingDefinitions(Collection<PropertyDefinition> definitions);


    /**
     * Get a textual representation of all defined properties
     */
    String getDefinitionsToString();
}
