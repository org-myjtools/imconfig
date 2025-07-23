/*
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 */
package org.myjtools.imconfig.internal;


import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.myjtools.imconfig.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class ConfigFactory {

    private final ConversionHandler conversionHandler = new ApacheConfiguration2ConversionHandler();
    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();

    public static final ApacheConfiguration2 EMPTY = new ApacheConfiguration2(new BaseConfiguration());
    public static final ApacheConfiguration2 ENVIRONMENT = new ApacheConfiguration2( new EnvironmentConfiguration());
    public static final ApacheConfiguration2 SYSTEM = new ApacheConfiguration2( new SystemConfiguration());

    private char separator = 0;


    public ConfigFactory multivalueSeparator(char separator) {
        if (separator == 0) {
            throw new IllegalArgumentException("Invalid separator symbol: "+separator);
        }
        this.separator = separator;
        return this;
    }


    public boolean hasMultivalueSeparator() {
        return this.separator != 0;
    }


    public char multivalueSeparator() {
        return this.separator;
    }


    public Config merge(Config base, Config delta) {

        AbstractConfiguration result = new BaseConfiguration();

        base.keys().filter(delta::notHasProperty).forEach(
        property -> base.getList(property,String.class).forEach(value -> result.addProperty(property,value))
        );

        delta.keys().forEach(property -> {
            var existing = base.getList(property,String.class);
            var added = delta.getList(property,String.class);
            if (existing.isEmpty() && added.isEmpty()) {
                result.setProperty(property,"");
            } else if (!added.isEmpty()) {
                added.forEach(value -> result.addProperty(property,value));
            }
        });

        Map<String, PropertyDefinition> definitions = new HashMap<>(base.getDefinitions());
        definitions.putAll(delta.getDefinitions());

        return new ApacheConfiguration2(definitions, result);
    }



    public Config ofClass(Class<?> configuredClass) {
        return Optional.ofNullable(configuredClass.getAnnotation(AnnotatedConfig.class))
            .map(this::ofClass)
            .orElseThrow(
                () -> new ConfigException(
                    configuredClass + " is not annotated with @Configurator"
                )
            );
    }


    public Config ofClass(AnnotatedConfig annotation) {
        BaseConfiguration configuration = configure(new BaseConfiguration());
        for (Property property : annotation.value()) {
            String[] value = property.value();
            if (value.length == 1) {
                configuration.addProperty(property.key(), value[0]);
            } else {
                configuration.addProperty(property.key(), value);
            }
        }
        return new ApacheConfiguration2(configuration);

    }


    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
    public Config ofProperties(Properties properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<Object, Object> property : properties.entrySet()) {
            configuration.addProperty(property.getKey().toString(), property.getValue());
        }
        return new ApacheConfiguration2(configuration);
    }


    public Config ofMap(Map<String, ?> properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<String, ?> property : properties.entrySet()) {
            configuration.addProperty(property.getKey(), property.getValue());
        }
        return new ApacheConfiguration2(configuration);
    }



    public Config ofURI(URI uri) {
        try (Reader reader = new InputStreamReader(uri.toURL().openStream())) {
            return buildFromInputStream(uri.getPath(), reader);
        } catch (ConfigurationException | IOException e) {
            throw new ConfigException(e);
        }
    }


    public Config ofResource(String resource, ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(resource)))) {
            return buildFromInputStream(resource, reader);
        } catch (ConfigurationException | IOException | RuntimeException e) {
            throw new ConfigException(e);
        }
    }



    private Config buildFromInputStream(String file, Reader reader) throws ConfigurationException, IOException {
        if (file.endsWith(".properties")) {
            var abstractConfiguration = configure(new PropertiesConfiguration());
            abstractConfiguration.read(reader);
            return new ApacheConfiguration2(Map.of(), abstractConfiguration);
        } else if (file.endsWith(".json")) {
            var abstractConfiguration = configure(new JSONConfiguration());
            abstractConfiguration.read(reader);
            return new ApacheConfiguration2(Map.of(), abstractConfiguration);
        } else if (file.endsWith(".xml")) {
            var abstractConfiguration = configure(new XMLConfiguration());
            FileHandler handler = new FileHandler(abstractConfiguration);
            handler.load(reader);
            return new ApacheConfiguration2(Map.of(), abstractConfiguration);
        } else if (file.endsWith(".yaml") || file.endsWith(".yml")) {
            var abstractConfiguration = configure(new YAMLConfiguration());
            abstractConfiguration.read(reader);
            return new ApacheConfiguration2(Map.of(), abstractConfiguration);
        } else {
            throw new ConfigException("Cannot determine resource type of " + file);
        }
    }



    private <T extends AbstractConfiguration> T configure(T configuration) {
        configuration.setConversionHandler(conversionHandler);
        if (hasMultivalueSeparator()) {
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(multivalueSeparator()));
        }
        return configuration;
    }



    public Config withDefinitions(Collection<PropertyDefinition> definitions) {
        Map<String,String> defaultValues = definitions
                .stream()
                .filter(definition -> definition.defaultValue().isPresent())
                .collect(Collectors.toMap(
                        PropertyDefinition::property,
                        definition->definition.defaultValue().orElseThrow()
                ));
        BaseConfiguration configuration = new BaseConfiguration();
        defaultValues.forEach(configuration::addProperty);
        var definitionMap = definitions.stream()
                .collect(Collectors.toMap(PropertyDefinition::property,x->x));
        return new ApacheConfiguration2(definitionMap,configuration);
    }


    public Collection<PropertyDefinition> loadDefinitions(URI uri) {
        try (InputStream inputStream = uri.toURL().openStream()) {
            return parser.read(inputStream);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }


    public Collection<PropertyDefinition> loadDefinitionsFromResource(String resource, ClassLoader classLoader) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            return parser.read(inputStream);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }
}
