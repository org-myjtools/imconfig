/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package org.myjtools.imconfig.internal;


import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.myjtools.imconfig.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;


public class ApacheConfiguration2 extends AbstractConfiguration {

    protected final org.apache.commons.configuration2.Configuration conf;


    protected ApacheConfiguration2(
        Map<String, PropertyDefinition> definitions,
        org.apache.commons.configuration2.Configuration conf
    ) {
        super(definitions);
        this.conf = conf;
    }



    protected ApacheConfiguration2(org.apache.commons.configuration2.Configuration conf) {
        super(Map.of());
        this.conf = conf;
    }



    @Override
    public Config withPrefix(String keyPrefix) {
        BaseConfiguration innerConf = prepare(new BaseConfiguration());
        conf.getKeys().forEachRemaining(
            key -> innerConf.addProperty(keyPrefix + "." + key, conf.getProperty(key))
        );
        return new ApacheConfiguration2(definitions, innerConf);
    }


    public Config filtered(String keyPrefix) {
        BaseConfiguration innerConf = prepare(new BaseConfiguration());
        conf.getKeys(keyPrefix).forEachRemaining(key -> {
            if (key.startsWith(keyPrefix)) {
                innerConf.addProperty(key, conf.getProperty(key));
            }
        });
        return new ApacheConfiguration2(definitions, innerConf);
    }


    public Config inner(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            return this;
        }
        return new ApacheConfiguration2(definitions, conf.subset(keyPrefix));
    }


    public boolean isEmpty() {
        return conf.isEmpty();
    }


    public boolean isNotEmpty() {
        return !conf.isEmpty();
    }


    public boolean hasProperty(String key) {
        return conf.containsKey(key);
    }

    public boolean notHasProperty(String key) {
        return !conf.containsKey(key);
    }


    public Stream<String> keys() {
        List<String> keys = new ArrayList<>();
        conf.getKeys().forEachRemaining(keys::add);
        return keys.stream();
    }


    public <T> Optional<T> get(String key, Class<T> type) {
        var definition = definitions.get(key);
        String raw = conf.getString(key);
        boolean empty = (raw == null || raw.isEmpty());
        if (definition != null) {
            var value = empty ? definition.defaultValue().orElse(null) : raw;
            if (value != null) {
                return Optional.of(convert(value, type));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.ofNullable(empty ? null : conf.get(type, key));
        }
    }


    @Override
    public Optional<Integer> getInteger(String key) {
        return get(key, Integer.class);
    }

    @Override
    public Optional<Long> getLong(String key) {
        return get(key,Long.class);
    }

    @Override
    public Optional<Float> getFloat(String key) {
        return get(key,Float.class);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return get(key,Double.class);
    }

    @Override
    public Optional<String> getString(String key) {
        return get(key,String.class);
    }

    @Override
    public <T> Optional<T> get(String key, Function<String, T> converter) {
        return get(key,String.class).map(converter);
    }

    public <T> List<T> getList(String key, Class<T> type) {
        return Optional.ofNullable(conf.getList(type, key)).orElse(Collections.emptyList());
    }


    public <T> Set<T> getSet(String key, Class<T> type) {
        return new HashSet<>(getList(key, type));
    }


    public <T> Stream<T> getStream(String key, Class<T> type) {
        return getList(key, type).stream();
    }


    public Properties asProperties() {
        Properties properties = new Properties();
        conf.getKeys().forEachRemaining(key -> properties.put(key, conf.getString(key)));
        return properties;
    }


    @Override
    public Map<String, String> asMap() {
        Map<String, String> map = new LinkedHashMap<>();
        conf.getKeys().forEachRemaining(key -> map.put(key, conf.getString(key)));
        return map;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("configuration:\n---------------\n");
        conf.getKeys().forEachRemaining(key -> {
            final String[] values = conf.getStringArray(key);
            String value = "<undefined>";
            if (values.length == 1) {
                value = values[0];
            } else if (values.length > 1) {
                value = Arrays.toString(values);
            }
            string
                .append(key)
                .append(" : ")
                .append(value)
                .append("\n");
        });
        return string.append("---------------").toString();
    }


    @Override
    public void forEach(BiConsumer<String, String> consumer) {
        conf.getKeys().forEachRemaining(key -> consumer.accept(key, conf.get(String.class, key)));
    }




    private <T> T convert(String raw, Class<T> type) {
        var abstractConf = (org.apache.commons.configuration2.AbstractConfiguration)conf;
        return abstractConf.getConversionHandler().to(raw, type, abstractConf.getInterpolator());
    }


    private <T extends org.apache.commons.configuration2.AbstractConfiguration> T prepare(T abstractConfiguration) {
        if (factory.hasMultivalueSeparator()) {
            abstractConfiguration.setListDelimiterHandler(
                new DefaultListDelimiterHandler(factory.multivalueSeparator())
            );
        }
        return abstractConfiguration;
    }


    @Override
    public <T> T getObject(Class<T> configClass) {
        if (!configClass.isAnnotationPresent(ConfigClass.class)) {
            throw new ConfigException("Class "+configClass+" should be annotated with @ConfigClass");
        }
        try {
            T instance = configClass.getConstructor().newInstance();
            for (var field : configClass.getDeclaredFields()) {
                String key = field.getName();
                var annotation = field.getAnnotation(ConfigProperty.class);
                if (annotation != null) {
                    key = annotation.value();
                }
                Class<?> propertyClass = field.getType();
                field.setAccessible(true);
                field.set(instance, get(key,propertyClass).orElse(null));
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new ConfigException("Cannot create a new instance of "+configClass, e);
        }
    }
}
